package org.prismus.scrambler.log

import groovy.transform.CompileStatic

/**
 * @author Serge Pruteanu
 */
@CompileStatic
class RegexConsumerBuilder<R extends RegexConsumer> extends ConsumerBuilder<R> {

    RegexConsumerBuilder() {
    }

    RegexConsumerBuilder(ContextBuilder contextBuilder, R consumer) {
        super(contextBuilder, consumer)
    }

    RegexConsumerBuilder register(String group, Integer index = null, LogConsumer processor = null) {
        throw new RuntimeException()
    }

    RegexConsumerBuilder register(String group, LogConsumer processor) {
        throw new RuntimeException()
    }

    RegexConsumerBuilder registerProcessor(String group, LogConsumer processor) {
        throw new RuntimeException()
    }

    RegexConsumerBuilder registerAll(Map<String, Integer> groupIndexMap) {
        throw new RuntimeException()
    }

}
