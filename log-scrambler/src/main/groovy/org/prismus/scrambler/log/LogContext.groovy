package org.prismus.scrambler.log

import groovy.transform.CompileStatic
import groovy.transform.PackageScope
import groovy.util.logging.Log
import org.apache.commons.lang3.StringUtils
import org.codehaus.groovy.control.CompilerConfiguration
import org.codehaus.groovy.control.customizers.ImportCustomizer
import org.springframework.context.ApplicationContext

import java.nio.file.FileVisitOption
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths
import java.nio.file.attribute.BasicFileAttributes
import java.text.SimpleDateFormat
import java.util.concurrent.*
import java.util.concurrent.atomic.AtomicInteger
import java.util.function.Predicate
import java.util.logging.Level
import java.util.regex.Pattern
import java.util.stream.Collectors

/**
 * @author Serge Pruteanu
 */
@CompileStatic
@Log
class LogContext implements Iterable<LogEntry> {
    private Map<LogEntry, LogConsumer> sourceConsumerMap = [:]
    private List<LogConsumer> consumers = new ArrayList<LogConsumer>()
    private List<Closeable> closeables = []

    private CompletionService completionService
    private int asynchTimeout
    private TimeUnit asynchUnit
    private AtomicInteger jobsCount
    boolean processContext = true

    boolean multiline = true

    private LogContext() {
    }

    LogContext oneLineEntry() {
        multiline = false
        return this
    }

    LogContext withExecutorService(ExecutorService executorService, int timeout = 0, TimeUnit unit = TimeUnit.MILLISECONDS) {
        this.asynchUnit = unit
        this.asynchTimeout = timeout
        if (executorService) {
            completionService = new ExecutorCompletionService<Void>(executorService)
            jobsCount = new AtomicInteger()
        }
        return this
    }

    LogContext addSource(LogEntry source, LogConsumer sourceConsumer) {
        sourceConsumerMap.put(source, sourceConsumer)
        return this
    }

    LogContext addConsumer(LogConsumer consumer) {
        consumers.add(consumer)
        if (consumer instanceof Closeable) {
            closeables.add(consumer as Closeable)
        }
        if (consumer instanceof CsvWriterConsumer) {
            final csvOutputConsumer = (CsvWriterConsumer) consumer
            if (!csvOutputConsumer.columns) {
                for (LogConsumer sourceConsumer : sourceConsumerMap.values()) {
                    if (sourceConsumer instanceof RegexConsumer) {
                        csvOutputConsumer.columns = ((RegexConsumer) sourceConsumer).groupIndexMap.keySet().toList()
                    }
                }
                if (!csvOutputConsumer.columns) {
                    throw new RuntimeException('Columns are not defined for CsvOutputConsumer')
                }
            }
        }
        return this
    }

    LogContext predicateConsumer(LogConsumer consumer, Predicate predicate) {
        return addConsumer(new PredicateConsumer(consumer, predicate))
    }

    protected LogEntry consumeEntry(LogEntry entry) {
        for (LogConsumer consumer : consumers) {
            consumer.consume(entry)
        }
        return entry
    }

    protected void awaitJobsCompletion() {
        if (jobsCount == null) {
            return
        }
        List<Throwable> errors = []
        while (jobsCount.get()) {
            Future<Void> future
            if (asynchTimeout) {
                future = completionService.poll(asynchTimeout, asynchUnit)
            } else {
                future = completionService.poll()
            }
            if (future) {
                try {
                    future.get()
                } catch (ExecutionException ignore) {
                    errors.add(ignore.getCause())
                } finally {
                    jobsCount.decrementAndGet()
                }
            }
        }
        if (errors) {
            throw new ContextException('Failed to execute asychnronous jobs', errors)
        }
    }

    protected void consumeEntry(LogConsumer sourceConsumer, LogEntry lastEntry) {
        if (lastEntry) {
            sourceConsumer.consume(lastEntry)
            consumeEntry(lastEntry)
        }
    }

    protected void consumeSource(LineReader lineReader, String sourceName, LogConsumer sourceConsumer) {
        try {
            LogEntry lastEntry = null
            int currentRow = 0
            String line
            if (sourceName) {
                log.log(Level.INFO, "Consuming '$sourceName' using: '$sourceConsumer'")
            }
            while ((line = lineReader.readLine()) != null) {
                final logEntry = new LogEntry(sourceName, line, ++currentRow)
                sourceConsumer.consume(logEntry)
                if (logEntry.isEmpty()) {
                    if (multiline && lastEntry) {
                        lastEntry.line += LineReader.LINE_BREAK + line
                    }
                } else {
                    consumeEntry(sourceConsumer, lastEntry)
                    lastEntry = logEntry
                }
            }
            consumeEntry(sourceConsumer, lastEntry)
            if (sourceName) {
                log.log(Level.INFO, "Done consuming '$sourceName' using: '$sourceConsumer'")
            }
        } finally {
            Utils.closeQuietly(lineReader)
        }
    }

