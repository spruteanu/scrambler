package org.prismus.scrambler.log

import java.text.SimpleDateFormat

/**
 * @author Serge Pruteanu
 */
class DateValueProcessor implements EntryProcessor {
    private static final String TIMESTAMP = 'Timestamp'

    String entryValueName = TIMESTAMP
    SimpleDateFormat dateFormat

    DateValueProcessor() {
    }

    DateValueProcessor(SimpleDateFormat dateFormat, String entryValueName = TIMESTAMP) {
        this.dateFormat = dateFormat
        this.entryValueName = entryValueName
    }

    @Override
    LogEntry process(LogEntry entry) {
        if (entry.entryValueMap.containsKey(entryValueName)) {
            final dateString = entry.getEntryValue(entryValueName)
            final date = dateFormat.parse(dateString.toString())
            entry.putEntryValue(entryValueName, date)
        }
        return entry
    }

    static DateValueProcessor of(SimpleDateFormat dateFormat, String entryValueName = TIMESTAMP) {
        return new DateValueProcessor(dateFormat, entryValueName)
    }

    static DateValueProcessor of(String dateFormat, String entryValueName = TIMESTAMP) {
        return new DateValueProcessor(new SimpleDateFormat(dateFormat), entryValueName)
    }

}
