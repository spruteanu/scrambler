package org.prismus.scrambler.log

import groovy.transform.CompileStatic

/**
 * @author Serge Pruteanu
 */
@CompileStatic
class LogEntry {
    private final String line

    Object source
    Object row
    String id

    private Map entryValueMap

    LogEntry(String line) {
        this(null, line, 0)
    }

    LogEntry(String line, int row) {
        this(null, line, row)
    }

    LogEntry(String source, String line, int row) {
        this.source = source
        this.line = line
        this.row = row
        entryValueMap = [:]
    }

    String getLine() {
        return line
    }

    String getId() {
        return id
    }

    LogEntry putEntryValue(Object entryKey, Object value) {
        entryValueMap.put(entryKey, value)
        return this
    }

    Object getEntryValue(Object entryKey) {
        return entryValueMap.get(entryKey)
    }

    boolean isEmpty() {
        return id == null || entryValueMap.isEmpty()
    }

    LogEntry merge(LogEntry entry) {
        if (entry) {
            entryValueMap.putAll(entry.entryValueMap)
            if (entry.id) {
                id = entry.id
            }
        }
        return this
    }

    Map asEntryValueMap() {
        return Collections.unmodifiableMap(entryValueMap)
    }

}
