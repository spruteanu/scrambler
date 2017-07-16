package org.prismus.scrambler.log

import com.google.common.base.Preconditions
import com.google.common.collect.ArrayListMultimap
import groovy.transform.CompileStatic
import groovy.transform.PackageScope

import java.util.regex.Matcher
import java.util.regex.Pattern

/**
 * @author Serge Pruteanu
 */
@CompileStatic
class RegexProcessor implements LogProcessor {

    Pattern pattern
    protected final ArrayListMultimap<String, LogProcessor> groupProcessorMap = ArrayListMultimap.create()
    protected final Map<String, Integer> groupIndexMap = [:]

    String group

    RegexProcessor() {
    }

    RegexProcessor(String regEx, int flags = 0, Object group = null) {
        this(Pattern.compile(regEx, flags), group)
    }

    RegexProcessor(Pattern pattern, Object group = null) {
        this.pattern = pattern
        this.group = group
    }

    RegexProcessor register(String group, Integer index = null, LogProcessor processor = null) {
        Preconditions.checkArgument(index > 0, 'Group index should be a positive number')
        Preconditions.checkNotNull(group, 'Group value name should be provided')
        groupIndexMap.put(group, index)
        if (processor) {
            groupProcessorMap.put(group, processor)
        }
        return this
    }

    RegexProcessor register(String group, LogProcessor processor) {
        Preconditions.checkNotNull(group, "Group Name can't be null")
        Preconditions.checkNotNull(processor, 'Entry Processor instance should be provided')
        groupProcessorMap.put(group, processor)
        groupIndexMap.put(group, null)
        return this
    }

    RegexProcessor registerProcessor(String group, LogProcessor processor) {
        Preconditions.checkNotNull(group, "Group Name can't be null")
        Preconditions.checkNotNull(processor, 'Entry Processor instance should be provided')
        groupProcessorMap.put(group, processor)
        return this
    }

    RegexProcessor registerAll(Map<String, Integer> groupIndexMap) {
        Preconditions.checkNotNull(groupIndexMap, 'Group value map should not be null')
        this.groupIndexMap.putAll(groupIndexMap)
        return this
    }

    @Override
    LogEntry process(LogEntry entry) {
        final Matcher matcher
        if (group) {
            final value = entry.getLogValue(group)
            if (!value) {
                return entry
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
                    final List<LogProcessor> processors = groupProcessorMap.get(key)
                    for (LogProcessor processor : processors) {
                        processor.process(entry)
                    }
                }
            }
        }
        return entry
    }

    static String dateFormatToRegEx(String dateFormat) {
        String result = dateFormat
        result = result.replaceAll('[w]+', '\\\\w+')
        result = result.replaceAll('[WDdFuHkKhmsSyYGMEazZX]+', '\\\\w+')
        result = result.replaceAll(',', '\\.')
        return result
    }

    static RegexProcessor of(Pattern pattern) {
        return new RegexProcessor(pattern)
    }

    static RegexProcessor of(String regEx, int flags = 0) {
        return new RegexProcessor(Pattern.compile(regEx, flags))
    }

}
