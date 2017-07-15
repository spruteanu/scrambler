package org.prismus.scrambler.log

import groovy.transform.CompileStatic

/**
 * @author Serge Pruteanu
 */
@CompileStatic
class LogEntry {
    String line

    Object source
    Object row
    String id

    Map logValueMap

    LogEntry(String line) {
        this(null, line, 0)
    }

    LogEntry(String line, int row) {
        this(null, line, row)
    }

    LogEntry(Object source, String line, int row) {
        this.source = source
        this.line = line
        this.row = row
        logValueMap = [:]
    }

    LogEntry putLogValue(Object entryKey, Object value) {
        logValueMap.put(entryKey, value)
        return this
    }

    Object getLogValue(Object entryKey) {
        return logValueMap.get(entryKey)
    }

    void clearEntryValueMap() {
        logValueMap.clear()
    }

    boolean isEmpty() {
        return logValueMap.isEmpty()
    }

}
