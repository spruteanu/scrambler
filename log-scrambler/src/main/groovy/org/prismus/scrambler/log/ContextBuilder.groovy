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

    private LogContext currentContext
    private final Stack<LogContext> contextStack = new Stack<>()
    private final List<LogContext> contextList = []
    private final List<ConsumerBuilder> consumerBuilders = []

    boolean asynchronousAll
    private ExecutorService executorService
    private int defaultTimeout
    private TimeUnit defaultUnit = TimeUnit.MILLISECONDS

    ContextBuilder() {
        newContext()
    }

    private Cache newCache(int cacheSize = 1024 * 1024) {
        return CacheBuilder.newBuilder()
                .maximumSize(cacheSize)
                .build()
    }

    private ContextBuilder newContext() {
        currentContext = new LogContext()
        contextStack.push(currentContext)
        contextList.add(currentContext)
        return this
    }

    @PackageScope
    ContextBuilder endContext() {
        currentContext = contextStack.size() > 0 ? contextStack.pop() : null
        return this
    }

    protected AsynchronousProxyConsumer newAsynchronousConsumer(LogConsumer consumer) {
        if (executorService == null && currentContext.executorService == null) {
            executorService = Executors.newCachedThreadPool(new ContextThreadFactory())
            currentContext.withExecutorService(executorService, defaultTimeout, defaultUnit)
        }
        return new AsynchronousProxyConsumer(currentContext, consumer)
    }

    LogConsumer getConsumer(Object processorId, Object... args) {
        return provider.get(processorId, args) as LogConsumer
    }

    ContextBuilder asynchronousAll(int defaultTimeout = this.defaultTimeout, TimeUnit defaultUnit = this.defaultUnit) {
        asynchronousAll = true
        this.defaultTimeout = defaultTimeout
        this.defaultUnit = defaultUnit
        return this
    }

    ContextBuilder withProvider(ObjectProvider provider) {
        this.provider = provider
        return this
    }

    ContextBuilder withConsumer(LogConsumer consumer) {
        currentContext.addConsumer(newAsynchronousConsumer(consumer))
        return this
    }

    ContextBuilder asynchronousConsumer(LogConsumer consumer) {
        currentContext.addConsumer(newAsynchronousConsumer(consumer))
        return this
    }

    ConsumerBuilder withCsvOutputConsumer(Writer writer, String... columns) {
        final builder = new ConsumerBuilder(this, CsvOutputConsumer.of(writer, columns))
        consumerBuilders.add(builder)
        return builder
    }

    ConsumerBuilder withCsvOutputConsumer(File file, String... columns) {
        final builder = new ConsumerBuilder(this, CsvOutputConsumer.of(file, columns))
        consumerBuilders.add(builder)
        return builder
    }

    ConsumerBuilder withCsvOutputConsumer(String filePath, String... columns) {
        final builder = new ConsumerBuilder(this, CsvOutputConsumer.of(filePath, columns))
        consumerBuilders.add(builder)
        return builder
    }

    ContextBuilder withDateFormatConsumer(SimpleDateFormat dateFormat, String group = DateFormatConsumer.TIMESTAMP) {
        currentContext.addConsumer(DateFormatConsumer.of(dateFormat, group))
        return this
    }

    ContextBuilder withDateFormatConsumer(String dateFormat, String group = DateFormatConsumer.TIMESTAMP) {
        currentContext.addConsumer(DateFormatConsumer.of(dateFormat, group))
        return this
    }

    RegexConsumerBuilder withRegexConsumer(Pattern pattern) {
        final builder = new RegexConsumerBuilder(this, RegexConsumer.of(pattern))
        consumerBuilders.add(builder)
        return builder
    }

    RegexConsumerBuilder withRegexConsumer(String regEx, int flags = 0) {
        final builder = new RegexConsumerBuilder(this, RegexConsumer.of(regEx, flags))
        consumerBuilders.add(builder)
        return builder
    }

    Log4jConsumerBuilder withLog4jConsumer(String conversionPattern) {
        final builder = new Log4jConsumerBuilder(this, Log4jConsumer.ofPattern(conversionPattern))
        consumerBuilders.add(builder)
        return builder
    }

    ContextBuilder logSource(RandomAccessFile rf, String sourceName = null) {
        throw new RuntimeException()
    }

    ContextBuilder logSource(InputStream inputStream, String sourceName = null) {
        throw new RuntimeException()
    }

    ContextBuilder logSource(Reader reader, String sourceName = null) {
        throw new RuntimeException()
    }

    ContextBuilder logSource(File file, String sourceName = null) {
        throw new RuntimeException()
    }

    ContextBuilder logSource(String content, String sourceName = null) {
        throw new RuntimeException()
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
        for (File file : files) {
            final sourceEntry = LogReaderConsumer.addSourceName(new LogEntry(source: folder), file.path)
        }
        return withRegexConsumer(pattern)
    }

    Log4jConsumerBuilder log4jSourceFolder(File folder, String conversionPattern,
                                           String fileFilter = '*', Comparator<Path> fileSorter = CREATED_DT_COMPARATOR) {
        final files = listFolderFiles(folder, fileFilter, fileSorter)
        for (File file : files) {
            final sourceEntry = LogReaderConsumer.addSourceName(new LogEntry(source: folder), file.path)
        }
        return withLog4jConsumer(conversionPattern)
    }

    ContextBuilder sourceFolder(String path, LogConsumer consumer,
                                String fileFilter = '*', Comparator<Path> fileSorter = CREATED_DT_COMPARATOR) {
        final folder = new File(path)
        final files = listFolderFiles(folder, fileFilter, fileSorter)
        for (File file : files) {
            final sourceEntry = LogReaderConsumer.addSourceName(new LogEntry(source: folder), file.path)
        }
        return withConsumer(consumer)
    }

    ContextBuilder build() {
        while (currentContext) {
            endContext()
        }
        throw new RuntimeException()
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
