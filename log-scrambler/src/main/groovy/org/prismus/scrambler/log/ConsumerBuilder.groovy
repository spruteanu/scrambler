package org.prismus.scrambler.log

import groovy.transform.CompileStatic

/**
 * @author Serge Pruteanu
 */
@CompileStatic
class ConsumerBuilder {
    LogCrawler.Builder contextBuilder
    private def consumer
    private Object[] args

    Map<String, Object> consumerProperties

    ConsumerBuilder() {
    }

    ConsumerBuilder(LogCrawler.Builder contextBuilder, def consumer, Object... args) {
        this.args = args
        this.contextBuilder = contextBuilder
        this.consumer = consumer
    }

    ConsumerBuilder withConsumer(def consumer) {
        this.consumer = consumer
        return this
    }

    ConsumerBuilder withProperties(Map<String, Object> consumerProperties) {
        this.consumerProperties = consumerProperties
        return this
    }

    LogCrawler.Builder recurContext() {
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
