package org.prismus.scrambler.log

import groovy.transform.CompileStatic

import java.text.SimpleDateFormat

/**
 * @author Serge Pruteanu
 */
@CompileStatic
class Log4jConsumerBuilder extends RegexConsumerBuilder {

    Log4jConsumerBuilder() {
    }

    Log4jConsumerBuilder(LogCrawler.Builder contextBuilder, def consumer) {
        super(contextBuilder, consumer)
    }

    Log4jConsumerBuilder withDateConsumer(String dateFormat = null) {
        withConsumer(Log4jConsumer.DATE, dateFormat ? DateConsumer.of(dateFormat, Log4jConsumer.DATE) : new DateConsumer(null, Log4jConsumer.DATE))
        return this
    }

    Log4jConsumerBuilder withDateConsumer(SimpleDateFormat dateFormat) {
        withConsumer(Log4jConsumer.DATE, DateConsumer.of(dateFormat, Log4jConsumer.DATE))
        return this
    }

    Log4jConsumerBuilder withMessageExceptionConsumer() {
        withConsumer(Log4jConsumer.MESSAGE, new MessageExceptionConsumer(Log4jConsumer.MESSAGE))
        return this
    }

    Log4jConsumerBuilder withMessageConsumer(Closure closure) {
        withConsumer(Log4jConsumer.MESSAGE, closure)
        return this
    }

    Log4jConsumerBuilder withMessageConsumer(LogConsumer consumer) {
        withConsumer(Log4jConsumer.MESSAGE, consumer)
        if (consumer instanceof RegexConsumer) {
            ((RegexConsumer) consumer).group = Log4jConsumer.MESSAGE
        }
        return this
    }

    protected void buildGroupConsumers(RegexConsumer instance) {
        Log4jConsumer result = instance as Log4jConsumer
        for (Map.Entry<String, List> entry : groupProcessorMap.entrySet()) {
            final consumers = entry.value
            for (Object obj : consumers) {
                final consumer = newConsumer(obj)
                if (consumer instanceof DateConsumer) {
                    consumer.setDateFormat(new SimpleDateFormat(result.dateFormat))
                }
                result.withGroupConsumer(entry.key, consumer)
            }
        }
    }

}
