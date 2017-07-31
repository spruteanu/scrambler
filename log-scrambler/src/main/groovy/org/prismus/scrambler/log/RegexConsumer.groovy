package org.prismus.scrambler.log

import groovy.transform.CompileStatic

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

    RegexConsumer groups(Map<String, Object> groups) {
        Objects.requireNonNull(groups, 'Groups must be provided')
        int i = 0
        for (Map.Entry<String, Object> entry : groups.entrySet()) {
            final consumer = entry.value
            final groupName = entry.key
            if (consumer instanceof Closure) {
                group(groupName, i + 1, new ClosureConsumer(consumer as Closure))
            } else if (consumer instanceof LogConsumer) {
                group(groupName, i + 1, consumer as LogConsumer)
            } else {
                group(groupName, i + 1)
            }
        }
        return this
    }

    RegexConsumer indexedGroups(Map<String, Integer> groupIndexMap) {
        Objects.requireNonNull(groupIndexMap, 'Group value map should not be null')
        this.groupIndexMap.putAll(groupIndexMap)
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

    protected static Map<String, String> toMap(Pattern pattern, String line, Map<String, Integer> groupIndexMap) {
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

    static RegexConsumer of(Pattern pattern) {
        return new RegexConsumer(pattern)
    }

    static RegexConsumer of(String regEx, int flags) {
        return new RegexConsumer(Pattern.compile(regEx, flags))
    }

}
