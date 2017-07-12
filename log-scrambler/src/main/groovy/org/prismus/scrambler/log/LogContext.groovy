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
    private List<EntryProcessor> entryProcessors

    LogContext() {
        withCache(1024 * 1024)
        entryProcessors = new ArrayList<EntryProcessor>()
    }

    LogContext withCache(int cacheSize) {
        cache = CacheBuilder.newBuilder()
                .maximumSize(cacheSize)
                .build()
        return this
    }

    LogContext withCache(Cache cache) {
        this.cache = cache
        return this
    }

    LogContext register(EntryProcessor processor) {
        entryProcessors.add(processor)
        return this
    }

    LogEntry handle(LogEntry entry) {
        for (EntryProcessor entryProcessor : entryProcessors) {
            entry = entryProcessor.process(entry)
        }
        if (entry.id) {
            cache.put(entry.id, entry)
        }
        return entry
    }

}
