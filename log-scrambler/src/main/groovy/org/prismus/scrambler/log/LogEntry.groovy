package org.prismus.scrambler.log

import groovy.transform.CompileStatic

/**
 * @author Serge Pruteanu
 */
@CompileStatic
class LogEntry {
    private String message
    private String id
    private Map entryValueMap

    LogEntry(String message) {
        this.message = message
        entryValueMap = [:]
    }

    String getMessage() {
        return message
    }

    String getId() {
        return id
    }

    LogEntry registerId(String id) {
        this.id = id
        return this
    }

    LogEntry putEntryValue(Object entryKey, Object value) {
        entryValueMap.put(entryKey, value)
        return this
    }

    Object getEntryValue(Object entryKey) {
        return entryValueMap.get(entryKey)
    }

    Map asEntryValueMap() {
        return Collections.unmodifiableMap(entryValueMap)
    }

}
