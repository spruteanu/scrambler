package org.prismus.scrambler.log

import com.google.common.cache.Cache
import com.google.common.cache.CacheBuilder
import groovy.transform.CompileStatic

import java.util.concurrent.ExecutorService
import java.util.concurrent.TimeUnit

/**
 * @author Serge Pruteanu
 */
@CompileStatic
class ContextBuilder {
    ExecutorService executorService
    ObjectProvider provider

    private LogContext logContext
    private Stack<LogContext> contextStack = new Stack<>()

    ContextBuilder() {
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

    ContextBuilder asynchronousConsumer(LogConsumer consumer, int timeout = 0, TimeUnit unit = TimeUnit.MILLISECONDS) {
        return this
    }

    LogConsumer getProcessor(String processorId, Object... args) {
        return provider.get(processorId, args) as LogConsumer
    }

    ContextBuilder withProvider(ObjectProvider provider) {
        this.provider = provider
        return this
    }

    ContextBuilder source(RandomAccessFile rf, String sourceName = null) {
        throw new RuntimeException()
    }

    ContextBuilder source(InputStream inputStream, String sourceName = null) {
        throw new RuntimeException()
    }

    ContextBuilder source(Reader reader, String sourceName = null) {
        throw new RuntimeException()
    }

    ContextBuilder source(File file, String sourceName = null) {
        throw new RuntimeException()
    }

    ContextBuilder source(String content, String sourceName = null) {
        throw new RuntimeException()
    }

    ContextBuilder directorySource(File file) {
        file.eachFileRecurse {}
        throw new RuntimeException()
    }

    ContextBuilder directorySource(String path) {
        return directorySource(new File(path))
    }

    private Cache newCache(int cacheSize = 1024 * 1024) {
        return CacheBuilder.newBuilder()
                .maximumSize(cacheSize)
                .build()
    }

    ContextBuilder build() {
        throw new RuntimeException()
    }

}
