package org.prismus.scrambler.log

import groovy.transform.CompileStatic
import groovy.transform.PackageScope
import groovy.util.logging.Log
import org.apache.commons.lang3.StringUtils
import org.codehaus.groovy.control.CompilerConfiguration
import org.codehaus.groovy.control.customizers.ImportCustomizer
import org.codehaus.groovy.runtime.InvokerHelper
import org.springframework.context.ApplicationContext

import javax.sql.DataSource
import java.nio.file.FileVisitOption
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths
import java.nio.file.attribute.BasicFileAttributes
import java.text.SimpleDateFormat
import java.util.concurrent.*
import java.util.concurrent.atomic.AtomicInteger
import java.util.function.Predicate
import java.util.regex.Pattern
import java.util.stream.Collectors

/**
 * @author Serge Pruteanu
 */
@CompileStatic
@Log
class LogCrawler implements Iterable<LogEntry> {
    protected static final String LOG4J_ARG = '-log4j'
    protected static final String REGEX_ARG = '-regex'

    private Map<LogEntry, LogConsumer> sourceConsumerMap = [:]
    private List<LogConsumer> consumers = new ArrayList<LogConsumer>()
    private List<Closeable> closeables = []

    private CompletionService completionService
    private int asynchTimeout
    private TimeUnit asynchUnit
    private AtomicInteger jobsCount
    volatile boolean processContext = true

    boolean multiline = true

    private LogCrawler() {
    }

    LogCrawler oneLineEntry() {
        multiline = false
        return this
    }

    LogCrawler forSource(LogEntry source, LogConsumer sourceConsumer) {
        sourceConsumerMap.put(source, sourceConsumer)
        return this
    }

    @SuppressWarnings("GroovySynchronizationOnNonFinalField")
    LogCrawler stopAsynchronous() {
        processContext = false
        synchronized (completionService) {
            completionService.notifyAll()
        }
        return this
    }

    LogCrawler withExecutorService(ExecutorService executorService, int timeout = 0, TimeUnit unit = TimeUnit.MILLISECONDS) {
        this.asynchUnit = unit
        this.asynchTimeout = timeout
        if (executorService) {
            completionService = new ExecutorCompletionService<Void>(executorService)
            jobsCount = new AtomicInteger()
        }
        return this
    }

    LogCrawler withConsumer(LogConsumer consumer) {
        consumers.add(consumer)
        if (consumer instanceof Closeable) {
            closeables.add(consumer as Closeable)
        }
        if (consumer instanceof CsvWriterConsumer) {
            final csvOutputConsumer = (CsvWriterConsumer) consumer
            if (!csvOutputConsumer.columns) {
                final columns = new LinkedHashSet<String>()
                for (LogConsumer sourceConsumer : sourceConsumerMap.values()) {
                    if (sourceConsumer instanceof RegexConsumer) {
                        columns.addAll(((RegexConsumer) sourceConsumer).groupIndexMap.keySet().toList())
                    }
                }
                if (!columns) {
                    throw new RuntimeException('Columns are not defined for CsvOutputConsumer')
                }
                csvOutputConsumer.columns = columns.toList()
            }
        }
        return this
    }

    LogCrawler filterTo(Predicate predicate, LogConsumer consumer, LogConsumer... consumers) {
        def cs = consumers ? ContainerConsumer.of(consumer).addAll(consumers) : consumer
        return withConsumer(new PredicateConsumer(predicate, cs))
    }

    LogCrawler filterTo(Closure predicate, Closure consumer, Closure... consumers) {
        def cs = consumers ? ContainerConsumer.of(consumer).addAll(consumers) : new ClosureConsumer(consumer)
        return filterTo(new ClosurePredicate(predicate), cs)
    }

