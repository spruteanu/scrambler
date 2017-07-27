package org.prismus.scrambler.log

import java.text.SimpleDateFormat

/**
 * @author Serge Pruteanu
 */
class DateConsumer implements LogConsumer {
    static final String TIMESTAMP = 'Timestamp'

    String group = TIMESTAMP
    SimpleDateFormat dateFormat

    DateConsumer() {
    }

    DateConsumer(SimpleDateFormat dateFormat, String group = TIMESTAMP) {
        this.dateFormat = dateFormat
        this.group = group
    }

    @Override
    void consume(LogEntry entry) {
        if (entry.logValueMap.containsKey(group)) {
            final dateString = entry.getLogValue(group)
            final date = dateFormat.parse(dateString.toString())
            entry.putLogValue(group, date)
        }
    }

    static DateConsumer of(SimpleDateFormat dateFormat, String group = TIMESTAMP) {
        return new DateConsumer(dateFormat, group)
    }

    static DateConsumer of(String dateFormat, String group = TIMESTAMP) {
        return new DateConsumer(new SimpleDateFormat(dateFormat), group)
    }

}
