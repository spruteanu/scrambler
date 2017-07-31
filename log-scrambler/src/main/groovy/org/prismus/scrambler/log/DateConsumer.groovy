package org.prismus.scrambler.log

import java.text.SimpleDateFormat

/**
 * @author Serge Pruteanu
 */
class DateConsumer implements LogConsumer {
    static final String DATE = 'DATE'

    String group = DATE
    SimpleDateFormat dateFormat

    DateConsumer() {
    }

    DateConsumer(SimpleDateFormat dateFormat, String group = DATE) {
        this.dateFormat = dateFormat
        this.group = group
    }

    @Override
    void consume(LogEntry entry) {
        if (entry.logValueMap.containsKey(group)) {
            final dateString = entry.get(group)
            final date = dateFormat.parse(dateString.toString())
            entry.put(group, date)
        }
    }

    static DateConsumer of(SimpleDateFormat dateFormat, String group = DATE) {
        return new DateConsumer(dateFormat, group)
    }

    static DateConsumer of(String dateFormat, String group = DATE) {
        return new DateConsumer(new SimpleDateFormat(dateFormat), group)
    }

}
