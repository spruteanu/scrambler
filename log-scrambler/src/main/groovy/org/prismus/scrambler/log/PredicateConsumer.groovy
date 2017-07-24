package org.prismus.scrambler.log

import groovy.transform.CompileStatic

import java.util.function.Predicate

/**
 * @author Serge Pruteanu
 */
@CompileStatic
class PredicateConsumer implements LogConsumer {
    final LogConsumer consumer
    final Predicate<LogEntry> filter

    PredicateConsumer(LogConsumer consumer, Predicate<LogEntry> filter) {
        this.consumer = consumer
        this.filter = filter
    }

    @Override
    void consume(LogEntry entry) {
        if (filter.test(entry)) {
            consumer.consume(entry)
        }
    }

}
