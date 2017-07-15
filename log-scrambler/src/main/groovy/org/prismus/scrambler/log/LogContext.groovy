package org.prismus.scrambler.log

import com.google.common.cache.Cache
import com.google.common.cache.CacheBuilder
import groovy.transform.CompileStatic

/**
 * @author Serge Pruteanu
 */
@CompileStatic
class LogContext {
    private Cache<Object, LogEntry> cache

    private final List<LogProcessor> processors = new ArrayList<LogProcessor>()
    private final List<LogProcessor> closeableProcessors = new ArrayList<LogProcessor>()

    ProcessorProvider provider

    LogContext() {
        withCache(1024 * 1024)
    }

    LogContext withProvider(ProcessorProvider provider) {
        this.provider = provider
        return this
    }

    LogContext withCache(int cacheSize) {
        cache = CacheBuilder.newBuilder()
                .maximumSize(cacheSize)
                .build()
        return this
    }

    LogContext withHugeCache() {
        return withCache(50 * 1024 * 1024)
    }

    LogContext withCache(Cache cache) {
        this.cache = cache
        return this
    }

    LogContext register(LogProcessor processor) {
        processors.add(processor)
        if (processor instanceof Closeable) {
            closeableProcessors.add(processor as LogProcessor)
        }
        return this
    }

    LogProcessor getProcessor(String processorId, Object... args) {
        return provider.get(processorId, args)
    }

    LogEntry handle(LogEntry entry) {
        for (LogProcessor processor : processors) {
            entry = processor.process(entry)
        }
        if (entry.id) {
            cache.put(entry.id, entry)
        }
        return entry
    }

    LogContext close() {
        for (LogProcessor processor : closeableProcessors) {
            if (processor instanceof Closeable) {
                Utils.closeQuietly(processor as Closeable)
            }
        }
        return this
    }

}
