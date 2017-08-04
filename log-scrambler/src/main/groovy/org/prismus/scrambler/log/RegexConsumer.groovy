/*
 * Log crawler, tool that allows to extract/crawl log files for further analysis
 *
 * Copyright (c) 2015, Sergiu Prutean. All rights reserved.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3.0 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library.
 */

package org.prismus.scrambler.log

import groovy.transform.CompileStatic

import java.text.SimpleDateFormat
import java.util.regex.Matcher
import java.util.regex.Pattern

/**
 * @author Serge Pruteanu
 */
@CompileStatic
class RegexConsumer implements LogConsumer {

    Pattern pattern
    protected final Map<String, List<LogConsumer>> groupConsumerMap = new LinkedHashMap<>()
    protected final Map<String, Integer> groupIndexMap = new LinkedHashMap<>()

    String group

    RegexConsumer() {
    }

    RegexConsumer(String regEx, int flags = 0, Object group = null) {
        this(Pattern.compile(regEx, flags), group)
    }

    RegexConsumer(Pattern pattern, Object group = null) {
        this.pattern = pattern
        this.group = group
    }

    private void addConsumer(String group, LogConsumer consumer) {
        if (!groupConsumerMap.containsKey(group)) {
            groupConsumerMap.put(group, new ArrayList<LogConsumer>())
        }
        groupConsumerMap.get(group).add(consumer)
    }

    RegexConsumer groups(String... groups) {
        Objects.requireNonNull(groups, 'Groups must be provided')
        for (int i = 0; i < groups.length; i++) {
            group(groups[i], i + 1)
        }
        return this
    }

    RegexConsumer group(String group, Integer index = null, LogConsumer consumer = null) {
        assert index == null || index > 0, 'Group index should be a positive number'
        Objects.requireNonNull(group, 'Group value name should be provided')
        groupIndexMap.put(group, index)
        if (consumer) {
            addConsumer(group, consumer)
        }
        return this
    }

    RegexConsumer group(String group, LogConsumer consumer) {
        Objects.requireNonNull(group, "Group Name can't be null")
        Objects.requireNonNull(consumer, 'Entry consumer instance should be provided')
        addConsumer(group, consumer)
        groupIndexMap.put(group, null)
        return this
    }

    RegexConsumer group(String groupName, Closure closure) {
        return group(groupName, new ClosureConsumer(closure))
    }

    RegexConsumer withGroupConsumer(String group, LogConsumer consumer) {
        Objects.requireNonNull(group, "Group Name can't be null")
        Objects.requireNonNull(consumer, 'Entry consumer instance should be provided')
        addConsumer(group, consumer)
        return this
    }

    RegexConsumer withGroupConsumer(String group, Closure closure) {
        return withGroupConsumer(group, new ClosureConsumer(closure))
    }

    private List<LogConsumer> getConsumer(String key) {
        return groupConsumerMap.containsKey(key) ? groupConsumerMap.get(key) : Collections.<LogConsumer>emptyList()
    }

    @Override
    void consume(LogEntry entry) {
        final Matcher matcher
        if (group) {
            final value = entry.get(group)
            if (!value) {
                return
            }
            matcher = pattern.matcher(value.toString())
        } else {
            matcher = pattern.matcher(entry.line)
        }
        while (matcher.find()) {
            for (Map.Entry<String, Integer> enr : groupIndexMap.entrySet()) {
                final key = enr.key
                Integer idx = enr.value
                String groupValue = null
                try {
                    if (idx) {
                        groupValue = matcher.group(idx)
                    } else {
                        groupValue = matcher.group(key)
                    }
                } catch (Exception ignore) { }
                if (groupValue) {
                    entry.put(key, groupValue.trim())
                    final List<LogConsumer> consumers = getConsumer(key)
                    for (LogConsumer consumer : consumers) {
                        consumer.consume(entry)
                    }
                }
            }
        }
    }

    protected static String dateFormatToRegEx(String dateFormat) {
        String result = dateFormat
        result = result.replaceAll('[w]+', '\\\\w+')
        result = result.replaceAll('[WDdFuHkKhmsSyYGMEazZX]+', '\\\\w+')
        result = result.replaceAll(',', '\\.')
        return result
    }

