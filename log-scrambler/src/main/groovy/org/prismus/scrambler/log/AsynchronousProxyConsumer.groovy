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

import java.util.concurrent.TimeUnit
import java.util.concurrent.TimeoutException

/**
 * @author Serge Pruteanu
 */
@CompileStatic
class AsynchronousProxyConsumer implements LogConsumer {
    final LogConsumer consumer

    private final LogCrawler logContext
    private boolean awaitConsumption
    private int timeout
    private TimeUnit unit = TimeUnit.MILLISECONDS

    AsynchronousProxyConsumer(LogCrawler logContext, LogConsumer consumer) {
        this.logContext = logContext
        this.consumer = consumer
    }

    AsynchronousProxyConsumer awaitConsumption(int timeout = 0, TimeUnit unit = TimeUnit.MILLISECONDS) {
        this.timeout = timeout
        this.unit = unit
        return this
    }

    @Override
    void consume(final LogEntry entry) {
        final work = logContext.submitAsynchronous(consumer, entry)
        if (awaitConsumption) {
            while (logContext.processContext) {
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

}
