package org.prismus.scrambler.log

import java.text.SimpleDateFormat

/**
 * @author Serge Pruteanu
 */
class DateFormatConsumer implements LogConsumer {
    static final String TIMESTAMP = 'Timestamp'

    String group = TIMESTAMP
    SimpleDateFormat dateFormat

    DateFormatConsumer() {
    }

    DateFormatConsumer(SimpleDateFormat dateFormat, String group = TIMESTAMP) {
        this.dateFormat = dateFormat
        this.group = group
    }

    @Override
    void process(LogEntry entry) {
        if (entry.logValueMap.containsKey(group)) {
            final dateString = entry.getLogValue(group)
            final date = dateFormat.parse(dateString.toString())
            entry.putLogValue(group, date)
        }
    }

    static DateFormatConsumer of(SimpleDateFormat dateFormat, String group = TIMESTAMP) {
        return new DateFormatConsumer(dateFormat, group)
    }

    static DateFormatConsumer of(String dateFormat, String group = TIMESTAMP) {
        return new DateFormatConsumer(new SimpleDateFormat(dateFormat), group)
    }

}
