package org.prismus.scrambler.log

import groovy.transform.CompileStatic

/**
 * @author Serge Pruteanu
 */
@CompileStatic
class Log4jConsumerBuilder extends RegexConsumerBuilder<Log4JConsumer> {
    Log4jConsumerBuilder() {
    }

    Log4jConsumerBuilder(ContextBuilder contextBuilder, Log4JConsumer consumer) {
        super(contextBuilder, consumer)
    }

}
