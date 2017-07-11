package org.prismus.scrambler.log

import com.google.common.collect.ArrayListMultimap
import groovy.transform.CompileStatic

import java.util.regex.Pattern

/**
 * @author Serge Pruteanu
 */
@CompileStatic
class RegExEntryProcessor implements EntryProcessor {
    Pattern pattern
    private final ArrayListMultimap<Object, EntryProcessor> groupProcessorMap = ArrayListMultimap.create()

    RegExEntryProcessor() {
    }

    RegExEntryProcessor(Pattern pattern) {
        this.pattern = pattern
    }

    RegExEntryProcessor register(String name, EntryProcessor entryProcessor) {
        groupProcessorMap.put(name, entryProcessor)
        return this
    }

    RegExEntryProcessor register(int groupIndex, EntryProcessor entryProcessor) {
        groupProcessorMap.put(groupIndex, entryProcessor)
        return this
    }

    @Override
    LogEntry process(LogEntry entry) {
        final matcher = pattern.matcher(entry.line)
        for (Map.Entry<Object, Collection<EntryProcessor>> ge : groupProcessorMap.asMap().entrySet()) {
            final key = ge.key
            String groupValue = null
            try {
                if (key instanceof String) {
                    groupValue = matcher.group(key)
                } else if (key instanceof Integer) {
                    groupValue = matcher.group(key)
                }
            } catch (Exception ignore) { }
            if (groupValue) {
                LogEntry groupEntry = new LogEntry(groupValue)
                for (EntryProcessor processor : ge.value) {
                    groupEntry = processor.process(groupEntry)
                    if (groupEntry) {
                        entry.entryValueMap.putAll(groupEntry.entryValueMap)
                    }
                }
            }
        }
        return entry
    }

}
