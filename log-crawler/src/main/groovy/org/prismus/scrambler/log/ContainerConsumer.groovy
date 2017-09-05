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

/**
 * @author Serge Pruteanu
 */
@CompileStatic
class ContainerConsumer implements LogConsumer {
    private List<LogConsumer> consumers

    ContainerConsumer() {
        this(new ArrayList<LogConsumer>())
    }

    ContainerConsumer(List<LogConsumer> consumers) {
        this.consumers = consumers
    }

    ContainerConsumer using(LogConsumer logConsumer) {
        consumers.add(logConsumer)
        return this
    }

    ContainerConsumer using(LogConsumer... logConsumers) {
        this.consumers = logConsumers.toList()
        return this
    }

    ContainerConsumer using(Closure... logConsumers) {
        for (Closure closure : logConsumers) {
            using(new ClosureConsumer(closure))
        }
        return this
    }

    ContainerConsumer using(List<LogConsumer> logConsumers) {
        this.consumers = logConsumers
        return this
    }

    ContainerConsumer addAll(LogConsumer... consumers) {
        this.consumers.addAll(consumers)
        return this
    }

    ContainerConsumer add(LogConsumer consumer) {
        consumers.add(consumer)
        return this
    }

    ContainerConsumer addAll(Closure... consumers) {
        for (Closure closure : consumers) {
            add(new ClosureConsumer(closure))
        }
        return this
    }

    @Override
    void consume(LogEntry entry) {
        for (LogConsumer consumer : consumers) {
            consumer.consume(entry)
        }
    }

    static ContainerConsumer of(List<LogConsumer> logConsumers) {
        return new ContainerConsumer(logConsumers)
    }

    static ContainerConsumer of(LogConsumer... logConsumers) {
        return new ContainerConsumer().using(logConsumers)
    }

    static ContainerConsumer of(Closure... logConsumers) {
        return new ContainerConsumer().using(logConsumers)
    }
}
