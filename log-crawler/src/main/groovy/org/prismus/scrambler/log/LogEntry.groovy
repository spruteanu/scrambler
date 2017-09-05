/*
 * Log crawler, tool that allows to extract/crawl log files for further analysis
 *
 * Copyright (c) 2015, Sergiu Prutean. All rights reserved.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3.0 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library.
 */

package org.prismus.scrambler.log

import groovy.transform.CompileStatic

import java.text.SimpleDateFormat
import java.util.concurrent.ConcurrentHashMap
import java.util.regex.Pattern

/**
 * @author Serge Pruteanu
 */
@CompileStatic
class LogEntry extends Expando implements Cloneable {
    public static final String SOURCE_INFO = 'Source'
    private static final Map<Object, SimpleDateFormat> dateFormatMap = new ConcurrentHashMap<>()

    String line

    Object source
    int row

    Map<String, Object> logValueMap = new ConcurrentHashMap<String, Object>()

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

    LogEntry put(String entryKey, Object value) {
        logValueMap.put(entryKey, value)
        return this
    }

    LogEntry put(MapEntry... entries) {
        if (entries) {
            for (MapEntry entry : entries) {
                logValueMap.put(entry.key.toString(), entry.value)
            }
        }
        return this
    }

    LogEntry putAll(Map values) {
        logValueMap.putAll(values)
        return this
    }

    Map match(Pattern pattern, String line) {
        return RegexConsumer.toMap(pattern, line)
    }

    Object get(Object entryKey) {
        return logValueMap.get(entryKey)
    }

    LogEntry remove(Object... keys) {
        if (keys) {
            for (Object key : keys) {
                logValueMap.remove(key)
            }
        }
        return this
    }

    LogEntry sourceInfo(String value) {
        put(SOURCE_INFO, value)
        return this
    }

    String getSourceInfo() {
        return get(SOURCE_INFO)
    }

    boolean isEmpty() {
        return logValueMap.isEmpty()
    }

    LogEntry toInteger(String entryKey, String targetEntry = null) {
        final target = targetEntry ?: entryKey
        final value = logValueMap.get(entryKey)
        if (value) {
            logValueMap.put(target, value as Integer)
        }
        return this
    }

    LogEntry toLong(String entryKey, String targetEntry = null) {
        final target = targetEntry ?: entryKey
        final value = logValueMap.get(entryKey)
        if (value) {
            logValueMap.put(target, value as Long)
        }
        return this
    }

    LogEntry toShort(String entryKey, String targetEntry = null) {
        final target = targetEntry ?: entryKey
        final value = logValueMap.get(entryKey)
        if (value) {
            logValueMap.put(target, value as Short)
        }
        return this
    }

    LogEntry toByte(String entryKey, String targetEntry = null) {
        final target = targetEntry ?: entryKey
        final value = logValueMap.get(entryKey)
        if (value) {
            logValueMap.put(target, value as Byte)
        }
        return this
    }

    LogEntry toFloat(String entryKey, String targetEntry = null) {
        final target = targetEntry ?: entryKey
        final value = logValueMap.get(entryKey)
        if (value) {
            logValueMap.put(target, value as Float)
        }
        return this
    }

    LogEntry toDouble(String entryKey, String targetEntry = null) {
        final target = targetEntry ?: entryKey
        final value = logValueMap.get(entryKey)
        if (value) {
            logValueMap.put(target, value as Double)
        }
        return this
    }

    LogEntry toBigDecimal(String entryKey, String targetEntry = null) {
        final target = targetEntry ?: entryKey
        final value = logValueMap.get(entryKey)
        if (value) {
            logValueMap.put(target, value as BigDecimal)
        }
        return this
    }

    LogEntry toDate(String entryKey, SimpleDateFormat dateFormat, String targetEntry = null) {
        final target = targetEntry ?: entryKey
        final value = logValueMap.get(entryKey)
        if (value) {
            logValueMap.put(target, dateFormat.parse(value.toString()))
        }
        return this
    }

    LogEntry toDate(String entryKey, String dateFormat, String targetEntry = null) {
        final target = targetEntry ?: entryKey
        final value = logValueMap.get(entryKey)
        if (value) {
            if (!dateFormatMap.containsKey(entryKey)) {
                dateFormatMap.put(entryKey, new SimpleDateFormat(dateFormat))
            }
            logValueMap.put(target, dateFormatMap.get(entryKey).parse(value.toString()))
        }
        return this
    }

    def propertyMissing(String entryKey) {
        return logValueMap.get(entryKey)
    }

    void setProperty(String entryKey, Object value) {
        logValueMap.put(entryKey, value)
    }

    Object getAt(String property) {
        return logValueMap.get(property)
    }

    void putAt(String key, Object value) {
        logValueMap.put(key, value)
    }

}
