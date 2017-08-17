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

import java.util.concurrent.*
import java.util.concurrent.atomic.AtomicInteger

/**
 * @author Serge Pruteanu
 */
@CompileStatic
@PackageScope
class AsynchronousJobs extends CloseableContainer {

    private final LogCrawler logCrawler
    private int timeout
    private TimeUnit unit = TimeUnit.MILLISECONDS

    private ExecutorService executorService
    private CompletionService completionService
    private AtomicInteger count

    protected LinkedList<Closeable> closeables = []

    AsynchronousJobs(LogCrawler logCrawler) {
        this.logCrawler = logCrawler
    }

    AsynchronousJobs withExecutorService(ExecutorService executorService, int timeout = 0, TimeUnit unit = TimeUnit.MILLISECONDS) {
        this.executorService = executorService
        this.timeout = timeout
        this.unit = unit
        if (executorService) {
            completionService = new ExecutorCompletionService<Void>(executorService)
            count = new AtomicInteger()
        }
        return this
    }

    AsynchronousJobs stop() {
        if (executorService) {
            executorService.shutdown()
        }
        return this
    }

    protected Future submit(Callable<Void> callable) {
        final work = completionService.submit(callable)
        count.incrementAndGet()
        try {
            work.get(1, TimeUnit.MILLISECONDS) // make sure that job is submitted
        } catch (TimeoutException ignore) { }
        return work
    }

    protected Future submit(LogEntry logEntry, LogConsumer consumer) {
        return submit(new LogConsumerCallable(logEntry, consumer))
    }

    protected void finish() {
        if (count == null) {
            return
        }
        List<Throwable> errors = []
        final processContext = logCrawler.processContext
        while (processContext.get() && count.get()) {
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
                        count.decrementAndGet()
                    }
                }
            } catch (TimeoutException ignore) { }
        }
        if (errors) {
            throw new ContextException('Failed to execute asynchronous jobs', errors)
        }
    }

    @Override
    void close() throws IOException {
        try {
            finish()
        } finally {
            super.close()
        }
    }

    static Builder builder(LogCrawler logCrawler) {
        return new Builder(new AsynchronousJobs(logCrawler))
    }

    @CompileStatic
    static class Builder extends ConsumerBuilder {
        private ExecutorService executorService
        private int timeout
        private TimeUnit unit = TimeUnit.MILLISECONDS
        private final AsynchronousJobs jobs

        Builder(AsynchronousJobs jobs) {
            this.jobs = jobs
        }

        Builder withExecutorService(int timeout = 0, TimeUnit unit = TimeUnit.MILLISECONDS) {
            return withExecutorService(Executors.newCachedThreadPool(new ContextThreadFactory()), timeout, unit)
        }

        Builder withExecutorService(ExecutorService executorService, int timeout = 0, TimeUnit unit = TimeUnit.MILLISECONDS) {
            this.executorService = executorService
            this.timeout = timeout
            this.unit = unit
            return this
        }

        protected LogConsumer build() {
            if (!executorService) {
                executorService = Executors.newCachedThreadPool(new ContextThreadFactory())
            }
            jobs.withExecutorService(executorService, timeout, unit)
            final sourceConsumerMap = jobs.logCrawler.sourceConsumerMap
            for (LogEntry logEntry : sourceConsumerMap.keySet()) {
                sourceConsumerMap.put(logEntry, new AsynchronousJobConsumer(jobs, sourceConsumerMap.get(logEntry)))
            }
            jobs.logCrawler.checkCloseable(jobs)
            return null
        }
    }

    @CompileStatic
    @PackageScope
    static class AsynchronousJobConsumer implements LogConsumer {
        final AsynchronousJobs jobs
        final LogConsumer consumer

        AsynchronousJobConsumer(AsynchronousJobs jobs, LogConsumer consumer) {
            this.jobs = jobs
            this.consumer = consumer
        }

        @Override
        void consume(LogEntry entry) {
            jobs.submit(new LogConsumerCallable(entry, consumer))
        }
    }

    @CompileStatic
    private static class LogConsumerCallable implements Callable<Void> {
        final LogEntry logEntry
        final LogConsumer consumer

        LogConsumerCallable(LogEntry logEntry, LogConsumer consumer) {
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

}
