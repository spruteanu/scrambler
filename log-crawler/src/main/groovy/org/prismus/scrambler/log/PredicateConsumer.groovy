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

import java.util.function.Predicate

/**
 * @author Serge Pruteanu
 */
@CompileStatic
class PredicateConsumer implements LogConsumer {
    final LogConsumer consumer
    final Predicate<LogEntry> filter

    PredicateConsumer(@DelegatesTo(LogEntry) Closure<Boolean> filter, @DelegatesTo(LogEntry) Closure consumer) {
        this(new ClosurePredicate(filter), new ClosureConsumer(consumer))
    }

    PredicateConsumer(@DelegatesTo(LogEntry) Closure<Boolean> filter, LogConsumer consumer) {
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

    static PredicateConsumer of(@DelegatesTo(LogEntry) Closure<Boolean> filter, @DelegatesTo(LogEntry) Closure consumer) {
        return new PredicateConsumer(new ClosurePredicate(filter), new ClosureConsumer(consumer))
    }

    static PredicateConsumer of(@DelegatesTo(LogEntry) Closure<Boolean> filter, LogConsumer consumer) {
        return new PredicateConsumer(new ClosurePredicate(filter), consumer)
    }

    static PredicateConsumer of(Predicate<LogEntry> filter, LogConsumer consumer) {
        return new PredicateConsumer(filter, consumer)
    }
}