    protected void closeConsumers() {
        for (Closeable closeable : closeables) {
            Utils.closeQuietly(closeable)
        }
    }

    void consume() {
        for (final entry : sourceConsumerMap.entrySet()) {
            consumeSource(LineReader.toLineReader(entry.key), LineReader.getSourceName(entry.key), entry.value)
        }
        awaitJobsCompletion()
        closeConsumers()
    }

    protected Future submitAsynchronous(Callable<Void> callable) {
        final work = completionService.submit(callable)
        jobsCount.incrementAndGet()
        try {
            work.get(1, TimeUnit.MILLISECONDS) // make sure that job is submitted
        } catch (TimeoutException ignore) { }
        return work
    }

    protected Future submitAsynchronous(LogConsumer consumer, LogEntry logEntry) {
        return submitAsynchronous(new LogConsumerCallable(consumer, logEntry))
    }

    @Override
    Iterator<LogEntry> iterator() {
        if (sourceConsumerMap.isEmpty()) {
            throw new RuntimeException('No sources defined to iterate thru')
        }
        return new LogEntryIterator()
    }

//    Stream<LogEntry> stream() {
//        throw new UnsupportedOperationException()
////        return StreamSupport.stream(Spliterators.spliteratorUnknownSize(iterator(), 0), false)
//    }

    private class LogEntryIterator implements Iterator<LogEntry> {
        private Queue<Tuple> sources = new LinkedList<>()

        private LineReader lineReader
        private LogConsumer sourceConsumer
        private String sourceName
        private LogEntry lastEntry
        private int currentRow

        LogEntryIterator() {
            for (Map.Entry<LogEntry, LogConsumer> entry : sourceConsumerMap.entrySet()) {
                sources.add(new Tuple(LineReader.toLineReader(entry.key), LineReader.getSourceName(entry.key), entry.value))
            }
        }

        protected void openNextSource() {
            lastEntry = null
            currentRow = 0
            if (sources.size()) {
                final tuple = sources.poll()
                lineReader = tuple.get(0) as LineReader
                sourceName = tuple.get(1)
                sourceConsumer = tuple.get(2) as LogConsumer
                doNext(true)
            }
        }

        @Override
        boolean hasNext() {
            if (lastEntry == null && sources.size() > 0) {
                openNextSource()
            }
            boolean result = lastEntry != null
            if (!result) {
                LogContext.this.closeConsumers()
            }
            return result
        }

        protected LogEntry doNext(boolean sourceOpen = false) {
            LogEntry result = null
            String line = null
            while (result == null && (line = lineReader.readLine()) != null) {
                final logEntry = new LogEntry(sourceName, line, ++currentRow)
                sourceConsumer.consume(logEntry)
                if (logEntry.isEmpty()) {
                    if (multiline && lastEntry) {
                        lastEntry.line += LineReader.LINE_BREAK + line
                    }
                } else {
                    consumeEntry(sourceConsumer, lastEntry)
                    result = sourceOpen ? logEntry : lastEntry
                    lastEntry = logEntry
                }
            }
            if (result == null) {
                consumeEntry(sourceConsumer, lastEntry)
                result = lastEntry
                if (line == null) {
                    Utils.closeQuietly(lineReader)
                    lastEntry = null
                }
            }
            return result
        }

        @Override
        LogEntry next() {
            return doNext()
        }
    }

    private class LogConsumerCallable implements Callable<Void> {
        final LogConsumer consumer
        final LogEntry logEntry

        LogConsumerCallable(LogConsumer consumer, LogEntry logEntry) {
            this.consumer = consumer
            this.logEntry = logEntry
        }

        @Override
        Void call() throws Exception {
            consumer.consume(logEntry)
            return null
        }
    }

    private static class ContextException extends RuntimeException implements Iterable<Throwable> {
        private List<Throwable> throwables

        ContextException(String message, List<Throwable> throwables) {
            super(message)
            this.throwables = throwables
        }

        @Override
        Iterator<Throwable> iterator() {
            return throwables.iterator()
        }

    }

