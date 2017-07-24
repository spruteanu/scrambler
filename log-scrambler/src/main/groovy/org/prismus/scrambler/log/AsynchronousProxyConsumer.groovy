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

    private final LogContext logContext
    private boolean awaitConsumption
    private int timeout
    private TimeUnit unit = TimeUnit.MILLISECONDS

    AsynchronousProxyConsumer(LogContext logContext, LogConsumer consumer) {
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
