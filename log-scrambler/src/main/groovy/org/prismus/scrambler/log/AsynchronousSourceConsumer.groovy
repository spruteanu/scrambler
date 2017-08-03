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
import java.util.concurrent.Executors
import java.util.concurrent.Future
import java.util.concurrent.ThreadFactory
import java.util.concurrent.TimeUnit
import java.util.concurrent.TimeoutException
import java.util.concurrent.atomic.AtomicInteger

/**
 * @author Serge Pruteanu
 */
@CompileStatic
class AsynchronousSourceConsumer implements LogConsumer {
    final LogConsumer consumer

    private final LogCrawler logContext
    private boolean awaitConsumption
    private int timeout
    private TimeUnit unit = TimeUnit.MILLISECONDS

    private CompletionService completionService
    private int asynchTimeout
    private TimeUnit asynchUnit
    private AtomicInteger jobsCount
    volatile boolean processContext = true

    AsynchronousSourceConsumer(LogCrawler logContext, LogConsumer consumer) {
        this.logContext = logContext
        this.consumer = consumer
    }

    AsynchronousSourceConsumer withExecutorService(ExecutorService executorService, int timeout = 0, TimeUnit unit = TimeUnit.MILLISECONDS) {
        this.asynchUnit = unit
        this.asynchTimeout = timeout
        if (executorService) {
            completionService = new ExecutorCompletionService<Void>(executorService)
            jobsCount = new AtomicInteger()
        }
        return this
    }

    @SuppressWarnings("GroovySynchronizationOnNonFinalField")
    AsynchronousSourceConsumer stopAsynchronous() {
        processContext = false
        synchronized (completionService) {
            completionService.notifyAll()
        }
        return this
    }

    AsynchronousSourceConsumer awaitConsumption(int timeout = 0, TimeUnit unit = TimeUnit.MILLISECONDS) {
        this.timeout = timeout
        this.unit = unit
        return this
    }

    @Override
    void consume(final LogEntry entry) {
        final work = submitAsynchronous(consumer, entry)
        if (awaitConsumption) {
            while (processContext) {
                try {
                    if (timeout) {
                        work.get(timeout, unit)
                    } else {
                        work.get()
                    }
                    break
                } catch (TimeoutException ignore) { }
            }
        }
    }

//    private void consumeAsynchronous() {
//        for (final entry : sourceConsumerMap.entrySet()) {
//            consumeSource(LineReader.toLineReader(entry.key), LineReader.getSourceName(entry.key), entry.value)
//        }
//    }

    protected Future submitAsynchronous(Callable<Void> callable) {
        final work = completionService.submit(callable)
        jobsCount.incrementAndGet()
        try {
            work.get(1, TimeUnit.MILLISECONDS) // make sure that job is submitted
        } catch (TimeoutException ignore) { }
        return work
    }

    protected Future submitAsynchronous(LogConsumer consumer, LogEntry logEntry) {
        return submitAsynchronous(new LogConsumerCallable(consumer, logEntry))
    }

    protected void awaitJobsCompletion() {
        if (jobsCount == null) {
            return
        }
        List<Throwable> errors = []
        while (processContext && jobsCount.get()) {
            try {
                Future<Void> future
                if (asynchTimeout) {
                    future = completionService.poll(asynchTimeout, asynchUnit)
                } else {
                    future = completionService.poll()
                }
                if (future) {
                    try {
                        if (asynchTimeout) {
                            future.get(asynchTimeout, asynchUnit)
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

    static class Builder {
        private ExecutorService executorService
        private int defaultTimeout
        private TimeUnit defaultUnit = TimeUnit.MILLISECONDS

//        protected AsynchronousSourceConsumer newAsynchronousConsumer(LogConsumer consumer, int timeout = 0, TimeUnit unit = TimeUnit.MILLISECONDS) {
//            if (executorService == null) {
//                executorService = Executors.newCachedThreadPool(new ContextThreadFactory())
//                withExecutorService(executorService, defaultTimeout, defaultUnit)
//            }
//            return consumer instanceof AsynchronousSourceConsumer ? consumer as AsynchronousSourceConsumer : new AsynchronousSourceConsumer(context, consumer).awaitConsumption(timeout, unit)
//        }
//
//        Builder asynchronousSources(int defaultTimeout = this.defaultTimeout, TimeUnit defaultUnit = this.defaultUnit) {
//            asynchronousSources = true
//            this.defaultTimeout = defaultTimeout
//            this.defaultUnit = defaultUnit
//            return this
//        }
//
//        Builder asynchronous(LogConsumer consumer) {
//            context.withConsumer(newAsynchronousConsumer(consumer))
//            return this
//        }
    }
}
