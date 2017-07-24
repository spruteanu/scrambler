package org.prismus.scrambler.log

import com.google.common.cache.Cache
import com.google.common.cache.CacheBuilder
import groovy.transform.CompileStatic
import groovy.transform.PackageScope

import java.nio.file.FileVisitOption
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths
import java.nio.file.attribute.BasicFileAttributes
import java.text.SimpleDateFormat
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import java.util.concurrent.ThreadFactory
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicInteger
import java.util.regex.Pattern
import java.util.stream.Collectors

/**
 * @author Serge Pruteanu
 */
@CompileStatic
class ContextBuilder {
    static final Comparator<Path> CREATED_DT_COMPARATOR = { Path l, Path r ->
        final leftCreated = Files.readAttributes(l, BasicFileAttributes.class).creationTime()
        final rightCreated = Files.readAttributes(r, BasicFileAttributes.class).creationTime()
        return leftCreated.compareTo(rightCreated)
    } as Comparator<Path>

    ObjectProvider provider

    private LogContext context
    private final Map<LogEntry, Object> sourceConsumerMap = [:]
    private final List<ConsumerBuilder> consumerBuilders = []

    private boolean asynchronousSources
    private ExecutorService executorService
    private int defaultTimeout
    private TimeUnit defaultUnit = TimeUnit.MILLISECONDS

    ContextBuilder() {
        context = new LogContext()
    }

    private Cache newCache(int cacheSize = 1024 * 1024) {
        return CacheBuilder.newBuilder()
                .maximumSize(cacheSize)
                .build()
    }

    @PackageScope
    LogConsumer checkAsynchronousConsumer(LogConsumer result) {
        return (asynchronousSources ? newAsynchronousConsumer(result) : result)
    }

    @PackageScope
    void buildSourceConsumers() {
        for (Map.Entry<LogEntry, Object> entry : sourceConsumerMap.entrySet()) {
            final value = entry.value
            LogConsumer sourceConsumer = null
            if (value instanceof LogConsumer) {
                sourceConsumer = value as LogConsumer
            } else if (value instanceof ConsumerBuilder) {
                sourceConsumer = value.build()
            }
            if (sourceConsumer) {
                context.addSource(entry.key, checkAsynchronousConsumer(sourceConsumer))
            }
        }
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

    ContextBuilder asynchronousSources(int defaultTimeout = this.defaultTimeout, TimeUnit defaultUnit = this.defaultUnit) {
        asynchronousSources = true
        this.defaultTimeout = defaultTimeout
        this.defaultUnit = defaultUnit
        return this
    }

    ContextBuilder withProvider(ObjectProvider provider) {
        this.provider = provider
        return this
    }

    ContextBuilder withConsumer(LogConsumer consumer) {
        context.addConsumer(consumer)
        return this
    }

    ContextBuilder asynchronousConsumer(LogConsumer consumer) {
        context.addConsumer(newAsynchronousConsumer(consumer))
        return this
    }

    ConsumerBuilder withCsvCollector(Writer writer, String... columns) {
        final builder = new ConsumerBuilder(this, CsvOutputConsumer.of(writer, columns))
        consumerBuilders.add(builder)
        return builder
    }

    ConsumerBuilder withCsvCollector(File file, String... columns) {
        final builder = new ConsumerBuilder(this, CsvOutputConsumer.of(file, columns))
        consumerBuilders.add(builder)
        return builder
    }

    ConsumerBuilder withCsvCollector(String filePath, String... columns) {
        final builder = new ConsumerBuilder(this, CsvOutputConsumer.of(filePath, columns))
        consumerBuilders.add(builder)
        return builder
    }

    ContextBuilder withDateFormatConsumer(SimpleDateFormat dateFormat, String group = DateFormatConsumer.TIMESTAMP) {
        context.addConsumer(DateFormatConsumer.of(dateFormat, group))
        return this
    }

    ContextBuilder withDateFormatConsumer(String dateFormat, String group = DateFormatConsumer.TIMESTAMP) {
        context.addConsumer(DateFormatConsumer.of(dateFormat, group))
        return this
    }

    protected void addSource(LogEntry logEntry) {
        sourceConsumerMap.put(logEntry, null)
    }

    ContextBuilder logSource(RandomAccessFile rf, String sourceName = null) {
        addSource(LineReader.newLogSource(rf, sourceName))
        return this
    }

    ContextBuilder logSource(InputStream inputStream, String sourceName = null) {
        addSource(LineReader.newLogSource(inputStream, sourceName))
        return this
    }

    ContextBuilder logSource(Reader reader, String sourceName = null) {
        addSource(LineReader.newLogSource(reader, sourceName))
        return this
    }

    ContextBuilder logSource(File file, String sourceName = null) {
        addSource(LineReader.newLogSource(file, sourceName ?: file.path))
        return this
    }

    ContextBuilder logSource(String content, String sourceName = null) {
        addSource(LineReader.newLogSource(content, sourceName))
        return this
    }

    protected ContextBuilder sourceConsumer(Object sourceConsumer) {
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

    protected static String fileFilterToRegex(String fileFilter) {
        String result = fileFilter.replaceAll('\\*', '.*')
        result = result.replaceAll('\\?', '.')
        result = result.replaceAll('\\\\', '\\\\')
        return result
    }

    static List<File> listFolderFiles(File folder, String fileFilter = '*', Comparator<Path> fileSorter = CREATED_DT_COMPARATOR) {
        final Pattern filePattern = ~/${fileFilterToRegex(fileFilter)}/
        return Files.find(Paths.get(folder.toURI()), 999,
                { Path p, BasicFileAttributes bfa -> bfa.isRegularFile() && filePattern.matcher(p.getFileName().toString()).matches() }, FileVisitOption.FOLLOW_LINKS
        ).sorted(fileSorter).collect(Collectors.toList())
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
            sourceConsumerMap.put(LineReader.newLogSource(new LogEntry(source: folder), file.path), builder)
        }
        return builder
    }

    ContextBuilder sourceFolder(String path, LogConsumer consumer,
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
        context.withCache(newCache())

        buildSourceConsumers()
        buildLogEntryConsumers()
        sourceConsumerMap.clear()
        consumerBuilders.clear()
        return context
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
