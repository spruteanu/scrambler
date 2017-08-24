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
import groovy.transform.stc.ClosureParams
import groovy.transform.stc.SimpleType

import java.nio.file.Path
import java.text.SimpleDateFormat
import java.util.regex.Matcher
import java.util.regex.Pattern

/**
 * @author Serge Pruteanu
 */
@CompileStatic
class RegexConsumer implements LogConsumer {

    Pattern pattern
    protected final Map<String, List<LogConsumer>> consumerMap = new LinkedHashMap<>()
    protected final Map<String, Integer> groupIndexMap = new LinkedHashMap<>()

    String group

    RegexConsumer() {
    }

    RegexConsumer(String regEx, int flags = 0, Object group = null) {
        this(Pattern.compile(regEx, flags), group)
    }

    RegexConsumer(Pattern pattern, Object group = null) {
        this.group = group
        setPattern(pattern)
    }

    void setPattern(Pattern pattern) {
        this.pattern = pattern
        final groupNames = lookupNamedGroups(pattern.pattern())
        if (groupNames) {
            groups(groupNames.toArray(new String[0]))
        }
    }

    protected void add(String group, LogConsumer consumer) {
        if (!consumerMap.containsKey(group)) {
            consumerMap.put(group, new ArrayList<LogConsumer>())
        }
        consumerMap.get(group).add(consumer)
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
            add(group, consumer)
        }
        return this
    }

    RegexConsumer group(String groupName, @ClosureParams(value=SimpleType.class, options="org.prismus.scrambler.log.LogEntry") Closure closure) {
        return group(groupName, new ClosureConsumer(closure))
    }

    RegexConsumer group(String groupName, LogConsumer consumer) {
        Objects.requireNonNull(groupName, "Group Name can't be null")
        Objects.requireNonNull(consumer, 'Entry consumer instance should be provided')
        add(groupName, consumer)
        return this
    }

    private List<LogConsumer> get(String key) {
        return consumerMap.containsKey(key) ? consumerMap.get(key) : Collections.<LogConsumer>emptyList()
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
        // todo Serge: replace it with org.prismus.scrambler.log.RegexConsumer.toMap(java.util.regex.Pattern, java.lang.String, java.util.Map<java.lang.String,java.lang.Integer>)
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
                    final List<LogConsumer> consumers = get(key)
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

    static Map<String, ?> toMap(Pattern pattern, String line, Map<String, Integer> groupIndexMap) {
        final resultMap = [:]
        final Matcher matcher = pattern.matcher(line)
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
                    def entryValue = groupValue.trim()
                    if (resultMap.containsKey(key)) {
                        def val = resultMap.get(key)
                        final List list
                        if (val instanceof List) {
                            list = val as List
                        } else {
                            list = []
                            list.add(val)
                        }
                        list.add(groupValue)
                        entryValue = list
                    }
                    resultMap.put(key, entryValue)
                }
            }
        }
        return resultMap
    }

    static Map<String, ?> toMap(Pattern pattern, String line, String... groups) {
        int i = 1
        return toMap(pattern, line, groups.collectEntries { [it, i++] })
    }

    static RegexConsumer of(Pattern pattern) {
        return new RegexConsumer(pattern)
    }

    static RegexConsumer of(String regEx, int flags) {
        return new RegexConsumer(Pattern.compile(regEx, flags))
    }

    protected static Set<String> lookupNamedGroups(String regex) {
        final namedGroups = new LinkedHashSet<>()
        final matcher = Pattern.compile("\\(\\?<([a-zA-Z][a-zA-Z0-9]*)>").matcher(regex)
        while (matcher.find()) {
            namedGroups.add(matcher.group(1))
        }
        return namedGroups
    }

    /**
     * @author Serge Pruteanu
     */
    @CompileStatic
    static class Builder extends ConsumerBuilder {
        protected final Map<String, List> consumerMap = new LinkedHashMap<>()
        private final Map<String, Integer> groupIndexMap  = new LinkedHashMap<>()

        File path
        String pattern
        String fileFilter
        Comparator<Path> fileSorter = LogCrawler.CREATED_DT_COMPARATOR

        Builder() {
        }

        void path(String path) {
            this.path = new File(path)
        }

        void pattern(String pattern) {
            this.pattern = pattern
        }

        void fileFilter(String fileFilter) {
            this.fileFilter = fileFilter
        }

        void fileSorter(Comparator<Path> fileSorter) {
            this.fileSorter = fileSorter
        }

        Builder(LogCrawler.Builder contextBuilder, def consumer) {
            super(contextBuilder, consumer)
        }

        Builder group(String groupName, Integer index = null, LogConsumer consumer = null) {
            groupIndexMap.put(groupName, index)
            if (consumer) {
                group(groupName, consumer)
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

        Builder group(String groupName, LogConsumer consumer) {
            if (!consumerMap.containsKey(groupName)) {
                consumerMap.put(groupName, new ArrayList())
            }
            consumerMap.get(groupName).add(consumer)
            return this
        }

        Builder group(String groupName, Closure logEntryClosure) {
            return group(groupName, new ClosureConsumer(logEntryClosure))
        }

        @SuppressWarnings("GrUnnecessaryPublicModifier")
        public <T> Builder group(String groupName, T consumer, Object[] args = null, @DelegatesTo(T) Closure closure = null) {
            final logConsumer = new ConsumerBuilder(contextBuilder, consumer, args).build()
            if (closure) {
                closure.setDelegate(logConsumer)
                closure.call()
            }
            group(groupName, logConsumer)
            return this
        }

        Builder date(String group, String dateFormat, String target = null) {
            return date(group, new SimpleDateFormat(dateFormat), target)
        }

        Builder date(String field, SimpleDateFormat dateFormat, String target = null) {
            group(field, (Integer)null, new DateConsumer(dateFormat, field, target))
            return this
        }

        Builder exception(String groupName) {
            group(groupName, (Integer)null, new ExceptionConsumer(groupName))
            return this
        }

        protected void buildConsumers(RegexConsumer result) {
            for (Map.Entry<String, List> entry : consumerMap.entrySet()) {
                result.group(entry.key, newConsumer(entry.value))
            }
        }

        protected RegexConsumer build() {
            final RegexConsumer result = super.build() as RegexConsumer
            buildConsumers(result)
            return result
        }
    }
}
