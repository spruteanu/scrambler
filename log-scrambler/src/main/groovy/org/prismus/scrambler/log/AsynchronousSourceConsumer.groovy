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
import groovy.transform.PackageScope

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
import java.util.concurrent.atomic.AtomicBoolean
import java.util.concurrent.atomic.AtomicInteger

/**
 * @author Serge Pruteanu
 */
@CompileStatic
@PackageScope
class AsynchronousSourceConsumer {

    private final LogCrawler logContext
    private int timeout
    private TimeUnit unit = TimeUnit.MILLISECONDS

    private CompletionService completionService
    private AtomicInteger jobsCount

    AsynchronousSourceConsumer(LogCrawler logContext) {
        this.logContext = logContext
    }

    AsynchronousSourceConsumer withExecutorService(ExecutorService executorService, int timeout = 0, TimeUnit unit = TimeUnit.MILLISECONDS) {
        this.timeout = timeout
        this.unit = unit
        if (executorService) {
            completionService = new ExecutorCompletionService<Void>(executorService)
            jobsCount = new AtomicInteger()
        }
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

    protected Future submitAsynchronous(LogEntry logEntry, LogConsumer consumer) {
        return submitAsynchronous(new LogConsumerCallable(consumer, logEntry))
    }

    void awaitJobsCompletion() {
        if (jobsCount == null) {
            return
        }
        List<Throwable> errors = []
        final processContext = logContext.processContext
        while (processContext.get() && jobsCount.get()) {
            try {
                Future<Void> future
                if (timeout) {
                    future = completionService.poll(timeout, unit)
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
            } catch (TimeoutException ignore) { }
        }
        if (errors) {
            throw new ContextException('Failed to execute asynchronous jobs', errors)
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
