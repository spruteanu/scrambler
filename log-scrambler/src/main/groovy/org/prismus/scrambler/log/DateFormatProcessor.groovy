package org.prismus.scrambler.log

import java.text.SimpleDateFormat

/**
 * @author Serge Pruteanu
 */
class DateFormatProcessor implements LogProcessor {
    private static final String TIMESTAMP = 'Timestamp'

    String group = TIMESTAMP
    SimpleDateFormat dateFormat

    DateFormatProcessor() {
    }

    DateFormatProcessor(SimpleDateFormat dateFormat, String group = TIMESTAMP) {
        this.dateFormat = dateFormat
        this.group = group
    }

    @Override
    LogEntry process(LogEntry entry) {
        if (entry.logValueMap.containsKey(group)) {
            final dateString = entry.getLogValue(group)
            final date = dateFormat.parse(dateString.toString())
            entry.putLogValue(group, date)
        }
        return entry
    }

    static DateFormatProcessor of(SimpleDateFormat dateFormat, String group = TIMESTAMP) {
        return new DateFormatProcessor(dateFormat, group)
    }

    static DateFormatProcessor of(String dateFormat, String group = TIMESTAMP) {
        return new DateFormatProcessor(new SimpleDateFormat(dateFormat), group)
    }

}