    static final Comparator<Path> CREATED_DT_COMPARATOR = { Path l, Path r ->
        final leftCreated = Files.readAttributes(l, BasicFileAttributes.class).creationTime()
        final rightCreated = Files.readAttributes(r, BasicFileAttributes.class).creationTime()
        return leftCreated.compareTo(rightCreated)
    } as Comparator<Path>

    protected static String fileFilterToRegex(String fileFilter) {
        String result = fileFilter.replaceAll('\\*', '.*')
        result = result.replaceAll('\\?', '.')
        result = result.replaceAll('\\\\', '\\\\')
        return result
    }

    protected static List<File> listFolderFiles(File folder, String fileFilter = '*', Comparator<Path> fileSorter = CREATED_DT_COMPARATOR) {
        log.log(Level.INFO, "Scanning '$folder.path' for logging sources using: '$fileFilter' filter")
        final Pattern filePattern = ~/${fileFilterToRegex(fileFilter)}/
        final results = Files.find(Paths.get(folder.toURI()), 999,
                { Path p, BasicFileAttributes bfa -> bfa.isRegularFile() && filePattern.matcher(p.getFileName().toString()).matches() }, FileVisitOption.FOLLOW_LINKS
        ).sorted(fileSorter).map({ it.toFile() }).collect(Collectors.toList())
        if (results) {
            log.log(Level.INFO, "Found '${results.size()}' files in '$folder.path'")
        } else {
            log.log(Level.WARNING, "No files found in '$folder.path' using '$fileFilter' filter")
        }
        return results
    }

    /**
     * @author Serge Pruteanu
     */
    @CompileStatic
    static class Builder {
        ObjectProvider provider = new DefaultObjectProvider()

        private LogContext context
        private final Map<LogEntry, Object> sourceConsumerMap = [:]
        private final List<ConsumerBuilder> consumerBuilders = []

        private boolean asynchronousSources
        private ExecutorService executorService
        private int defaultTimeout
        private TimeUnit defaultUnit = TimeUnit.MILLISECONDS

        Builder() {
            context = new LogContext()
        }

        @PackageScope
        LogConsumer checkAsynchronousConsumer(LogConsumer result) {
            return (asynchronousSources ? newAsynchronousConsumer(result) : result)
        }

        @PackageScope
        void buildSourceConsumers() {
            final List<String> sourceNames = new ArrayList<>()
            for (Map.Entry<LogEntry, Object> entry : sourceConsumerMap.entrySet()) {
                final value = entry.value
                LogConsumer sourceConsumer = null
                if (value instanceof LogConsumer) {
                    sourceConsumer = value as LogConsumer
                } else if (value instanceof ConsumerBuilder) {
                    sourceConsumer = value.build()
                }
                if (sourceConsumer) {
                    final logEntry = entry.key
                    context.addSource(logEntry, checkAsynchronousConsumer(sourceConsumer))
                    final sourceName = LineReader.getSourceName(logEntry)
                    if (sourceName) {
                        sourceNames.add(sourceName)
                    }
                }
            }
            int difference = StringUtils.indexOfDifference(sourceNames.toArray() as String[])
            if (difference > 0) {
                for (LogEntry logEntry : sourceConsumerMap.keySet()) {
                    final sourceName = LineReader.getSourceName(logEntry)
                    if (sourceName) {
                        int idx = indexOfLastFolderSeparator(sourceName)
                        difference = Math.min(difference, indexOfLastFolderSeparator(sourceName, difference))
                        LineReader.addSourceName(logEntry, sourceName.substring(Math.min(idx, difference)))
                    }
                }
            }
        }

        protected static int indexOfLastFolderSeparator(String sourceName, int sidx = -1) {
            if (sidx < 0) {
                sidx = sourceName.length()
            }
            def ch = '\\'
            int idx = sourceName.lastIndexOf(ch, sidx)
            if (idx < 0) {
                ch = '/'
                idx = sourceName.lastIndexOf(ch, sidx)
            }
            if (idx < 0) {
                idx = sidx
            } else {
                idx++
            }
            return idx
        }

        @PackageScope
        void buildLogEntryConsumers() {
            for (ConsumerBuilder builder : consumerBuilders) {
                context.addConsumer(builder.build())
            }
        }

