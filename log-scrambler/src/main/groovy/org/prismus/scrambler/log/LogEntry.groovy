package org.prismus.scrambler.log

import groovy.transform.CompileStatic

import java.util.concurrent.ConcurrentHashMap

/**
 * @author Serge Pruteanu
 */
@CompileStatic
class LogEntry implements Cloneable {
    public static final String SOURCE_INFO = 'Source'
    String line

    Object source
    int row

    Map logValueMap = new ConcurrentHashMap()

    LogEntry() {
    }

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
    }

    LogEntry putLogValue(Object entryKey, Object value) {
        logValueMap.put(entryKey, value)
        return this
    }

    Object getLogValue(Object entryKey) {
        return logValueMap.get(entryKey)
    }

    LogEntry removeLogValue(Object entryKey) {
        logValueMap.remove(entryKey)
        return this
    }

    LogEntry sourceInfo(String value) {
        putLogValue(SOURCE_INFO, value)
        return this
    }

    String getSourceInfo() {
        return getLogValue(SOURCE_INFO)
    }

    boolean isEmpty() {
        return logValueMap.isEmpty()
    }

    @Override
    Object clone() {
        return super.clone()
    }
}
