package org.prismus.scrambler.log

import groovy.transform.CompileStatic

/**
 * @author Serge Pruteanu
 */
@CompileStatic
class ConsumerBuilder {
    LogContext.Builder contextBuilder
    private def consumer
    private Object[] args

    Map<String, Object> consumerProperties

    ConsumerBuilder() {
    }

    ConsumerBuilder(LogContext.Builder contextBuilder, def consumer, Object... args) {
        this.args = args
        this.contextBuilder = contextBuilder
        this.consumer = consumer
    }

    ConsumerBuilder forConsumer(def consumer) {
        this.consumer = consumer
        return this
    }

    ConsumerBuilder withConsumerProperties(Map<String, Object> consumerProperties) {
        this.consumerProperties = consumerProperties
        return this
    }

    LogContext.Builder endBuilder() {
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

    LogConsumer build() {
        LogConsumer result = newConsumer(consumer, args)
        if (consumerProperties) {
            DefaultObjectProvider.setInstanceProperties(result, consumerProperties)
        }
        return result
    }

}
