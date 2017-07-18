package org.prismus.scrambler.log

import groovy.transform.CompileStatic

import java.util.concurrent.TimeUnit

/**
 * @author Serge Pruteanu
 */
@CompileStatic
class ConsumerBuilder {
    ContextBuilder contextBuilder
    Map<String, Object> consumerProperties
    boolean asynchronous
    int timeout
    TimeUnit unit = TimeUnit.MILLISECONDS

    private def consumer
    private Object[] args

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
        asynchronous = true
        return this
    }

    ConsumerBuilder withConsumerProperties(Map<String, Object> consumerProperties) {
        this.consumerProperties = consumerProperties
        return this
    }

    ContextBuilder endContext() {
        contextBuilder.endContext()
        return contextBuilder
    }

    ConsumerBuilder endConsumer() {
        return this
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
        return (asynchronous ? contextBuilder.newAsynchronousConsumer(result) : result)
    }

    LogConsumer build() {
        return checkAsynchronousConsumer(buildConsumer())
    }

}
