package org.prismus.scrambler.log

import groovy.transform.CompileStatic

import java.util.concurrent.ExecutorService
import java.util.concurrent.TimeUnit
import java.util.concurrent.TimeoutException

/**
 * @author Serge Pruteanu
 */
@CompileStatic
class AsychronousProxyConsumer implements LogConsumer {
    final ExecutorService executorService
    final LogConsumer consumer

    int timeout
    TimeUnit unit = TimeUnit.MILLISECONDS

    AsychronousProxyConsumer(ExecutorService executorService, LogConsumer consumer) {
        this.executorService = executorService
        this.consumer = consumer
    }

    AsychronousProxyConsumer awaitConsumption(int timeout, TimeUnit unit = TimeUnit.MILLISECONDS) {
        this.timeout = timeout
        this.unit = unit
        return this
    }

    @Override
    void process(final LogEntry entry) {
        final work = executorService.submit({consumer.process(entry)})
        if (timeout) {
            while (true) {
                try {
                    work.get(timeout, unit)
                    break
                } catch (TimeoutException ignore) { }
            }
        }
    }

}
