/*
 * Log crawler, tool that allows to extract/crawl log files for further analysis
 *
 * Copyright (c) 2015, Sergiu Prutean. All rights reserved.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3.0 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library.
 */

package org.prismus.scrambler.log

import groovy.transform.CompileStatic

import java.util.concurrent.Callable
import java.util.concurrent.CompletionService
import java.util.concurrent.ExecutionException
import java.util.concurrent.ExecutorCompletionService
import java.util.concurrent.ExecutorService
import java.util.concurrent.Future
import java.util.concurrent.ThreadFactory
import java.util.concurrent.TimeUnit
import java.util.concurrent.TimeoutException
import java.util.concurrent.atomic.AtomicBoolean
import java.util.concurrent.atomic.AtomicInteger

/**
 * @author Serge Pruteanu
 */
@CompileStatic
class AsynchronousProxyConsumer implements LogConsumer {
    final LogConsumer consumer
    private final LogCrawler logContext

    private int awaitTimeout
    private TimeUnit unit = TimeUnit.MILLISECONDS
    private boolean awaitConsumption
    private AtomicBoolean processContext = new AtomicBoolean(true)

    private ExecutorService executorService
    private CompletionService completionService
    private AtomicInteger jobsCount

    AsynchronousProxyConsumer(LogCrawler logContext, LogConsumer consumer) {
        this.logContext = logContext
        this.consumer = consumer
    }

    AsynchronousProxyConsumer withExecutorService(ExecutorService executorService) {
        this.executorService = executorService
        if (executorService) {
            completionService = new ExecutorCompletionService<Void>(executorService)
            jobsCount = new AtomicInteger()
        }
        return this
    }

    AsynchronousProxyConsumer awaitConsumption(int awaitTimeout = 0, TimeUnit unit = TimeUnit.MILLISECONDS) {
        this.awaitTimeout = awaitTimeout
        this.unit = unit
        return this
    }

    protected Future submitAsynchronous(Callable<Void> callable) {
        final work = completionService.submit(callable)
        jobsCount.incrementAndGet()
        try {
            work.get(1, TimeUnit.MILLISECONDS) // make sure that job is submitted
        } catch (TimeoutException ignore) { }
        return work
    }

////    todo fix asynch
//    private void consumeAsynchronous() {
//        for (final entry : sourceConsumerMap.entrySet()) {
//            consumeSource(LineReader.toLineReader(entry.key), LineReader.getSourceName(entry.key), entry.value)
//        }
//        awaitCompletion()
//        closeConsumers()
//    }

    protected Future submitAsynchronous(LogConsumer consumer, LogEntry logEntry) {
        return submitAsynchronous(new LogConsumerCallable(consumer, logEntry))
    }

    protected void awaitCompletion() {
        if (jobsCount == null) {
            return
        }
        List<Throwable> errors = []
        while (processContext.get() && jobsCount.get()) {
            try {
                Future<Void> future
                if (awaitTimeout) {
                    future = completionService.poll(awaitTimeout, unit)
                } else {
                    future = completionService.poll()
                }
                if (future) {
                    try {
                        if (awaitTimeout) {
                            future.get(awaitTimeout, unit)
                        } else {
                            future.get()
                        }
                    } catch (ExecutionException ignore) {
                        errors.add(ignore.getCause())
                    } finally {
                        jobsCount.decrementAndGet()
                    }
                }
            } catch (TimeoutException ignore) { }
        }
        if (errors) {
            throw new ContextException('Failed to execute asynchronous jobs', errors)
        }
    }

    @Override
    void consume(final LogEntry entry) {
        final work = submitAsynchronous(consumer, entry)
        if (awaitConsumption) {
            while (logContext.processContext) {
                try {
                    if (awaitTimeout) {
                        work.get(awaitTimeout, unit)
                    } else {
                        work.get()
                    }
                    break
                } catch (TimeoutException ignore) { }
            }
        }
    }

    @CompileStatic
    private static class LogConsumerCallable implements Callable<Void> {
        final LogConsumer consumer
        final LogEntry logEntry

        LogConsumerCallable(LogConsumer consumer, LogEntry logEntry) {
            this.consumer = consumer
            this.logEntry = logEntry
        }

        @Override
        Void call() throws Exception {
            consumer.consume(logEntry)
            return null
        }
    }

    @CompileStatic
    private static class ContextThreadFactory implements ThreadFactory {
        private final AtomicInteger count = new AtomicInteger()

        @Override
        Thread newThread(Runnable r) {
            final thread = new Thread(r)
            thread.setDaemon(true)
            thread.setName("LogContextThread${count.incrementAndGet()}")
            return thread
        }
    }

    @CompileStatic
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
