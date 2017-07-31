package org.prismus.scrambler.log

import groovy.transform.CompileStatic

/**
 * @author Serge Pruteanu
 */
@CompileStatic
class ArrayListCollector implements LogConsumer {
    final List<LogEntry> logEntries

    ArrayListCollector(int size = 1024) {
        this(new ArrayList<LogEntry>(size))
    }

    ArrayListCollector(List<LogEntry> logEntries) {
        this.logEntries = logEntries
    }

    @Override
    void consume(LogEntry entry) {
        logEntries.add(entry)
    }
}
