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

    ContainerConsumer withConsumer(LogConsumer logConsumer) {
        consumers.add(logConsumer)
        return this
    }

    ContainerConsumer withConsumers(LogConsumer... logConsumers) {
        this.consumers = logConsumers.toList()
        return this
    }

    ContainerConsumer withConsumers(Closure... logConsumers) {
        for (Closure closure : logConsumers) {
            withConsumer(new ClosureConsumer(closure))
        }
        return this
    }

    ContainerConsumer withConsumers(List<LogConsumer> logConsumers) {
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
        return new ContainerConsumer().withConsumers(logConsumers)
    }

    static ContainerConsumer of(Closure... logConsumers) {
        return new ContainerConsumer().withConsumers(logConsumers)
    }
}
