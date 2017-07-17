package org.prismus.scrambler.log

import com.google.common.base.Preconditions
import groovy.transform.CompileStatic

import java.util.regex.Matcher
import java.util.regex.Pattern

/**
 * @author Serge Pruteanu
 */
@CompileStatic
class RegexConsumer implements LogConsumer {

    Pattern pattern
    protected final Map<String, List<LogConsumer>> groupProcessorMap = new LinkedHashMap<>()
    protected final Map<String, Integer> groupIndexMap = [:]

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

    private void addProcessor(String group, LogConsumer processor) {
        if (!groupProcessorMap.containsKey(group)) {
            groupProcessorMap.put(group, new ArrayList<LogConsumer>())
        }
        groupProcessorMap.get(group).add(processor)
    }

    RegexConsumer register(String group, Integer index = null, LogConsumer processor = null) {
        Preconditions.checkArgument(index > 0, 'Group index should be a positive number')
        Preconditions.checkNotNull(group, 'Group value name should be provided')
        groupIndexMap.put(group, index)
        if (processor) {
            addProcessor(group, processor)
        }
        return this
    }

    RegexConsumer register(String group, LogConsumer processor) {
        Preconditions.checkNotNull(group, "Group Name can't be null")
        Preconditions.checkNotNull(processor, 'Entry Processor instance should be provided')
        addProcessor(group, processor)
        groupIndexMap.put(group, null)
        return this
    }

    RegexConsumer registerProcessor(String group, LogConsumer processor) {
        Preconditions.checkNotNull(group, "Group Name can't be null")
        Preconditions.checkNotNull(processor, 'Entry Processor instance should be provided')
        addProcessor(group, processor)
        return this
    }

    RegexConsumer registerAll(Map<String, Integer> groupIndexMap) {
        Preconditions.checkNotNull(groupIndexMap, 'Group value map should not be null')
        this.groupIndexMap.putAll(groupIndexMap)
        return this
    }

    private List<LogConsumer> getProcessor(String key) {
        return groupProcessorMap.containsKey(key) ? groupProcessorMap.get(key) : Collections.<LogConsumer>emptyList()
    }

    @Override
    void process(LogEntry entry) {
        final Matcher matcher
        if (group) {
            final value = entry.getLogValue(group)
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
                    entry.putLogValue(key, groupValue)
                    final List<LogConsumer> processors = getProcessor(key)
                    for (LogConsumer processor : processors) {
                        processor.process(entry)
                    }
                }
            }
        }
    }

    static String dateFormatToRegEx(String dateFormat) {
        String result = dateFormat
        result = result.replaceAll('[w]+', '\\\\w+')
        result = result.replaceAll('[WDdFuHkKhmsSyYGMEazZX]+', '\\\\w+')
        result = result.replaceAll(',', '\\.')
        return result
    }

    static RegexConsumer of(Pattern pattern) {
        return new RegexConsumer(pattern)
    }

    static RegexConsumer of(String regEx, int flags = 0) {
        return new RegexConsumer(Pattern.compile(regEx, flags))
    }

}
