package org.prismus.scrambler.log

import groovy.transform.CompileStatic

import java.util.concurrent.TimeUnit

/**
 * @author Serge Pruteanu
 */
@CompileStatic
class ConsumerBuilder<T extends LogConsumer> {
    ContextBuilder contextBuilder
    Map<String, Object> consumerProperties
    boolean asynchronous
    int timeout
    TimeUnit unit = TimeUnit.MILLISECONDS

    private T consumer

    ConsumerBuilder() {
    }

    ConsumerBuilder(ContextBuilder contextBuilder, T consumer) {
        this.contextBuilder = contextBuilder
        this.consumer = consumer
    }

    ConsumerBuilder forConsumer(T consumer) {
        this.consumer = consumer
        return this
    }

    ConsumerBuilder asynchronous(int timeout = 0, TimeUnit unit = TimeUnit.MILLISECONDS) {
        asynchronous = true
        return this
    }

    ConsumerBuilder withConsumerProperties(Map<String, Object> consumerProperties) {
        this.consumerProperties = consumerProperties
        return this
    }

    ContextBuilder end() {
        return contextBuilder
    }

    LogConsumer build() {
        if (consumerProperties) {
            DefaultObjectProvider.setInstanceProperties(consumer, consumerProperties)
        }
        return (asynchronous ? contextBuilder.newAsychronousConsumer(consumer, timeout, unit) : consumer) as T
    }

}
