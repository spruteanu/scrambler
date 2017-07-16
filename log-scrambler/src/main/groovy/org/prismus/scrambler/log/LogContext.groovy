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

    ObjectProvider provider

    LogContext() {
        withCache(1024 * 1024)
    }

    LogContext withProvider(ObjectProvider provider) {
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
        return provider.get(processorId, args) as LogProcessor
    }

    LogEntry handle(LogEntry entry) {
        for (LogProcessor processor : processors) {
            processor.process(entry)
        }
        if (entry.isCacheable()) {
            final cacheKey = entry.cacheKey
            final cachedEntry = cache.getIfPresent(cacheKey)
            if (cachedEntry) {
                cachedEntry.logValueMap.putAll(entry.logValueMap)
            } else {
                cache.put(cacheKey, entry)
            }
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