        protected AsynchronousProxyConsumer newAsynchronousConsumer(LogConsumer consumer, int timeout = 0, TimeUnit unit = TimeUnit.MILLISECONDS) {
            if (executorService == null) {
                executorService = Executors.newCachedThreadPool(new ContextThreadFactory())
                context.withExecutorService(executorService, defaultTimeout, defaultUnit)
            }
            return consumer instanceof AsynchronousProxyConsumer ? consumer as AsynchronousProxyConsumer : new AsynchronousProxyConsumer(context, consumer).awaitConsumption(timeout, unit)
        }

        LogConsumer getConsumer(Object processorId, Object... args) {
            return provider.get(processorId, args) as LogConsumer
        }

        Builder asynchronousSources(int defaultTimeout = this.defaultTimeout, TimeUnit defaultUnit = this.defaultUnit) {
            asynchronousSources = true
            this.defaultTimeout = defaultTimeout
            this.defaultUnit = defaultUnit
            return this
        }

        Builder withObjectProvider(ObjectProvider provider) {
            this.provider = provider
            return this
        }

        Builder withSpringObjectProvider(ApplicationContext context) {
            this.provider = SpringObjectProvider.of(context)
            return this
        }

        Builder withSpringObjectProvider(Class... contextClass) {
            this.provider = SpringObjectProvider.of(contextClass)
            return this
        }

        Builder withSpringObjectProvider(String... contextXml) {
            this.provider = SpringObjectProvider.of(contextXml)
            return this
        }

        Builder withConsumer(LogConsumer consumer) {
            consumerBuilders.add(new ConsumerBuilder().forConsumer(consumer))
            return this
        }

        Builder asynchronousConsumer(LogConsumer consumer) {
            context.addConsumer(newAsynchronousConsumer(consumer))
            return this
        }

        Builder csvWriter(Writer writer, String... columns) {
            return withConsumer(CsvWriterConsumer.of(writer, columns))
        }

        Builder csvWriter(File file, String... columns) {
            return withConsumer(CsvWriterConsumer.of(file, columns))
        }

        Builder csvWriter(String filePath, String... columns) {
            return withConsumer(CsvWriterConsumer.of(filePath, columns))
        }

        ConsumerBuilder csvWriterBuilder(Writer writer, String... columns) {
            final builder = new ConsumerBuilder(this, CsvWriterConsumer.of(writer, columns))
            consumerBuilders.add(builder)
            return builder
        }

        ConsumerBuilder csvWriterBuilder(File file, String... columns) {
            final builder = new ConsumerBuilder(this, CsvWriterConsumer.of(file, columns))
            consumerBuilders.add(builder)
            return builder
        }

        ConsumerBuilder csvWriterBuilder(String filePath, String... columns) {
            final builder = new ConsumerBuilder(this, CsvWriterConsumer.of(filePath, columns))
            consumerBuilders.add(builder)
            return builder
        }

        Builder dateFormatConsumer(SimpleDateFormat dateFormat, String group = DateFormatConsumer.TIMESTAMP) {
            return withConsumer(DateFormatConsumer.of(dateFormat, group))
        }

        Builder dateFormatConsumer(String dateFormat, String group = DateFormatConsumer.TIMESTAMP) {
            return withConsumer(DateFormatConsumer.of(dateFormat, group))
        }

        protected void addSource(LogEntry logEntry) {
            sourceConsumerMap.put(logEntry, null)
        }

        Builder logSource(RandomAccessFile rf, String sourceName = null) {
            addSource(LineReader.newLogSource(rf, sourceName))
            return this
        }

        Builder logSource(InputStream inputStream, String sourceName = null) {
            addSource(LineReader.newLogSource(inputStream, sourceName))
            return this
        }

        Builder logSource(Reader reader, String sourceName = null) {
            addSource(LineReader.newLogSource(reader, sourceName))
            return this
        }

        Builder logSource(File file, String sourceName = null) {
            addSource(LineReader.newLogSource(file, sourceName ?: file.path))
            return this
        }

        Builder logSource(String content, String sourceName = null) {
            addSource(LineReader.newLogSource(content, sourceName))
            return this
        }

        protected Builder sourceConsumer(Object sourceConsumer) {
            for (LogEntry source : sourceConsumerMap.keySet()) {
                final sc = sourceConsumerMap.get(source)
                if (sc == null) {
                    sourceConsumerMap.put(source, sourceConsumer)
                }
            }
            return this
        }

        RegexConsumerBuilder sourceRegexConsumer(Pattern pattern) {
            final builder = new RegexConsumerBuilder(this, RegexConsumer.of(pattern))
            sourceConsumer(builder)
            return builder
        }

