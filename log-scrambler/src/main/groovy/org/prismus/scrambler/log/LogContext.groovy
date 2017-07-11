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
    private List<LineParser> lineParsers
    private List<EntryProcessor> entryProcessors

    LogContext() {
        withCache(1024 * 1024)
        lineParsers = new ArrayList<LineParser>()
        entryProcessors = new ArrayList<EntryProcessor>()
    }

    LogContext withCache(int cacheSize) {
        cache = CacheBuilder.newBuilder()
                .maximumSize(cacheSize)
                .build()
        return this
    }

    LogContext register(LineParser parser) {
        lineParsers.add(parser)
        return this
    }

    LogContext register(EntryProcessor processor) {
        entryProcessors.add(processor)
        return this
    }

    LogContext withCache(Cache cache) {
        this.cache = cache
        return this
    }

    LogEntry handle(LogEntry entry) {
        final line = entry.line
        for (LineParser lineParser : lineParsers) {
            entry = entry.merge(lineParser.parse(line))
        }
        for (EntryProcessor entryProcessor : entryProcessors) {
            entry = entry.merge(entryProcessor.process(entry))
        }
        if (entry.id) {
            cache.put(entry.id, entry)
        }
        return entry
    }

}
