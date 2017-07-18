package org.prismus.scrambler.log

import com.google.common.cache.Cache
import com.google.common.cache.CacheBuilder
import groovy.transform.CompileStatic

import java.text.SimpleDateFormat
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit
import java.util.regex.Pattern

/**
 * @author Serge Pruteanu
 */
@CompileStatic
class ContextBuilder {
    ExecutorService executorService
    ObjectProvider provider

    private LogContext logContext
    private Stack<LogContext> contextStack = new Stack<>()

    boolean asynchronousAll
    int defaultTimeout
    TimeUnit defaultUnit = TimeUnit.MILLISECONDS

    ContextBuilder() {
    }

    private Cache newCache(int cacheSize = 1024 * 1024) {
        return CacheBuilder.newBuilder()
                .maximumSize(cacheSize)
                .build()
    }

    private ContextBuilder newLogContext() {
        logContext = new LogContext()
        contextStack.push(logContext)
        return this
    }

    private ContextBuilder popLogContext() {
        logContext = contextStack.size() > 0 ? contextStack.pop() : null
        return this
    }

    protected AsychronousProxyConsumer newAsychronousConsumer(LogConsumer consumer, int timeout = defaultTimeout, TimeUnit unit = defaultUnit) {
        if (executorService == null) {
            Executors.newCachedThreadPool({})
        }
        return new AsychronousProxyConsumer(executorService, consumer).awaitConsumption(timeout, unit)
    }

    ContextBuilder asynchronousConsumer() {
        asynchronousAll = true
        return this
    }

    ContextBuilder asynchronousConsumer(LogConsumer consumer, int timeout = 0, TimeUnit unit = TimeUnit.MILLISECONDS) {
        throw new RuntimeException()
        return this
    }

    LogConsumer getConsumer(Object processorId, Object... args) {
        return provider.get(processorId, args) as LogConsumer
    }

    ContextBuilder withProvider(ObjectProvider provider) {
        this.provider = provider
        return this
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

    ContextBuilder logDirectory(File file) {
        file.eachFileRecurse {}
        throw new RuntimeException()
    }

    ContextBuilder logDirectory(String path) {
        return logDirectory(new File(path))
    }

    ConsumerBuilder contextCsvOutputConsumer(Writer writer, String... columns) {
        throw new RuntimeException()
    }

    ConsumerBuilder contextCsvOutputConsumer(File file, String... columns) {
        throw new RuntimeException()
    }

    ConsumerBuilder contextCsvOutputConsumer(String filePath, String... columns) {
        throw new RuntimeException()
    }

    ConsumerBuilder contextDateFormatConsumer(SimpleDateFormat dateFormat, String group = DateFormatConsumer.TIMESTAMP) {
        throw new RuntimeException()
    }

    ConsumerBuilder contextDateFormatConsumer(String dateFormat, String group = DateFormatConsumer.TIMESTAMP) {
        throw new RuntimeException()
    }

    RegexConsumerBuilder contextRegexConsumer(Pattern pattern) {
        throw new RuntimeException()
    }

    RegexConsumerBuilder contextRegexConsumer(String regEx, int flags = 0) {
        throw new RuntimeException()
    }

    RegexConsumerBuilder regexSource(String regEx, int flags = 0) {
        throw new RuntimeException()
    }

    Log4jConsumerBuilder log4jSource(String conversionPattern) {
        throw new RuntimeException()
    }

    ContextBuilder build() {
        throw new RuntimeException()
    }

}