        RegexConsumerBuilder sourceRegexConsumer(String regEx, int flags = 0) {
            final builder = new RegexConsumerBuilder(this, RegexConsumer.of(regEx, flags))
            sourceConsumer(builder)
            return builder
        }

        Log4jConsumerBuilder sourceLog4jConsumer(String conversionPattern) {
            final builder = new Log4jConsumerBuilder(this, Log4jConsumer.ofPattern(conversionPattern))
            sourceConsumer(builder)
            return builder
        }

        RegexConsumerBuilder regexSourceFolder(File folder, Pattern pattern,
                                               String fileFilter = '*', Comparator<Path> fileSorter = CREATED_DT_COMPARATOR) {
            final files = listFolderFiles(folder, fileFilter, fileSorter)
            final builder = sourceRegexConsumer(pattern)
            for (File file : files) {
                sourceConsumerMap.put(LineReader.newLogSource(new LogEntry(source: folder), file.path), builder)
            }
            return builder
        }

        Log4jConsumerBuilder log4jSourceFolder(File folder, String conversionPattern,
                                               String fileFilter = '*', Comparator<Path> fileSorter = CREATED_DT_COMPARATOR) {
            final files = listFolderFiles(folder, fileFilter, fileSorter)
            final builder = sourceLog4jConsumer(conversionPattern)
            for (File file : files) {
                sourceConsumerMap.put(LineReader.newLogSource(new LogEntry(source: file), file.path), builder)
            }
            return builder
        }

        Log4jConsumerBuilder log4jSourceFolder(String folder, String conversionPattern,
                                               String fileFilter = '*', Comparator<Path> fileSorter = CREATED_DT_COMPARATOR) {
            return log4jSourceFolder(new File(folder), conversionPattern, fileFilter, fileSorter)
        }

        Builder sourceFolder(String path, LogConsumer consumer,
                             String fileFilter = '*', Comparator<Path> fileSorter = CREATED_DT_COMPARATOR) {
            final folder = new File(path)
            final files = listFolderFiles(folder, fileFilter, fileSorter)
            final builder = withConsumer(consumer)
            for (File file : files) {
                sourceConsumerMap.put(LineReader.newLogSource(new LogEntry(source: folder), file.path), builder)
            }
            return builder
        }

        LogContext build() {
            context.withExecutorService(executorService, defaultTimeout, defaultUnit)
            buildSourceConsumers()
            buildLogEntryConsumers()
            sourceConsumerMap.clear()
            consumerBuilders.clear()
            return context
        }

        private Builder init(String... args) {
            if (!args) {
                return this
            }
            final unknownArgs = []
            for (String arg : args) {
                if (arg.endsWith('groovy')) {
                    initGroovyScriptBuilder(this, loadResourceText(arg))
                } else {
                    unknownArgs.add(arg)
                }
            }
            if (unknownArgs) {
                throw new IllegalArgumentException("Unsupported/unknown arguments: '${unknownArgs.join(', ')}'")
            }
            return this
        }

        static class ContextThreadFactory implements ThreadFactory {
            private AtomicInteger count

            @Override
            Thread newThread(Runnable r) {
                final thread = new Thread(r)
                thread.setDaemon(true)
                thread.setName("LogContextThread${count.incrementAndGet()}")
                return thread
            }
        }
    }

    private static GroovyShell checkCreateShell(Properties parserProperties = new Properties()) {
        final compilerConfiguration = (parserProperties != null && parserProperties.size() > 0) ? new CompilerConfiguration(parserProperties) : new CompilerConfiguration()
        compilerConfiguration.setScriptBaseClass(DelegatingScript.name)

        final importCustomizer = new ImportCustomizer()
        importCustomizer.addStarImports(LogContext.package.name)
        compilerConfiguration.addCompilationCustomizers(importCustomizer)

        return new GroovyShell(compilerConfiguration)
    }

    private static String loadResourceText(String resource) {
        final String text
        if (resource.endsWith('groovy')) {
            final URL url = LogContext.getResource(resource)
            if (url == null) {
                final file = new File(resource)
                if (!file.exists()) {
                    throw new IllegalArgumentException(String.format("Not found resource for: %s", resource))
                } else {
                    text = file.text
                }
            } else {
                text = url.text
            }
        } else {
            text = resource
        }
        return text
    }

    private static Builder initGroovyScriptBuilder(Builder builder, String definitionText) {
        final shell = checkCreateShell()
        final script = (DelegatingScript) shell.parse(definitionText)
        script.setDelegate(builder)
        script.run()
        return builder
    }

    static Builder builder(String... args) {
        return new Builder().init(args)
    }

}
