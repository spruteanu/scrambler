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

    PredicateConsumer(Closure filter, Closure consumer) {
        this(new ClosurePredicate(filter), new ClosureConsumer(consumer))
    }

    PredicateConsumer(Closure filter, LogConsumer consumer) {
        this(new ClosurePredicate(filter), consumer)
    }

    PredicateConsumer(Predicate<LogEntry> filter, LogConsumer consumer) {
        this.consumer = consumer
        this.filter = filter
    }

    @Override
    void consume(LogEntry entry) {
        if (filter.test(entry)) {
            consumer.consume(entry)
        }
    }

    static PredicateConsumer of(Closure filter, Closure consumer) {
        return new PredicateConsumer(new ClosurePredicate(filter), new ClosureConsumer(consumer))
    }

    static PredicateConsumer of(Closure filter, LogConsumer consumer) {
        return new PredicateConsumer(new ClosurePredicate(filter), consumer)
    }

    static PredicateConsumer of(Predicate<LogEntry> filter, LogConsumer consumer) {
        return new PredicateConsumer(filter, consumer)
    }
}
