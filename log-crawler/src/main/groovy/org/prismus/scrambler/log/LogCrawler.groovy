/*
 * Log crawler, tool that allows to extract/crawl log files for further analysis
 *
 * Copyright (c) 2015, Sergiu Prutean. All rights reserved.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3.0 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library.
 */

package org.prismus.scrambler.log

import groovy.transform.CompileStatic
import groovy.transform.PackageScope
import groovy.transform.stc.ClosureParams
import groovy.transform.stc.SimpleType
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
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicBoolean
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

    protected Map<LogEntry, LogConsumer> sourceConsumerMap = [:]
    private List<LogConsumer> consumers = new ArrayList<LogConsumer>()
    private CloseableContainer closeableContainer = new CloseableContainer()

    AtomicBoolean processContext = new AtomicBoolean(true)

    boolean multiline = true

    private LogCrawler() {
    }

    LogCrawler oneLineEntry() {
        multiline = false
        return this
    }

    LogCrawler source(LogEntry source, LogConsumer sourceConsumer) {
        sourceConsumerMap.put(source, sourceConsumer)
        return this
    }

    @SuppressWarnings("GroovySynchronizationOnNonFinalField")
    LogCrawler stop() {
        processContext.set(false)
        return this
    }

    protected void checkCloseable(Object object) {
        if (object instanceof Closeable) {
            if (object instanceof CloseableContainer) {
                ((CloseableContainer) object).addAll(closeableContainer)
                closeableContainer = object as CloseableContainer
            } else {
                closeableContainer.add(object as Closeable)
            }
        }
    }

    LogCrawler withConsumer(LogConsumer consumer) {
        consumers.add(consumer)
        checkCloseable(consumer)
        if (consumer instanceof CsvWriterConsumer) {
            final csvOutputConsumer = (CsvWriterConsumer) consumer
            if (!csvOutputConsumer.columns) {
                final columns = new LinkedHashSet<String>()
                for (LogConsumer sourceConsumer : sourceConsumerMap.values()) {
                    sourceConsumer = lookupWrapped(sourceConsumer)
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

    LogCrawler filter(Predicate predicate, LogConsumer consumer, LogConsumer... consumers) {
        def cs = consumers ? ContainerConsumer.of(consumer).addAll(consumers) : consumer
        return withConsumer(new PredicateConsumer(predicate, cs))
    }

    LogCrawler filter(@DelegatesTo(LogEntry) Closure<Boolean> predicate, @DelegatesTo(LogEntry) Closure consumer, @DelegatesTo(LogEntry) Closure... consumers) {
        def cs = consumers ? ContainerConsumer.of(consumer).addAll(consumers) : new ClosureConsumer(consumer)
        return filter(new ClosurePredicate(predicate), cs)
    }

    LogCrawler output(DataSource dataSource, String tableName = 'LogEntry', String... columns) {
        return withConsumer(TableBatchConsumer.of(dataSource, tableName, columns))
    }

    protected void consume(LogEntry logEntry, LogConsumer sourceConsumer) {
        if (logEntry) {
            logEntry.sourceInfo("${Objects.toString(logEntry.source, '')}:($logEntry.row)".toString())
            sourceConsumer.consume(logEntry)
            for (LogConsumer consumer : consumers) {
                consumer.consume(logEntry)
            }
        }
    }

    protected void consume(LineReader lineReader, String sourceName, LogConsumer sourceConsumer) {
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
                    consume(lastEntry, sourceConsumer)
                    lastEntry = logEntry
                }
            }
            consume(lastEntry, sourceConsumer)
            log.finest("Done consuming '$sourceName' using: '$sourceConsumer'. Processed '$currentRow' rows, consumed '$nEntries' entries")
        } finally {
            Utils.closeQuietly(lineReader)
        }
    }

    protected void close() {
        closeableContainer.close()
    }

    void consume() {
        for (final entry : sourceConsumerMap.entrySet()) {
            entry.value.consume(entry.key)
        }
        close()
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

    static final Comparator<Path> CREATED_DT_COMPARATOR = { Path l, Path r ->
        final leftCreated = Files.readAttributes(l, BasicFileAttributes.class).creationTime()
        final rightCreated = Files.readAttributes(r, BasicFileAttributes.class).creationTime()
        return leftCreated.compareTo(rightCreated)
    } as Comparator<Path>

    private static LogConsumer lookupWrapped(LogConsumer consumer) {
        if (consumer instanceof AsynchronousJobs.AsynchronousJobConsumer) {
            consumer = ((AsynchronousJobs.AsynchronousJobConsumer) consumer).consumer
        }
        if (consumer instanceof LineReaderConsumer) {
            consumer = ((LineReaderConsumer) consumer).sourceConsumer
        }
        return consumer
    }

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
                LogConsumer sourceConsumer = lookupWrapped(entry.value)
                sources.add(new Tuple(LineReader.toLineReader(entry.key), LineReader.getSourceName(entry.key), sourceConsumer))
            }
        }

        protected void nextSource() {
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
            if (processContext && lastEntry == null && sources.size() > 0) {
                nextSource()
            }
            boolean result = processContext && lastEntry != null
            if (!result) {
                LogCrawler.this.close()
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
                    consume(lastEntry, sourceConsumer)
                    result = sourceOpen ? logEntry : lastEntry
                    lastEntry = logEntry
                }
            }
            if (result == null) {
                consume(lastEntry, sourceConsumer)
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

    @CompileStatic
    private static class LineReaderConsumer implements LogConsumer {
        private final LogCrawler logCrawler
        private final LogConsumer sourceConsumer

        LineReaderConsumer(LogCrawler logCrawler, LogConsumer sourceConsumer) {
            this.sourceConsumer = sourceConsumer
            this.logCrawler = logCrawler
        }

        @Override
        void consume(LogEntry entry) {
            logCrawler.consume(LineReader.toLineReader(entry), LineReader.getSourceName(entry), sourceConsumer)
        }
    }

    protected static String fileFilterToRegex(String fileFilter) {
        String result = fileFilter.replaceAll('\\*', '.*')
        result = result.replaceAll('\\?', '.')
        result = result.replaceAll('\\\\', '\\\\')
        return result
    }

    protected static List<File> listFiles(File folder, String fileFilter = '*', Comparator<Path> fileSorter = CREATED_DT_COMPARATOR) {
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

    protected static void checkDelegateClosure(Closure closure, def builder) {
        if (closure) {
            closure.setDelegate(builder)
            closure.setResolveStrategy(Closure.DELEGATE_ONLY)
            closure.call()
        }
    }

    /**
     * @author Serge Pruteanu
     */
    @CompileStatic
    static class Builder {
        ObjectProvider provider = new DefaultObjectProvider()

        private LogCrawler logCrawler
        private final Map<String, Object> sourceNameConsumerMap = [:]
        private final Map<LogEntry, Object> sourceMap = [:]
        private final List<ConsumerBuilder> builders = []

        Builder() {
            logCrawler = new LogCrawler()
        }

        @PackageScope
        void buildSources() {
            final List<String> sourceNames = new ArrayList<>()
            final Map<Object, LogConsumer> consumers = [:]
            for (Map.Entry<LogEntry, Object> entry : sourceMap.entrySet()) {
                final value = entry.value
                LogConsumer source = null
                if (value instanceof LogConsumer) {
                    source = value as LogConsumer
                } else if (value instanceof ConsumerBuilder) {
                    if (consumers.containsKey(value)) {
                        source = consumers.get(value)
                    } else {
                        source = value.build()
                        consumers.put(value, source)
                    }
                }
                if (source) {
                    final logEntry = entry.key
                    logCrawler.source(logEntry, new LineReaderConsumer(logCrawler, source))
                    final sourceName = LineReader.getSourceName(logEntry)
                    if (sourceName) {
                        sourceNames.add(sourceName)
                    }
                }
            }
            int difference = StringUtils.indexOfDifference(sourceNames.toArray() as String[])
            if (difference > 0) {
                for (LogEntry logEntry : sourceMap.keySet()) {
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
            for (ConsumerBuilder builder : builders) {
                final logConsumer = builder.build()
                if (logConsumer) {
                    logCrawler.withConsumer(logConsumer)
                }
            }
        }

        LogConsumer get(Object consumerId, Object... args) {
            return provider.get(consumerId, args) as LogConsumer
        }

        Builder parallel(int awaitTimeout = 0, TimeUnit unit = TimeUnit.MILLISECONDS) {
            builders.add(AsynchronousJobs.builder(logCrawler).withExecutorService(awaitTimeout, unit))
            return this
        }

        Builder provider(ObjectProvider provider) {
            this.provider = provider
            return this
        }

        Builder provider(ApplicationContext context) {
            this.provider = SpringObjectProvider.of(context)
            return this
        }

        Builder provider(Class... contextClass) {
            this.provider = SpringObjectProvider.of(contextClass)
            return this
        }

        Builder provider(String... contextXml) {
            this.provider = SpringObjectProvider.of(contextXml)
            return this
        }

        Builder withConsumer(LogConsumer consumer) {
            builders.add(new ConsumerBuilder().withConsumer(consumer))
            return this
        }

        Builder withConsumer(@ClosureParams(value=SimpleType.class, options="org.prismus.scrambler.log.LogEntry") Closure logEntryClosure) {
            return withConsumer(new ClosureConsumer(logEntryClosure))
        }

        Builder filter(Predicate predicate, LogConsumer consumer, LogConsumer... consumers) {
            def cs = consumers ? ContainerConsumer.of(consumer).addAll(consumers) : consumer
            return withConsumer(new PredicateConsumer(predicate, cs))
        }

        Builder filter(@DelegatesTo(LogEntry) Closure<Boolean> predicate, @DelegatesTo(LogEntry) Closure consumer, @DelegatesTo(LogEntry) Closure... consumers) {
            def cs = consumers ? ContainerConsumer.of(consumer).addAll(consumers) : new ClosureConsumer(consumer)
            return filter(new ClosurePredicate(predicate), cs)
        }

        TableBatchConsumer.Builder output(DataSource dataSource, @DelegatesTo(CsvWriterConsumer.Builder) Closure closure = null) {
            final builder = new TableBatchConsumer.Builder(this, TableBatchConsumer.of(dataSource, 'LogEntry'))
            checkDelegateClosure(closure, builder)
            builders.add(builder)
            return builder
        }

        Builder output(Writer writer, String... columns) {
            return withConsumer(CsvWriterConsumer.of(writer, columns))
        }

        Builder output(File writer, String... columns) {
            return withConsumer(CsvWriterConsumer.of(writer, columns))
        }

        CsvWriterConsumer.Builder output(File file, @DelegatesTo(CsvWriterConsumer.Builder) Closure closure = null) {
            final builder = new CsvWriterConsumer.Builder(this, CsvWriterConsumer.of(file))
            checkDelegateClosure(closure, builder)
            builders.add(builder)
            return builder
        }

        Builder output(String writer, String... columns) {
            return withConsumer(CsvWriterConsumer.of(writer, columns))
        }

        CsvWriterConsumer.Builder output(String filePath, @DelegatesTo(CsvWriterConsumer.Builder) Closure closure = null) {
            final builder = new CsvWriterConsumer.Builder(this, CsvWriterConsumer.of(filePath))
            checkDelegateClosure(closure, builder)
            builders.add(builder)
            return builder
        }

        Builder date(SimpleDateFormat dateFormat, String group = DateConsumer.DATE) {
            return withConsumer(DateConsumer.of(dateFormat, group))
        }

        Builder date(String dateFormat, String group = DateConsumer.DATE) {
            return withConsumer(DateConsumer.of(dateFormat, group))
        }

        protected void addSource(LogEntry logEntry) {
            sourceMap.put(logEntry, null)
        }

        Builder source(RandomAccessFile rf, String sourceName = null) {
            addSource(LineReader.newLogSource(rf, sourceName))
            return this
        }

        Builder source(InputStream inputStream, String sourceName = null) {
            addSource(LineReader.newLogSource(inputStream, sourceName))
            return this
        }

        Builder source(Reader reader, String sourceName = null) {
            addSource(LineReader.newLogSource(reader, sourceName))
            return this
        }

        Builder source(File file, String sourceName = null) {
            addSource(LineReader.newLogSource(file, sourceName ?: file.path))
            return this
        }

        Builder source(String content, String sourceName = null) {
            addSource(LineReader.newLogSource(content, sourceName))
            return this
        }

        protected void register(LogEntry sourceEntry, Object sourceConsumer, String sourceName = null) {
            sourceMap.put(sourceEntry, sourceConsumer)
            if (sourceName) {
                sourceNameConsumerMap.put(sourceName, sourceConsumer)
            }
        }

        protected Builder sourceConsumer(Object sourceConsumer, String sourceName = null) {
            for (LogEntry sourceEntry : sourceMap.keySet()) {
                final sc = sourceMap.get(sourceEntry)
                if (sc == null) {
                    register(sourceEntry, sourceConsumer, sourceName)
                }
            }
            return this
        }

        RegexConsumer.Builder regex(Pattern pattern, @DelegatesTo(RegexConsumer.Builder) Closure closure = null) {
            final builder = new RegexConsumer.Builder(this, RegexConsumer.of(pattern))
            checkDelegateClosure(closure, builder)
            if (builder.path) {
                source(builder, pattern.pattern(), builder.path, builder.fileFilter, builder.fileSorter)
            } else {
                sourceConsumer(builder, pattern.pattern())
            }
            return builder
        }

        RegexConsumer.Builder regex(String regEx, int flags = 0,
                                    @DelegatesTo(RegexConsumer.Builder) Closure closure = null) {
            final builder = new RegexConsumer.Builder(this, RegexConsumer.of(regEx, flags))
            checkDelegateClosure(closure, builder)
            if (builder.path) {
                source(builder, regEx, builder.path, builder.fileFilter, builder.fileSorter)
            } else {
                sourceConsumer(builder, regEx)
            }
            return builder
        }

        RegexConsumer.Builder regex(@DelegatesTo(RegexConsumer.Builder) Closure closure) {
            final builder = new RegexConsumer.Builder(this, new RegexConsumer())
            checkDelegateClosure(closure, builder)
            if (!builder.pattern) {
                throw new IllegalArgumentException('Regex pattern must be defined, please set it in configuration')
            }
            if (builder.path) {
                source(builder, builder.pattern, builder.path, builder.fileFilter, builder.fileSorter)
            } else {
                sourceConsumer(builder, builder.pattern)
            }
            return builder
        }

        Log4jConsumer.Builder log4j(String conversionPattern, @DelegatesTo(Log4jConsumer.Builder) Closure closure = null) {
            final builder = new Log4jConsumer.Builder(this, Log4jConsumer.of(conversionPattern))
            checkDelegateClosure(closure, builder)
            if (builder.path) {
                source(builder, conversionPattern, builder.path, builder.fileFilter, builder.fileSorter)
            } else {
                sourceConsumer(builder, conversionPattern)
            }
            return builder
        }

        protected void source(ConsumerBuilder builder, String sourceName, File folder, String fileFilter, Comparator<Path> fileSorter) {
            final files = listFiles(folder, fileFilter, fileSorter)
            for (File file : files) {
                final logEntry = LineReader.newLogSource(new LogEntry(source: file), file.path)
                register(logEntry, builder, sourceName)
                register(logEntry, builder, fileFilter)
            }
        }

        RegexConsumer.Builder regex(File folder, Pattern pattern,
                                    String fileFilter = '*', Comparator<Path> fileSorter = CREATED_DT_COMPARATOR) {
            final builder = regex(pattern)
            source(builder, pattern.pattern(), folder, fileFilter, fileSorter)
            return builder
        }

        Log4jConsumer.Builder log4j(File folder, String conversionPattern,
                                    String fileFilter = '*', Comparator<Path> fileSorter = CREATED_DT_COMPARATOR,
                                    @DelegatesTo(Log4jConsumer.Builder) Closure closure = null) {
            final builder = log4j(conversionPattern)
            source(builder, conversionPattern, folder, fileFilter, fileSorter)
            return builder
        }

        Log4jConsumer.Builder log4j(String folder, String conversionPattern,
                                    String fileFilter = '*', Comparator<Path> fileSorter = CREATED_DT_COMPARATOR,
                                    @DelegatesTo(Log4jConsumer.Builder) Closure closure = null) {
            final builder = log4j(conversionPattern, closure)
            source(builder, conversionPattern, new File(folder), fileFilter, fileSorter)
            return builder
        }

        Builder log4jConfig(File folder, String log4jConfig, Comparator<Path> fileSorter = CREATED_DT_COMPARATOR,
                            @DelegatesTo(Log4jConsumer.Builder) Closure closure = null) {
            final log4jConsumerProperties = Log4jConsumer.extractLog4jConsumerProperties(Utils.readResourceText(log4jConfig).readLines())
            if (log4jConsumerProperties.isEmpty()) {
                throw new IllegalArgumentException("Either empty or there are no file loggers defined in '$log4jConfig'")
            }
            final filterConversionMap = Log4jConsumer.toLog4jFileConversionPattern(log4jConsumerProperties)
            for (Map.Entry<String, String> entry : filterConversionMap.entrySet()) {
                log4j(folder, entry.value, entry.key, fileSorter)
            }
            for (Map.Entry<String, Map<String, String>> entry : log4jConsumerProperties.entrySet()) {
                final loggerName = entry.key
                final loggerProperties = entry.value
                final builder = log4jBuilder(loggerProperties.get(Log4jConsumer.APPENDER_CONVERSION_PATTERN_PROPERTY))
                if (builder) {
                    checkDelegateClosure(closure, builder)
                    sourceNameConsumerMap.put(loggerName, builder)
                }
            }
            return this
        }

        Builder log4jConfig(String filePath, String log4jConfigPath, Comparator<Path> fileSorter = CREATED_DT_COMPARATOR,
                            @DelegatesTo(Log4jConsumer.Builder) Closure closure = null) {
            return log4jConfig(new File(filePath), log4jConfigPath, fileSorter, closure)
        }

        Builder log4j(@DelegatesTo(Log4jConsumer.Builder) Closure closure) {
            final builder = new Log4jConsumer.Builder(this, new Log4jConsumer())
            checkDelegateClosure(closure, builder)
            if (!builder.pattern) {
                throw new IllegalArgumentException('Either conversion pattern or log4j config file must be defined, please set it in configuration')
            }
            if (builder.path && new File(builder.pattern).exists()) {
                log4jConfig(builder.path, builder.pattern, builder.fileSorter, closure)
            } else {
                log4j(builder.pattern, closure)
            }
            return this
        }

        Builder source(String path, LogConsumer consumer,
                       String fileFilter = '*', Comparator<Path> fileSorter = CREATED_DT_COMPARATOR) {
            final folder = new File(path)
            final files = listFiles(folder, fileFilter, fileSorter)
            final builder = withConsumer(consumer)
            for (File file : files) {
                register(LineReader.newLogSource(new LogEntry(source: folder), file.path), builder)
            }
            return builder
        }

        @SuppressWarnings("GrUnnecessaryPublicModifier")
        public <T> T builder(String sourceName) {
            return sourceNameConsumerMap.get(sourceName) as T
        }

        RegexConsumer.Builder regexBuilder(String regex) {
            return builder(regex)
        }

        Log4jConsumer.Builder log4jBuilder(String sourceName) {
            return builder(sourceName)
        }

        LogCrawler build() {
            buildSources()
            buildConsumers()
            sourceMap.clear()
            sourceNameConsumerMap.clear()
            builders.clear()
            return logCrawler
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
                            result = ['log4jConfig', [file, configValue] as Object[]]
                        } else {
                            result = ['log4j', [file, configValue] as Object[]]
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
                        final builder = log4j(configValue)
                        result = ['register', [sourceEntry, builder, configValue] as Object[]]
                        break
                    case REGEX_ARG:
                        result = ['register', [sourceEntry, regex(configValue), configValue] as Object[]]
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

    private static void usage() {
        println """
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
