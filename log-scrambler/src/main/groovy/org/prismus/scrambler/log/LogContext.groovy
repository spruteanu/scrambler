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
    List<LogConsumer> consumers = new ArrayList<LogConsumer>()

    ExecutorService executorService
    boolean processContext = true
    private int asynchTimeout
    private TimeUnit asynchUnit

    LogContext() {
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

    LogContext addConsumer(LogConsumer processor) {
        consumers.add(processor)
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
        for (LogConsumer processor : consumers) {
            processor.process(entry)
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
