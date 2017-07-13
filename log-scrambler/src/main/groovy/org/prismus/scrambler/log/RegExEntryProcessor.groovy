package org.prismus.scrambler.log

import com.google.common.base.Preconditions
import com.google.common.collect.ArrayListMultimap
import groovy.transform.CompileStatic
import groovy.transform.PackageScope

import java.util.regex.Pattern

/**
 * @author Serge Pruteanu
 */
@CompileStatic
class RegExEntryProcessor implements EntryProcessor {
    @PackageScope
    static final String EXCEPTION_REGEX = '^.+Exception[^\\n]++(?:\\s+at .++)+'

    Pattern pattern
    private final ArrayListMultimap<Object, EntryProcessor> groupProcessorMap = ArrayListMultimap.create()
    private final Map<Object, String> groupNameValueMap = [:]

    RegExEntryProcessor() {
    }

    RegExEntryProcessor(String regEx, int flags = 0) {
        this(Pattern.compile(regEx, flags))
    }

    RegExEntryProcessor(Pattern pattern) {
        this.pattern = pattern
    }

    RegExEntryProcessor register(String groupName, EntryProcessor entryProcessor) {
        Preconditions.checkNotNull(groupName, "Group Name can't be null")
        Preconditions.checkNotNull(entryProcessor, 'Entry Processor instance should be provided')
        groupProcessorMap.put(groupName, entryProcessor)
        groupNameValueMap.put(groupName, groupName)
        return this
    }

    RegExEntryProcessor register(int groupIndex, String groupNameValue) {
        Preconditions.checkArgument(groupIndex > 0, 'Group index should be a positive number')
        Preconditions.checkNotNull(groupNameValue, 'Group value name should be provided')
        groupNameValueMap.put(groupIndex, groupNameValue)
        return this
    }

    RegExEntryProcessor register(int groupIndex, EntryProcessor entryProcessor) {
        Preconditions.checkArgument(groupIndex > 0, 'Group index should be a positive number')
        Preconditions.checkNotNull(entryProcessor, 'Entry Processor instance should be provided')
        groupProcessorMap.put(groupIndex, entryProcessor)
        return this
    }

    RegExEntryProcessor registerAll(Map<Object, String> groupNameValueMap) {
        Preconditions.checkNotNull(groupNameValueMap, 'Group value map should not be null')
        this.groupNameValueMap.putAll(groupNameValueMap)
        return this
    }

    @Override
    LogEntry process(LogEntry entry) {
        final matcher = pattern.matcher(entry.line)
        while (matcher.find()) {
            for (final key : (groupProcessorMap.keySet() + groupNameValueMap.keySet())) {
                String groupValue = null
                try {
                    if (key instanceof String) {
                        groupValue = matcher.group(key as String)
                    } else if (key instanceof Integer) {
                        groupValue = matcher.group(key as Integer)
                    }
                } catch (Exception ignore) { }
                if (groupValue) {
                    if (groupNameValueMap.containsKey(key)) {
                        entry.putEntryValue(groupNameValueMap.get(key), groupValue)
                    }
                    LogEntry groupEntry = new LogEntry(groupValue)
                    final List<EntryProcessor> entryProcessors = groupProcessorMap.get(key)
                    for (EntryProcessor processor : entryProcessors) {
                        groupEntry = processor.process(groupEntry)
                        if (groupEntry) {
                            // todo Serge: investigate if there is a need in merge method for logEntry
                            entry.entryValueMap.putAll(groupEntry.entryValueMap)
                        }
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
        return result
    }

    static RegExEntryProcessor of(Pattern pattern) {
        return new RegExEntryProcessor(pattern)
    }

    static RegExEntryProcessor of(String regEx, int flags = 0) {
        return new RegExEntryProcessor(Pattern.compile(regEx, flags))
    }

}
