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

    Log4jConsumerBuilder(LogContext.Builder contextBuilder, def consumer) {
        super(contextBuilder, consumer)
    }

    Log4jConsumerBuilder dateFormatGroup() {
        groupConsumer(Log4jConsumer.TIMESTAMP, new DateFormatConsumer())
        return this
    }

    Log4jConsumerBuilder messageGroup() {
        groupConsumer(Log4jConsumer.MESSAGE, new MessageExceptionConsumer(Log4jConsumer.MESSAGE))
        return this
    }

    protected void buildGroupConsumers(RegexConsumer instance) {
        Log4jConsumer result = instance as Log4jConsumer
        for (Map.Entry<String, List> entry : groupProcessorMap.entrySet()) {
            final consumers = entry.value
            for (Object obj : consumers) {
                final consumer = newConsumer(obj)
                if (consumer instanceof DateFormatConsumer) {
                    consumer.setDateFormat(new SimpleDateFormat(result.timestampFormat))
                }
                result.groupConsumer(entry.key, consumer)
            }
        }
    }

}
