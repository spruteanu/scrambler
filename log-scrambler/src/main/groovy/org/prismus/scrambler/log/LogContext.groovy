package org.prismus.scrambler.log

import com.google.common.cache.Cache
import com.google.common.cache.CacheBuilder
import groovy.transform.CompileStatic

import java.util.concurrent.Callable
import java.util.concurrent.ExecutorService
import java.util.concurrent.Future
import java.util.concurrent.TimeUnit
import java.util.concurrent.TimeoutException

/**
 * @author Serge Pruteanu
 */
@CompileStatic
class LogContext {
    Cache<Object, LogEntry> cache
    private Map<LogEntry, LogConsumer> sourceConsumerMap = [:]
    List<LogConsumer> consumers = new ArrayList<LogConsumer>()

    ExecutorService executorService
    boolean processContext = true
    private int asynchTimeout
    private TimeUnit asynchUnit
    boolean multiline = true

    LogContext() {
    }

    LogContext oneLineEntry() {
        multiline = false
        return this
    }

    LogContext withExecutorService(ExecutorService executorService, int timeout = 0, TimeUnit unit = TimeUnit.MILLISECONDS) {
        this.asynchUnit = unit
        this.asynchTimeout = timeout
        this.executorService = executorService
        return this
    }

    LogContext withCache(int cacheSize = 1024 * 1024) {
        cache = CacheBuilder.newBuilder()
                .maximumSize(cacheSize)
                .build()
        return this
    }

    LogContext withCache(Cache cache) {
        this.cache = cache
        return this
    }

    LogContext addSource(LogEntry source, LogConsumer sourceConsumer) {
        sourceConsumerMap.put(source, sourceConsumer)
        return this
    }

    LogContext addConsumer(LogConsumer consumer) {
        consumers.add(consumer)
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

    protected void consumeSource(LogEntry entry) {
        final lineReader = LineReader.toLineReader(entry)
        final sourceName = LineReader.toSourceName(entry)
        try {
            LogEntry lastEntry = null
            int currentRow = 0
            String line
            while ((line = lineReader.readLine()) != null) {
                final logEntry = new LogEntry(sourceName, line, ++currentRow)
                consumeEntry(logEntry)
                if (!logEntry || logEntry.isEmpty()) {
                    if (multiline && lastEntry) {
                        lastEntry.line += line + LineReader.LINE_BREAK
                        consumeEntry(lastEntry)
                    }
                } else {
                    lastEntry = logEntry
                }
            }
        } finally {
            Utils.closeQuietly(lineReader)
        }
    }

    protected void consumeEntry(LogEntry entry) {
        for (LogConsumer consumer : consumers) {
            consumer.consume(entry)
        }
        if (entry.isCacheable() && cache) {
            cacheEntry(entry)
        }
    }

    Future submitAsynchronous(Callable<Void> callable) {
        final work = executorService.submit(callable)
        try {
            work.get(1, TimeUnit.MILLISECONDS) // make sure that job is submitted
        } catch (TimeoutException ignore) { }
        return work
    }

}
