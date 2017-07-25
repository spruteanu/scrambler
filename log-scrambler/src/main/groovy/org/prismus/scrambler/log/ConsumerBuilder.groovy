package org.prismus.scrambler.log

import groovy.transform.CompileStatic

import java.util.concurrent.TimeUnit

/**
 * @author Serge Pruteanu
 */
@CompileStatic
class ConsumerBuilder {
    ContextBuilder contextBuilder
    private def consumer
    private Object[] args

    Map<String, Object> consumerProperties
    boolean asynchronous
    int timeout
    TimeUnit unit = TimeUnit.MILLISECONDS

    ConsumerBuilder() {
    }

    ConsumerBuilder(ContextBuilder contextBuilder, def consumer, Object... args) {
        this.args = args
        this.contextBuilder = contextBuilder
        this.consumer = consumer
    }

    ConsumerBuilder forConsumer(def consumer) {
        this.consumer = consumer
        return this
    }

    ConsumerBuilder asynchronous(int timeout = 0, TimeUnit unit = TimeUnit.MILLISECONDS) {
        this.timeout = timeout
        this.unit = unit
        asynchronous = true
        return this
    }

    ConsumerBuilder withConsumerProperties(Map<String, Object> consumerProperties) {
        this.consumerProperties = consumerProperties
        return this
    }

    ContextBuilder endBuilder() {
        return contextBuilder
    }

    protected LogConsumer newConsumer(def object, Object[] objArgs) {
        LogConsumer result
        if (object instanceof LogConsumer) {
            result = object as LogConsumer
        } else if (object instanceof ConsumerBuilder) {
            result = ((ConsumerBuilder) object).build()
        } else {
            result = contextBuilder.getConsumer(object, objArgs)
        }
        return result
    }

    protected LogConsumer buildConsumer() {
        LogConsumer result = newConsumer(consumer, args)
        if (consumerProperties) {
            DefaultObjectProvider.setInstanceProperties(result, consumerProperties)
        }
        return result
    }

    protected LogConsumer checkAsynchronousConsumer(LogConsumer result) {
        return (asynchronous ? contextBuilder.newAsynchronousConsumer(result, timeout, unit) : result)
    }

    LogConsumer build() {
        return checkAsynchronousConsumer(buildConsumer())
    }

}
