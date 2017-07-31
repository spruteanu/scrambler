package org.prismus.scrambler.log

import groovy.transform.CompileStatic

/**
 * @author Serge Pruteanu
 */
@CompileStatic
class ContainerConsumer implements LogConsumer {
    private List<LogConsumer> logConsumers

    ContainerConsumer() {
        this(new ArrayList<LogConsumer>())
    }

    ContainerConsumer(List<LogConsumer> logConsumers) {
        this.logConsumers = logConsumers
    }

    ContainerConsumer withConsumer(LogConsumer logConsumer) {
        logConsumers.add(logConsumer)
        return this
    }

    ContainerConsumer withConsumers(LogConsumer... logConsumers) {
        this.logConsumers = logConsumers.toList()
        return this
    }

    ContainerConsumer withConsumers(Closure... logConsumers) {
        for (Closure closure : logConsumers) {
            withConsumer(new ClosureConsumer(closure))
        }
        return this
    }

    ContainerConsumer withConsumers(List<LogConsumer> logConsumers) {
        this.logConsumers = logConsumers
        return this
    }

    @Override
    void consume(LogEntry entry) {
        for (LogConsumer consumer : logConsumers) {
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