    static Map<String, String> toMap(Pattern pattern, String line, Map<String, Integer> groupIndexMap) {
        final resultMap = [:]
        final Matcher matcher
        matcher = pattern.matcher(line)
        while (matcher.find()) {
            for (Map.Entry<String, Integer> enr : groupIndexMap.entrySet()) {
                final key = enr.key
                Integer idx = enr.value
                String groupValue
                if (idx) {
                    groupValue = matcher.group(idx)
                } else {
                    groupValue = matcher.group(key)
                }
                if (groupValue) {
                    resultMap.put(key, groupValue.trim())
                }
            }
        }
        return resultMap
    }

    static Map<String, String> toMap(Pattern pattern, String line, String... groups) {
        final resultMap = [:]
        final Matcher matcher
        matcher = pattern.matcher(line)
        int i = 1
        while (matcher.find()) {
            for (String key : groups) {
                String groupValue = matcher.group(i++)
                if (groupValue) {
                    resultMap.put(key, groupValue.trim())
                }
            }
        }
        return resultMap
    }

    static RegexConsumer of(Pattern pattern) {
        return new RegexConsumer(pattern)
    }

    static RegexConsumer of(String regEx, int flags) {
        return new RegexConsumer(Pattern.compile(regEx, flags))
    }

    /**
     * @author Serge Pruteanu
     */
    @CompileStatic
    static class Builder extends ConsumerBuilder {
        protected final Map<String, List> groupProcessorMap = new LinkedHashMap<>()
        private final Map<String, Integer> groupIndexMap  = new LinkedHashMap<>()

        Builder() {
        }

        Builder(LogCrawler.Builder contextBuilder, def consumer) {
            super(contextBuilder, consumer)
        }

        Builder group(String group, Integer index = null, LogConsumer consumer = null) {
            groupIndexMap.put(group, index)
            if (consumer) {
                withConsumer(group, consumer)
            }
            return this
        }

        Builder groups(String... groups) {
            Objects.requireNonNull(groups, 'Groups must be provided')
            for (int i = 0; i < groups.length; i++) {
                group(groups[i], i + 1)
            }
            return this
        }

        Builder withConsumer(String group, LogConsumer consumer) {
            if (!groupProcessorMap.containsKey(group)) {
                groupProcessorMap.put(group, new ArrayList())
            }
            groupProcessorMap.get(group).add(consumer)
            return this
        }

        Builder withConsumer(String group, Closure logEntryClosure) {
            return withConsumer(group, new ClosureConsumer(logEntryClosure))
        }

        GroupConsumerBuilder withGroupBuilder(String group, def consumer, Object... args) {
            return new GroupConsumerBuilder(group, consumer, args)
        }

        Builder toDateConsumer(String group, String dateFormat) {
            return toDateConsumer(group, new SimpleDateFormat(dateFormat))
        }

        Builder toDateConsumer(String groupName, SimpleDateFormat dateFormat) {
            group(groupName, (Integer)null, new DateConsumer(dateFormat, groupName))
            return this
        }

        Builder toExceptionConsumer(String groupName) {
            group(groupName, (Integer)null, new ExceptionConsumer(groupName))
            return this
        }

        protected void buildGroupConsumers(RegexConsumer result) {
            for (Map.Entry<String, List> entry : groupProcessorMap.entrySet()) {
                result.withGroupConsumer(entry.key, newConsumer(entry.value))
            }
        }

        RegexConsumer build() {
            final RegexConsumer result = super.build() as RegexConsumer
            buildGroupConsumers(result)
            return result
        }

        class GroupConsumerBuilder extends ConsumerBuilder {
            private String group

            private GroupConsumerBuilder(String group, def consumer, Object... args) {
                super(Builder.this.contextBuilder, consumer, args)
                this.group = group
            }

            Builder recurBuilder() {
                Builder.this.withConsumer(group, build())
                return Builder.this
            }

            LogCrawler.Builder recurContext() {
                Builder.this.withConsumer(group, build())
                return contextBuilder
            }
        }

    }
}
