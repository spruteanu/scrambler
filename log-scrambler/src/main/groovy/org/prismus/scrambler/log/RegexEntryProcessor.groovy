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
class RegexEntryProcessor implements EntryProcessor {
    @PackageScope
    static final String EXCEPTION_REGEX = '^.+Exception[^\\n]++(?:\\s+at .++)+'

    Pattern pattern
    protected final ArrayListMultimap<String, EntryProcessor> groupProcessorMap = ArrayListMultimap.create()
    protected final Map<String, Integer> groupIndexMap = [:]

    String group

    RegexEntryProcessor() {
    }

    RegexEntryProcessor(String regEx, int flags = 0, Object group = null) {
        this(Pattern.compile(regEx, flags), group)
    }

    RegexEntryProcessor(Pattern pattern, Object group = null) {
        this.pattern = pattern
        this.group = group
    }

    RegexEntryProcessor register(String group, Integer index = null, EntryProcessor entryProcessor = null) {
        Preconditions.checkArgument(index > 0, 'Group index should be a positive number')
        Preconditions.checkNotNull(group, 'Group value name should be provided')
        groupIndexMap.put(group, index)
        if (entryProcessor) {
            groupProcessorMap.put(group, entryProcessor)
        }
        return this
    }

    RegexEntryProcessor register(String group, EntryProcessor entryProcessor) {
        Preconditions.checkNotNull(group, "Group Name can't be null")
        Preconditions.checkNotNull(entryProcessor, 'Entry Processor instance should be provided')
        groupProcessorMap.put(group, entryProcessor)
        groupIndexMap.put(group, null)
        return this
    }

    RegexEntryProcessor registerProcessor(String group, EntryProcessor entryProcessor) {
        Preconditions.checkNotNull(group, "Group Name can't be null")
        Preconditions.checkNotNull(entryProcessor, 'Entry Processor instance should be provided')
        groupProcessorMap.put(group, entryProcessor)
        return this
    }

    RegexEntryProcessor registerAll(Map<String, Integer> groupIndexMap) {
        Preconditions.checkNotNull(groupIndexMap, 'Group value map should not be null')
        this.groupIndexMap.putAll(groupIndexMap)
        return this
    }

    @Override
    LogEntry process(LogEntry entry) {
        final Matcher matcher
        if (group) {
            final value = entry.getEntryValue(group)
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
                    entry.putEntryValue(key, groupValue)
                    final List<EntryProcessor> entryProcessors = groupProcessorMap.get(key)
                    for (EntryProcessor processor : entryProcessors) {
                        entry = processor.process(entry)
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

    static RegexEntryProcessor of(Pattern pattern) {
        return new RegexEntryProcessor(pattern)
    }

    static RegexEntryProcessor of(String regEx, int flags = 0) {
        return new RegexEntryProcessor(Pattern.compile(regEx, flags))
    }

}
