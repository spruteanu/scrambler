package org.prismus.scrambler.log

import groovy.transform.CompileStatic

import java.text.SimpleDateFormat

/**
 * @author Serge Pruteanu
 */
@CompileStatic
class RegexConsumerBuilder extends ConsumerBuilder {
    protected final Map<String, List> groupProcessorMap = new LinkedHashMap<>()
    private final Map<String, Integer> groupIndexMap  = new LinkedHashMap<>()

    RegexConsumerBuilder() {
    }

    RegexConsumerBuilder(LogCrawler.Builder contextBuilder, def consumer) {
        super(contextBuilder, consumer)
    }

    RegexConsumerBuilder group(String group, LogConsumer consumer = null) {
        return indexedGroup(group, null, consumer)
    }

    RegexConsumerBuilder groups(String... groups) {
        Objects.requireNonNull(groups, 'Groups must be provided')
        for (int i = 0; i < groups.length; i++) {
            indexedGroup(groups[i], i + 1)
        }
        return this
    }

    RegexConsumerBuilder groups(Map<String, Object> groups) {
        Objects.requireNonNull(groups, 'Groups must be provided')
        int i = 0
        for (Map.Entry<String, Object> entry : groups.entrySet()) {
            final consumer = entry.value
            final groupName = entry.key
            if (consumer instanceof Closure) {
                indexedGroup(groupName, i + 1, new ClosureConsumer(consumer as Closure))
            } else if (consumer instanceof LogConsumer) {
                indexedGroup(groupName, i + 1, consumer as LogConsumer)
            } else {
                indexedGroup(groupName, i + 1)
            }
        }
        return this
    }

    RegexConsumerBuilder indexedGroup(String group, Integer index = null, LogConsumer consumer = null) {
        groupIndexMap.put(group, index)
        if (consumer) {
            withConsumer(group, consumer)
        }
        return this
    }

    RegexConsumerBuilder indexedGroups(Map<String, Integer> groupIndexMap) {
        this.groupIndexMap.putAll(groupIndexMap)
        return this
    }

    RegexConsumerBuilder withConsumer(String group, LogConsumer consumer) {
        if (!groupProcessorMap.containsKey(group)) {
            groupProcessorMap.put(group, new ArrayList())
        }
        groupProcessorMap.get(group).add(consumer)
        return this
    }

    RegexConsumerBuilder withConsumer(String group, Closure logEntryClosure) {
        return withConsumer(group, new ClosureConsumer(logEntryClosure))
    }

    GroupConsumerBuilder withConsumerBuilder(String group, def consumer, Object... args) {
        return new GroupConsumerBuilder(group, consumer, args)
    }

    RegexConsumerBuilder withDateConsumer(String group, String dateFormat) {
        return withDateConsumer(group, new SimpleDateFormat(dateFormat))
    }

    RegexConsumerBuilder withDateConsumer(String group, SimpleDateFormat dateFormat) {
        indexedGroup(group, (Integer)null)
        withConsumer(group, new DateConsumer(dateFormat, group))
        return this
    }

    RegexConsumerBuilder withMessageExceptionConsumer(String group) {
        indexedGroup(group, (Integer)null)
        withConsumer(group, new MessageExceptionConsumer(group))
        return this
    }

    protected void buildGroupConsumers(RegexConsumer result) {
        for (Map.Entry<String, List> entry : groupProcessorMap.entrySet()) {
            result.withGroupConsumer(entry.key, newConsumer(entry.value))
        }
    }

    RegexConsumer build() {
        final result = super.build() as RegexConsumer
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

        RegexConsumerBuilder recurBuilder() {
            RegexConsumerBuilder.this.withConsumer(group, build())
            return RegexConsumerBuilder.this
        }

        LogCrawler.Builder recurContext() {
            RegexConsumerBuilder.this.withConsumer(group, build())
            return contextBuilder
        }
    }

}
