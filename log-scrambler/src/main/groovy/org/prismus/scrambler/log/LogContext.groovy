package org.prismus.scrambler.log

import com.google.common.cache.Cache
import groovy.transform.CompileStatic

import java.util.concurrent.*
import java.util.concurrent.atomic.AtomicInteger
import java.util.function.Predicate

/**
 * @author Serge Pruteanu
 */
@CompileStatic
class LogContext {
    Cache<Object, LogEntry> cache
    private Map<LogEntry, LogConsumer> sourceConsumerMap = [:]
    private List<LogConsumer> consumers = new ArrayList<LogConsumer>()
    private List<Closeable> closeables = []

    private CompletionService completionService
    private int asynchTimeout
    private TimeUnit asynchUnit
    private AtomicInteger jobsCount
    boolean processContext = true

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
            jobsCount = new AtomicInteger()
        }
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
        if (consumer instanceof Closeable) {
            closeables.add(consumer as Closeable)
        }
        if (consumer instanceof CsvOutputConsumer) {
            final csvOutputConsumer = (CsvOutputConsumer) consumer
            if (!csvOutputConsumer.columns) {
                for (LogConsumer sourceConsumer : sourceConsumerMap.values()) {
                    if (sourceConsumer instanceof RegexConsumer) {
                        csvOutputConsumer.columns = ((RegexConsumer) sourceConsumer).groupIndexMap.keySet().toList()
                    }
                }
                if (!csvOutputConsumer.columns) {
                    throw new RuntimeException('Columns are not defined for CsvOutputConsumer')
                }
            }
        }
        return this
    }

    LogContext addPredicateConsumer(LogConsumer consumer, Predicate predicate) {
        return addConsumer(new PredicateConsumer(consumer, predicate))
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

    protected void checkCacheable(LogEntry entry) {
        if (entry.isCacheable() && cache) {
            cacheEntry(entry)
        }
    }

    protected LogEntry consumeEntry(LogEntry entry) {
        if (!entry) {
            return entry
        }
        for (LogConsumer consumer : consumers) {
            consumer.consume(entry)
        }
        checkCacheable(entry)
        return entry
    }

    protected void awaitJobsCompletion() {
        if (jobsCount == null) {
            return
        }
        List<Throwable> errors = []
        while (jobsCount.get()) {
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
                    jobsCount.decrementAndGet()
                }
            }
        }
        if (errors) {
            throw new ContextException('Failed to execute asychnronous jobs', errors)
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
                if (logEntry.isEmpty()) {
                    if (multiline && lastEntry) {
                        lastEntry.line += LineReader.LINE_BREAK + line
                        sourceConsumer.consume(lastEntry)
                    }
                } else {
                    consumeEntry(lastEntry)
                    lastEntry = logEntry
                }
            }
            consumeEntry(lastEntry)
        } finally {
            Utils.closeQuietly(lineReader)
        }
    }

    void consume() {
        for (final entry : sourceConsumerMap.entrySet()) {
            consumeSource(entry.key, entry.value)
        }
        awaitJobsCompletion()
        for (Closeable closeable: closeables) {
            Utils.closeQuietly(closeable)
        }
    }

    Future submitAsynchronous(Callable<Void> callable) {
        final work = completionService.submit(callable)
        jobsCount.incrementAndGet()
        try {
            work.get(1, TimeUnit.MILLISECONDS) // make sure that job is submitted
        } catch (TimeoutException ignore) { }
        return work
    }

    Future submitAsynchronous(LogConsumer consumer, LogEntry logEntry) {
        return submitAsynchronous(new LogConsumerCallable(consumer, logEntry))
    }

    private class LogConsumerCallable implements Callable<Void> {
        final LogConsumer consumer
        final LogEntry logEntry

        LogConsumerCallable(LogConsumer consumer, LogEntry logEntry) {
            this.consumer = consumer
            this.logEntry = logEntry
        }

        @Override
        Void call() throws Exception {
            consumer.consume(logEntry)
            checkCacheable(logEntry)
            return null
        }
    }

    private static class ContextException extends RuntimeException implements Iterable<Throwable> {
        private List<Throwable> throwables

        ContextException(String message, List<Throwable> throwables) {
            super(message)
            this.throwables = throwables
        }

        @Override
        Iterator<Throwable> iterator() {
            return throwables.iterator()
        }

    }

}
