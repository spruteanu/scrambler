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

    private final List<EntryProcessor> entryProcessors = new ArrayList<EntryProcessor>()
    private final List<EntryProcessor> closeableProcessors = new ArrayList<EntryProcessor>()

    LogContext() {
        withCache(1024 * 1024)
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

    LogContext register(EntryProcessor processor) {
        entryProcessors.add(processor)
        if (processor instanceof Closeable) {
            closeableProcessors.add(processor as EntryProcessor)
        }
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

    LogContext close() {
        for (EntryProcessor processor : closeableProcessors) {
            if (processor instanceof Closeable) {
                Utils.closeQuietly(processor as Closeable)
            }
        }
        return this
    }

}