    LogCrawler writeTo(DataSource dataSource, String tableName = 'LogEntry', String... columns) {
        return withConsumer(TableBatchConsumer.of(dataSource, tableName, columns))
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
        while (processContext && jobsCount.get()) {
            try {
                Future<Void> future
                if (asynchTimeout) {
                    future = completionService.poll(asynchTimeout, asynchUnit)
                } else {
                    future = completionService.poll()
                }
                if (future) {
                    try {
                        if (asynchTimeout) {
                            future.get(asynchTimeout, asynchUnit)
                        } else {
                            future.get()
                        }
                    } catch (ExecutionException ignore) {
                        errors.add(ignore.getCause())
                    } finally {
                        jobsCount.decrementAndGet()
                    }
                }
            } catch (TimeoutException ignore) { }
        }
        if (errors) {
            throw new ContextException('Failed to execute asynchronous jobs', errors)
        }
    }

    protected void consumeEntry(LogConsumer sourceConsumer, LogEntry logEntry) {
        if (logEntry) {
            logEntry.sourceInfo("${Objects.toString(logEntry.source, '')}:($logEntry.row)".toString())
            sourceConsumer.consume(logEntry)
            consumeEntry(logEntry)
        }
    }

    protected void consumeSource(LineReader lineReader, String sourceName, LogConsumer sourceConsumer) {
        try {
            LogEntry lastEntry = null
            int currentRow = 0
            int nEntries = 0
            String line
            log.finest("Consuming '$sourceName' using: '$sourceConsumer'")
            while ((line = lineReader.readLine()) != null) {
                final logEntry = new LogEntry(sourceName, line, ++currentRow)
                sourceConsumer.consume(logEntry)
                if (logEntry.isEmpty()) {
                    if (multiline && lastEntry) {
                        lastEntry.line += LineReader.LINE_BREAK + line
                    }
                } else {
                    nEntries++
                    consumeEntry(sourceConsumer, lastEntry)
                    lastEntry = logEntry
                }
            }
            consumeEntry(sourceConsumer, lastEntry)
            log.finest("Done consuming '$sourceName' using: '$sourceConsumer'. Processed '$currentRow' rows, consumed '$nEntries' entries")
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
        closeConsumers()
    }

//    todo fix asynch
    private void consumeAsynchronous() {
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
        private int nEntries

        LogEntryIterator() {
            for (Map.Entry<LogEntry, LogConsumer> entry : sourceConsumerMap.entrySet()) {
                sources.add(new Tuple(LineReader.toLineReader(entry.key), LineReader.getSourceName(entry.key), entry.value))
            }
        }

        protected void openNextSource() {
            lastEntry = null
            currentRow = nEntries = 0
            if (sources.size()) {
                final tuple = sources.poll()
                lineReader = tuple.get(0) as LineReader
                sourceName = tuple.get(1)
                sourceConsumer = tuple.get(2) as LogConsumer
                log.finest("Consuming '$sourceName' using: '$sourceConsumer'")
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
                LogCrawler.this.closeConsumers()
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
                    nEntries++
                    consumeEntry(sourceConsumer, lastEntry)
                    result = sourceOpen ? logEntry : lastEntry
                    lastEntry = logEntry
                }
            }
            if (result == null) {
                consumeEntry(sourceConsumer, lastEntry)
                result = lastEntry
                if (line == null) {
                    log.finest("Done consuming '$sourceName' using: '$sourceConsumer'. Processed '$currentRow' rows, consumed '$nEntries' entries")
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
        assert folder.exists() && folder.isDirectory(), "Folder: '$folder.path' doesn't exists"
        log.finest("Scanning '$folder.path' for logging sources using: '$fileFilter' filter")
        final Pattern filePattern = ~/${fileFilterToRegex(fileFilter)}/
        final results = Files.find(Paths.get(folder.toURI()), 999,
                { Path p, BasicFileAttributes bfa -> bfa.isRegularFile() && filePattern.matcher(p.getFileName().toString()).matches() }, FileVisitOption.FOLLOW_LINKS
        ).sorted(fileSorter).map({ it.toFile() }).collect(Collectors.toList())
        if (results) {
            log.finest("Found '${results.size()}' files in '$folder.path'")
        } else {
            log.finest("No files found in '$folder.path' using '$fileFilter' filter")
        }
        return results
    }

    /**
     * @author Serge Pruteanu
     */
    @CompileStatic
    static class Builder {
        ObjectProvider provider = new DefaultObjectProvider()

        private LogCrawler context
        private final Map<String, Object> sourceNameConsumerMap = [:]
        private final Map<LogEntry, Object> sourceConsumerMap = [:]
        private final List<ConsumerBuilder> consumerBuilders = []

        private boolean asynchronousSources
        private ExecutorService executorService
        private int defaultTimeout
        private TimeUnit defaultUnit = TimeUnit.MILLISECONDS

        Builder() {
            context = new LogCrawler()
        }

        @PackageScope
        LogConsumer checkAsynchronousConsumer(LogConsumer result) {
            return (asynchronousSources ? newAsynchronousConsumer(result) : result)
        }

        @PackageScope
        void buildSourceConsumers() {
            final List<String> sourceNames = new ArrayList<>()
            final Map<Object, LogConsumer> builtConsumers = [:]
            for (Map.Entry<LogEntry, Object> entry : sourceConsumerMap.entrySet()) {
                final value = entry.value
                LogConsumer sourceConsumer = null
                if (value instanceof LogConsumer) {
                    sourceConsumer = value as LogConsumer
                } else if (value instanceof ConsumerBuilder) {
                    if (builtConsumers.containsKey(value)) {
                        sourceConsumer = builtConsumers.get(value)
                    } else {
                        sourceConsumer = value.build()
                        builtConsumers.put(value, sourceConsumer)
                    }
                }
                if (sourceConsumer) {
                    final logEntry = entry.key
                    context.forSource(logEntry, checkAsynchronousConsumer(sourceConsumer))
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
            return idx < 0 ? sidx : idx + 1
        }

        @PackageScope
        void buildConsumers() {
            for (ConsumerBuilder builder : consumerBuilders) {
                context.withConsumer(builder.build())
            }
        }

        protected AsynchronousProxyConsumer newAsynchronousConsumer(LogConsumer consumer, int timeout = 0, TimeUnit unit = TimeUnit.MILLISECONDS) {
            if (executorService == null) {
                executorService = Executors.newCachedThreadPool(new ContextThreadFactory())
                context.withExecutorService(executorService, defaultTimeout, defaultUnit)
            }
            return consumer instanceof AsynchronousProxyConsumer ? consumer as AsynchronousProxyConsumer : new AsynchronousProxyConsumer(context, consumer).awaitConsumption(timeout, unit)
        }

        LogConsumer getConsumer(Object consumerId, Object... args) {
            return provider.get(consumerId, args) as LogConsumer
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
            consumerBuilders.add(new ConsumerBuilder().withConsumer(consumer))
            return this
        }

        Builder withConsumer(Closure logEntryClosure) {
            return withConsumer(new ClosureConsumer(logEntryClosure))
        }

        Builder asynchronous(LogConsumer consumer) {
            context.withConsumer(newAsynchronousConsumer(consumer))
            return this
        }

        Builder filterTo(Predicate predicate, LogConsumer consumer, LogConsumer... consumers) {
            def cs = consumers ? ContainerConsumer.of(consumer).addAll(consumers) : consumer
            return withConsumer(new PredicateConsumer(predicate, cs))
        }

        Builder filterTo(Closure predicate, Closure consumer, Closure... consumers) {
            def cs = consumers ? ContainerConsumer.of(consumer).addAll(consumers) : new ClosureConsumer(consumer)
            return filterTo(new ClosurePredicate(predicate), cs)
        }

        Builder writeTo(DataSource dataSource, String tableName = 'LogEntry', String... columns) {
            return withConsumer(TableBatchConsumer.of(dataSource, tableName, columns))
        }

        TableBatchConsumer.Builder tableBatchBuilder(DataSource dataSource, String tableName = 'LogEntry', String... columns) {
            final builder = new TableBatchConsumer.Builder(this, TableBatchConsumer.of(dataSource, tableName, columns))
            consumerBuilders.add(builder)
            return builder
        }

        Builder writerToCsv(Writer writer, String... columns) {
            return withConsumer(CsvWriterConsumer.of(writer, columns))
        }

        Builder writerToCsv(File file, String... columns) {
            return withConsumer(CsvWriterConsumer.of(file, columns))
        }

        Builder writerToCsv(String filePath, String... columns) {
            return withConsumer(CsvWriterConsumer.of(filePath, columns))
        }

        CsvWriterConsumer.Builder csvWriterBuilder(Writer writer, String... columns) {
            final builder = new CsvWriterConsumer.Builder(this, CsvWriterConsumer.of(writer, columns))
            consumerBuilders.add(builder)
            return builder
        }

        CsvWriterConsumer.Builder csvWriterBuilder(File file, String... columns) {
            final builder = new CsvWriterConsumer.Builder(this, CsvWriterConsumer.of(file, columns))
            consumerBuilders.add(builder)
            return builder
        }

        CsvWriterConsumer.Builder csvWriterBuilder(String filePath, String... columns) {
            final builder = new CsvWriterConsumer.Builder(this, CsvWriterConsumer.of(filePath, columns))
            consumerBuilders.add(builder)
            return builder
        }

        Builder toDateConsumer(SimpleDateFormat dateFormat, String group = DateConsumer.DATE) {
            return withConsumer(DateConsumer.of(dateFormat, group))
        }

        Builder toDateConsumer(String dateFormat, String group = DateConsumer.DATE) {
            return withConsumer(DateConsumer.of(dateFormat, group))
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

        protected void registerSourceConsumer(LogEntry sourceEntry, Object sourceConsumer, String sourceName = null) {
            sourceConsumerMap.put(sourceEntry, sourceConsumer)
            if (sourceName) {
                sourceNameConsumerMap.put(sourceName, sourceConsumer)
            }
        }

        protected Builder sourceConsumer(Object sourceConsumer, String sourceName = null) {
            for (LogEntry sourceEntry : sourceConsumerMap.keySet()) {
                final sc = sourceConsumerMap.get(sourceEntry)
                if (sc == null) {
                    registerSourceConsumer(sourceEntry, sourceConsumer, sourceName)
                }
            }
            return this
        }

        RegexConsumer.Builder sourceRegexConsumer(Pattern pattern) {
            final builder = new RegexConsumer.Builder(this, RegexConsumer.of(pattern))
            sourceConsumer(builder, pattern.pattern())
            return builder
        }

        RegexConsumer.Builder sourceRegexConsumer(String regEx, int flags = 0) {
            final builder = new RegexConsumer.Builder(this, RegexConsumer.of(regEx, flags))
            sourceConsumer(builder, regEx)
            return builder
        }

        Log4jConsumer.Builder sourceLog4jConsumer(String conversionPattern) {
            final builder = new Log4jConsumer.Builder(this, Log4jConsumer.of(conversionPattern))
            sourceConsumer(builder, conversionPattern)
            return builder
        }

        protected void sourceFolder(ConsumerBuilder builder, String sourceName, File folder, String fileFilter, Comparator<Path> fileSorter) {
            final files = listFolderFiles(folder, fileFilter, fileSorter)
            for (File file : files) {
                final logEntry = LineReader.newLogSource(new LogEntry(source: file), file.path)
                registerSourceConsumer(logEntry, builder, sourceName)
                registerSourceConsumer(logEntry, builder, fileFilter)
            }
        }

        RegexConsumer.Builder regexSourceFolder(File folder, Pattern pattern,
                                                String fileFilter = '*', Comparator<Path> fileSorter = CREATED_DT_COMPARATOR) {
            final builder = sourceRegexConsumer(pattern)
            sourceFolder(builder, pattern.pattern(), folder, fileFilter, fileSorter)
            return builder
        }

        Log4jConsumer.Builder log4jSourceFolder(File folder, String conversionPattern,
                                                String fileFilter = '*', Comparator<Path> fileSorter = CREATED_DT_COMPARATOR) {
            final builder = sourceLog4jConsumer(conversionPattern)
            sourceFolder(builder, conversionPattern, folder, fileFilter, fileSorter)
            return builder
        }

        Log4jConsumer.Builder log4jSourceFolder(String folder, String conversionPattern,
                                                String fileFilter = '*', Comparator<Path> fileSorter = CREATED_DT_COMPARATOR) {
            return log4jSourceFolder(new File(folder), conversionPattern, fileFilter, fileSorter)
        }

        Builder log4jConfigSource(File folder, String log4jConfig, Comparator<Path> fileSorter = CREATED_DT_COMPARATOR) {
            final log4jConsumerProperties = Log4jConsumer.extractLog4jConsumerProperties(Utils.readResourceText(log4jConfig).readLines())
            if (log4jConsumerProperties.isEmpty()) {
                throw new IllegalArgumentException("Either empty or there are no file loggers defined in '$log4jConfig'")
            }
            final filterConversionMap = Log4jConsumer.toLog4jFileConversionPattern(log4jConsumerProperties)
            for (Map.Entry<String, String> entry : filterConversionMap.entrySet()) {
                log4jSourceFolder(folder, entry.value, entry.key, fileSorter)
            }
            for (Map.Entry<String, Map<String, String>> entry : log4jConsumerProperties.entrySet()) {
                final loggerName = entry.key
                final loggerProperties = entry.value
                final log4jBuilder = getLog4jBuilder(loggerProperties.get(Log4jConsumer.APPENDER_CONVERSION_PATTERN_PROPERTY))
                if (log4jBuilder) {
                    sourceNameConsumerMap.put(loggerName, log4jBuilder)
                }
            }
            return this
        }

        Builder log4jConfigSource(String folder, String log4jConfig, Comparator<Path> fileSorter = CREATED_DT_COMPARATOR) {
            return log4jConfigSource(new File(folder), log4jConfig, fileSorter)
        }

        Builder sourceFolder(String path, LogConsumer consumer,
                             String fileFilter = '*', Comparator<Path> fileSorter = CREATED_DT_COMPARATOR) {
            final folder = new File(path)
            final files = listFolderFiles(folder, fileFilter, fileSorter)
            final builder = withConsumer(consumer)
            for (File file : files) {
                registerSourceConsumer(LineReader.newLogSource(new LogEntry(source: folder), file.path), builder)
            }
            return builder
        }

        public <T> T getSourceConsumer(String sourceName) {
            return sourceNameConsumerMap.get(sourceName) as T
        }

        RegexConsumer.Builder getRegexBuilder(String regex) {
            return getSourceConsumer(regex)
        }

        Log4jConsumer.Builder getLog4jBuilder(String sourceName) {
            return getSourceConsumer(sourceName)
        }

        LogCrawler build() {
            context.withExecutorService(executorService, defaultTimeout, defaultUnit)
            buildSourceConsumers()
            buildConsumers()
            sourceConsumerMap.clear()
            sourceNameConsumerMap.clear()
            consumerBuilders.clear()
            return context
        }

        private List toMethodTuple(String[] args, List<List> configTuple, int currentIdx, File file) {
            if (configTuple.isEmpty()) {
                throw new IllegalArgumentException("Illegal arguments provided: '${args.join(', ')}'; source type is unknown")
            }
            int i = configTuple.size() - 1
            for (; i > 0 && (configTuple.get(i)[1] as int) > currentIdx; i--) { ; }
            final String configType = configTuple.get(i)[0] as String
            final int configIdx = configTuple.get(i)[1] as int
            final configValue = args[configIdx]
            final boolean configFile = new File(configValue).exists()

            List result = null
            if (file.isDirectory()) {
                switch (configType) {
                    case LOG4J_ARG:
                        if (configFile) {
                            result = ['log4jConfigSource', [file, configValue] as Object[]]
                        } else {
                            result = ['log4jSourceFolder', [file, configValue] as Object[]]
                        }
                        break
                    case REGEX_ARG:
                        result = ['regexSourceFolder', [file, configValue] as Object[]]
                        break
                }
            } else {
                final sourceEntry = LineReader.newLogSource(new LogEntry(source: file), file.path)
                switch (configType) {
                    case LOG4J_ARG:
                        if (configFile) {
                            throw new UnsupportedOperationException("Unsupported log4j config file option: '$configValue' for a single file: '$file.path'. Only conversion pattern is supported here")
                        }
                        final builder = sourceLog4jConsumer(configValue)
                        result = ['registerSourceConsumer', [sourceEntry, builder, configValue] as Object[]]
                        break
                    case REGEX_ARG:
                        result = ['registerSourceConsumer', [sourceEntry, sourceRegexConsumer(configValue), configValue] as Object[]]
                        break
                }
            }
            if (!result) {
                throw new IllegalArgumentException("Illegal arguments provided: '${args.join(', ')}'; source type is unknown")
            }
            return result
        }

        private Builder init(String... args) {
            if (!args) {
                return this
            }
            final List<List> sourceMethodTuple = []
            final configTypes = [LOG4J_ARG, REGEX_ARG] as Set
            final List<List> configTuple = []
            final Collection<String> scripts = []
            final unknownArgs = []
            for (int i = 0; i < args.length; i++) {
                String arg = args[i];
                if (arg.endsWith('groovy')) {
                    scripts.add(arg)
                } else {
                    if (configTypes.contains(arg.toLowerCase())) {
                        if (args.length > i + 1) {
                            configTuple.add([arg, ++i])
                        } else {
                            unknownArgs.add("$arg argument must be followed by option and files to be applied")
                        }
                        continue
                    }
                    final file = new File(arg)
                    if (file.exists()) {
                        sourceMethodTuple.add(toMethodTuple(args, configTuple, i, file))
                    } else {
                        unknownArgs.add(arg)
                    }
                }
            }
            if (sourceMethodTuple.isEmpty() && configTuple.size() > 0) {
                unknownArgs.add('No source configuration options provided')
            }
            if (unknownArgs) {
                throw new IllegalArgumentException("Unsupported/unknown arguments: '${unknownArgs.join(', ')}'; arguments: '${args.join(', ')}'")
            }
            for (final i = 0; i < sourceMethodTuple.size(); i++) {
                final tuple = sourceMethodTuple.get(i)
                InvokerHelper.invokeMethod(this, tuple[0].toString(), tuple[1])
            }
            for (final script : scripts) {
                initGroovyScriptBuilder(this, Utils.readGroovyResourceText(script))
            }
            return this
        }

        static class ContextThreadFactory implements ThreadFactory {
            private final AtomicInteger count = new AtomicInteger()

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
        importCustomizer.addStarImports(LogCrawler.package.name)
        compilerConfiguration.addCompilationCustomizers(importCustomizer)

        return new GroovyShell(compilerConfiguration)
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

    private static String usage() {
        return """
Crawls files/folder based on logging consumer rules

Usage:
logCrawler [$LOG4J_ARG/$REGEX_ARG option] [sourceFiles/sourceFolders...] [<builder script>-log.groovy]

WHERE:
    [$LOG4J_ARG/$REGEX_ARG option]
        Logging configuration option; option is required before file/folder definition.
        '$LOG4J_ARG option' can be either a log4j config file (applied ONLY for folder) OR a log4j conversion pattern.
        '$REGEX_ARG option' regular expression string used to match logging entry line
    [<builder script>-log.groovy]
        Logging crawler builder Groovy configuration script.
"""
    }

    static void main(String[] args) {
        try {
            builder(args).build().consume()
        } catch (IllegalArgumentException | UnsupportedOperationException ignore) {
            System.err.println(ignore)
            usage()
        }
    }

}
