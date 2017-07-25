package org.prismus.scrambler.log

import groovy.transform.CompileStatic

import java.text.SimpleDateFormat

/**
 * @author Serge Pruteanu
 */
@CompileStatic
class RegexConsumerBuilder extends ConsumerBuilder {
    protected final Map<String, List> groupProcessorMap = new LinkedHashMap<>()
    private Map<String, Integer> groupIndexMap

    RegexConsumerBuilder() {
    }

    RegexConsumerBuilder(LogContext.Builder contextBuilder, def consumer) {
        super(contextBuilder, consumer)
    }

    RegexConsumerBuilder indexedGroup(String group, Integer index = null) {
        groupIndexMap.put(group, index)
        return this
    }

    RegexConsumerBuilder registerAll(Map<String, Integer> groupIndexMap) {
        this.groupIndexMap = groupIndexMap
        return this
    }

    RegexConsumerBuilder groupConsumer(String group, LogConsumer consumer) {
        if (!groupProcessorMap.containsKey(group)) {
            groupProcessorMap.put(group, new ArrayList())
        }
        groupProcessorMap.get(group).add(consumer)
        return this
    }

    RegexConsumerBuilder group(String group, LogConsumer consumer) {
        indexedGroup(group, null)
        groupConsumer(group, consumer)
        return this
    }

    RegexConsumerBuilder dateFormatGroup(String group, String dateFormat) {
        return dateFormatGroup(group, new SimpleDateFormat(dateFormat))
    }

    RegexConsumerBuilder dateFormatGroup(String group, SimpleDateFormat dateFormat) {
        indexedGroup(group, (Integer)null)
        groupConsumer(group, new DateFormatConsumer(dateFormat, group))
        return this
    }

    RegexConsumerBuilder messageGroup(String group) {
        indexedGroup(group, (Integer)null)
        groupConsumer(group, new MessageConsumer(group))
        return this
    }

    GroupConsumerBuilder newBuilder(String group, def consumer, Object... args) {
        return new GroupConsumerBuilder(group, consumer, args)
    }

    protected void buildGroupConsumers(RegexConsumer result) {
        for (Map.Entry<String, List> entry : groupProcessorMap.entrySet()) {
            result.groupConsumer(entry.key, newConsumer(entry.value))
        }
    }

    @Override
    protected RegexConsumer buildConsumer() {
        final result = super.buildConsumer() as RegexConsumer
        if (groupIndexMap) {
            result.indexedGroups(groupIndexMap)
        }
        buildGroupConsumers(result)
        return result
    }

    class GroupConsumerBuilder extends ConsumerBuilder {
        private String group

        private GroupConsumerBuilder(String group, def consumer, Object... args) {
            super(RegexConsumerBuilder.this.contextBuilder, consumer, args)
            this.group = group
        }

        RegexConsumerBuilder endGroup() {
            RegexConsumerBuilder.this.groupConsumer(group, build())
            return RegexConsumerBuilder.this
        }

        LogContext.Builder endBuilder() {
            RegexConsumerBuilder.this.groupConsumer(group, build())
            return contextBuilder
        }

    }

}
