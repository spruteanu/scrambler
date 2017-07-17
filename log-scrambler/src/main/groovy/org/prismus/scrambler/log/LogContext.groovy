package org.prismus.scrambler.log

import com.google.common.cache.Cache
import com.google.common.cache.CacheBuilder
import groovy.transform.CompileStatic

/**
 * @author Serge Pruteanu
 */
@CompileStatic
class LogContext {
    Cache<Object, LogEntry> cache
    List<LogConsumer> processors = new ArrayList<LogConsumer>()

    LogContext() {
    }

    LogContext cacheable(int cacheSize = 1024 * 1024) {
        cache = CacheBuilder.newBuilder()
                .maximumSize(cacheSize)
                .build()
        return this
    }

    LogContext withCache(Cache cache) {
        this.cache = cache
        return this
    }

    LogContext register(LogConsumer processor) {
        processors.add(processor)
        return this
    }

    synchronized void cacheEntry(LogEntry entry) {
        final cacheKey = entry.cacheKey
        final cachedEntry = cache.getIfPresent(cacheKey)
        if (cachedEntry) {
            cachedEntry.logValueMap.putAll(entry.logValueMap)
        } else {
            cache.put(cacheKey, entry)
        }
    }

    void process(LogEntry entry) {
        for (LogConsumer processor : processors) {
            processor.process(entry)
        }
        if (entry.isCacheable() && cache) {
            cacheEntry(entry)
        }
    }

}
