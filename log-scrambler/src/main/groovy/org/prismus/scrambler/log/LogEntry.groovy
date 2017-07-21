package org.prismus.scrambler.log

import groovy.transform.CompileStatic

/**
 * @author Serge Pruteanu
 */
@CompileStatic
class LogEntry implements Cloneable {
    String line

    Object source
    Object row
    String cacheKey

    Map logValueMap

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
        logValueMap = [:]
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

    LogEntry cacheable(Object cacheKey) {
        this.cacheKey = cacheKey
        return this
    }

    boolean isCacheable() {
        return cacheKey != null
    }

    @Override
    Object clone() {
        return super.clone()
    }

}
