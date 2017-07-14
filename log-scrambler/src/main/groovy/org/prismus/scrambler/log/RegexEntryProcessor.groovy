package org.prismus.scrambler.log

import com.google.common.base.Preconditions
import com.google.common.collect.ArrayListMultimap
import com.google.common.collect.BiMap
import com.google.common.collect.HashBiMap
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
    protected final ArrayListMultimap<Object, EntryProcessor> groupProcessorMap = ArrayListMultimap.create()
    protected final BiMap<Object, String> groupValueMap = HashBiMap.create()

    Object entryValueKey

    RegexEntryProcessor() {
    }

    RegexEntryProcessor(String regEx, int flags = 0, Object entryValueKey = null) {
        this(Pattern.compile(regEx, flags), entryValueKey)
    }

    RegexEntryProcessor(Pattern pattern, Object entryValueKey = null) {
        this.pattern = pattern
        this.entryValueKey = entryValueKey
    }

    RegexEntryProcessor register(int group, String groupNameValue) {
        Preconditions.checkArgument(group > 0, 'Group index should be a positive number')
        Preconditions.checkNotNull(groupNameValue, 'Group value name should be provided')
        groupValueMap.put(group, groupNameValue)
        return this
    }

    RegexEntryProcessor register(int group, EntryProcessor entryProcessor) {
        Preconditions.checkArgument(group > 0, 'Group index should be a positive number')
        Preconditions.checkNotNull(entryProcessor, 'Entry Processor instance should be provided')
        groupProcessorMap.put(group, entryProcessor)
        return this
    }

    RegexEntryProcessor register(String group, EntryProcessor entryProcessor) {
        Preconditions.checkNotNull(group, "Group Name can't be null")
        Preconditions.checkNotNull(entryProcessor, 'Entry Processor instance should be provided')
        groupProcessorMap.put(group, entryProcessor)
        groupValueMap.put(group, group)
        return this
    }

    RegexEntryProcessor registerProcessor(String group, EntryProcessor entryProcessor) {
        Preconditions.checkNotNull(group, "Group Name can't be null")
        Preconditions.checkNotNull(entryProcessor, 'Entry Processor instance should be provided')
        Object procId = group
        if (!groupValueMap.containsKey(group)) {
            final inverseMap = groupValueMap.inverse()
            if (inverseMap.containsKey(group)) {
                procId = inverseMap.get(group)
            }
        }
        groupProcessorMap.put(procId, entryProcessor)
        return this
    }

    RegexEntryProcessor registerAll(Map<Object, String> groupNameValueMap) {
        Preconditions.checkNotNull(groupNameValueMap, 'Group value map should not be null')
        this.groupValueMap.putAll(groupNameValueMap)
        return this
    }

    @Override
    LogEntry process(LogEntry entry) {
        final Matcher matcher
        if (entryValueKey) {
            final value = entry.getEntryValue(entryValueKey)
            if (!value) {
                return entry
            }
            matcher = pattern.matcher(value.toString())
        } else {
            matcher = pattern.matcher(entry.line)
        }
        while (matcher.find()) {
            for (final key : (groupProcessorMap.keySet() + groupValueMap.keySet())) {
                String groupValue = null
                try {
                    if (key instanceof String) {
                        groupValue = matcher.group(key as String)
                    } else if (key instanceof Integer) {
                        groupValue = matcher.group(key as Integer)
                    }
                } catch (Exception ignore) { }
                if (groupValue) {
                    if (groupValueMap.containsKey(key)) {
                        entry.putEntryValue(groupValueMap.get(key), groupValue)
                    }
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
