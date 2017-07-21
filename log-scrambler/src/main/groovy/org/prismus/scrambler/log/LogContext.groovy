package org.prismus.scrambler.log

import com.google.common.cache.Cache
import com.google.common.cache.CacheBuilder
import groovy.transform.CompileStatic

import java.util.concurrent.*

/**
 * @author Serge Pruteanu
 */
@CompileStatic
class LogContext {
    Cache<Object, LogEntry> cache
    private Map<LogEntry, LogConsumer> sourceConsumerMap = [:]
    List<LogConsumer> consumers = new ArrayList<LogConsumer>()

    CompletionService completionService
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
        if (executorService) {
            completionService = new ExecutorCompletionService<Void>(executorService)
        }
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

    protected void consumeEntry(LogEntry entry) {
        for (LogConsumer consumer : consumers) {
            consumer.consume(entry)
        }
        if (entry.isCacheable() && cache) {
            cacheEntry(entry)
        }
    }

    protected void consumeSource(LogEntry entry, LogConsumer sourceConsumer) {
        final lineReader = LineReader.toLineReader(entry)
        final sourceName = LineReader.toSourceName(entry)
        try {
            LogEntry lastEntry = null
            int currentRow = 0
            String line
            while ((line = lineReader.readLine()) != null) {
                final logEntry = new LogEntry(sourceName, line, ++currentRow)
                sourceConsumer.consume(logEntry)
                if (!logEntry || logEntry.isEmpty()) {
                    if (multiline && lastEntry) {
                        lastEntry.line += line + LineReader.LINE_BREAK
                        sourceConsumer.consume(lastEntry)
                    }
                } else {
                    consumeEntry(logEntry)
                    lastEntry = logEntry
                }
            }
        } finally {
            Utils.closeQuietly(lineReader)
        }
    }

    void consume() {
        final Set<Future<Void>> jobs = [] as Set
        for (final entry : sourceConsumerMap.entrySet()) {
            final sourceLogEntry = entry.key
            final sourceConsumer = entry.value
            if (sourceConsumer instanceof AsynchronousProxyConsumer) {
                submitAsynchronous({consumeSource(sourceLogEntry, sourceConsumer)})
            } else {
                consumeSource(sourceLogEntry, sourceConsumer)
            }
        }
        List<Throwable> errors = []
        while (jobs.size()) {
            Future<Void> future
            if (asynchTimeout) {
                future = completionService.poll(asynchTimeout, asynchUnit)
            } else {
                future = completionService.poll()
            }
            if (future) {
                try {
                    future.get()
                } catch (ExecutionException ignore) {
                    errors.add(ignore.getCause())
                } finally {
                    jobs.remove(future)
                }
            }
        }
        if (errors) {
            final resultError = new RuntimeException('Failed to execute asychnronous jobs')
            // todo Serge: implement a container exception with all traces
            for (Throwable throwable : errors) {
                resultError.setStackTrace(throwable.getStackTrace())
            }
            throw resultError
        }
    }

    Future submitAsynchronous(Callable<Void> callable) {
        final work = completionService.submit(callable)
        try {
            work.get(1, TimeUnit.MILLISECONDS) // make sure that job is submitted
        } catch (TimeoutException ignore) { }
        return work
    }

}
