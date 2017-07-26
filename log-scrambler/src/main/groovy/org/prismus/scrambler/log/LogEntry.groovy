package org.prismus.scrambler.log

import groovy.transform.CompileStatic

import java.util.concurrent.ConcurrentHashMap

/**
 * @author Serge Pruteanu
 */
@CompileStatic
class LogEntry implements Cloneable {
    String line

    Object source
    Object row

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

    void clearLogValueMap() {
        logValueMap.clear()
    }

    boolean isEmpty() {
        return logValueMap.isEmpty()
    }

    @Override
    Object clone() {
        return super.clone()
    }

}
