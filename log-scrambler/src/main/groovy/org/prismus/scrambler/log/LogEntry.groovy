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

    Map entryValueMap

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
        entryValueMap = [:]
    }

    LogEntry putEntryValue(Object entryKey, Object value) {
        entryValueMap.put(entryKey, value)
        return this
    }

    Object getEntryValue(Object entryKey) {
        return entryValueMap.get(entryKey)
    }

    void clearEntryValueMap() {
        entryValueMap.clear()
    }

    boolean isEmpty() {
        return entryValueMap.isEmpty()
    }

}
