package org.prismus.scrambler.log

import com.google.common.collect.ArrayListMultimap
import groovy.transform.CompileStatic

import java.util.regex.Matcher
import java.util.regex.Pattern

/**
 * @author Serge Pruteanu
 */
@CompileStatic
class RegExEntryProcessor implements EntryProcessor {
    private static final String DEFAULT_LOG4J_DATE_FORMAT = 'yyyy-MM-dd HH:mm:ss.SSS' // ISO8601DateFormat

    Pattern pattern
    private final ArrayListMultimap<Object, EntryProcessor> groupProcessorMap = ArrayListMultimap.create()

    RegExEntryProcessor() {
    }

    RegExEntryProcessor(String regEx, int flags = 0) {
        this(Pattern.compile(regEx, flags))
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

    static String replaceTextByGroups(Map<String, String> replacements, String text, String regEx) {
        final sb = new StringBuffer()
        final matcher = Pattern.compile(regEx).matcher(text)
        while (matcher.find()) {
            matcher.appendReplacement(sb, replacements.get(matcher.group()))
        }
        matcher.appendTail(sb)
    }

    static String dateFormatToRegEx(String dateFormat) {
        String result = dateFormat
        result = result.replaceAll('[w]+', '\\\\w+')
        result = result.replaceAll('[WDdFuHkKhmsSyYGMEazZX]+', '\\\\w+')
        return result
    }

    static String log4jConversionPatternToRegEx(String conversionPatter) {
        throw new RuntimeException('https://logging.apache.org/log4j/1.2/apidocs/org/apache/log4j/PatternLayout.html')
    }

}
