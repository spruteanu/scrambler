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

    LogEntry putAll(Map<String, ?> values) {
        logValueMap.putAll(values)
        return this
    }

    boolean match(String entryKey, Pattern pattern, @DelegatesTo(LogEntry) Closure closure = null) {
        final line = get(entryKey)
        if (!line) {
            return false
        }
        final map = RegexConsumer.toMap(pattern, line.toString())
        putAll map
        final matched = map.size() > 0
        if (matched && closure) {
            with closure
        }
        return matched
    }

    String get(String entryKey) {
        return logValueMap.get(entryKey)
    }

    Object _get(String entryKey) {
        return logValueMap.get(entryKey)
    }

    LogEntry remove(String... keys) {
        if (keys) {
            for (String key : keys) {
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

    Integer putInteger(String entryKey, String targetEntry = null) {
        final target = targetEntry ?: entryKey
        final value = logValueMap.get(entryKey)
        Integer var = null
        if (value) {
            var = value as Integer
            logValueMap.put(target, var)
        }
        return var
    }

    Long putLong(String entryKey, String targetEntry = null) {
        final target = targetEntry ?: entryKey
        final value = logValueMap.get(entryKey)
        Long var = null
        if (value) {
            var = value as Long
            logValueMap.put(target, var)
        }
        return var
    }

    Short putShort(String entryKey, String targetEntry = null) {
        final target = targetEntry ?: entryKey
        final value = logValueMap.get(entryKey)
        Short var = null
        if (value) {
            var = value as Short
            logValueMap.put(target, var)
        }
        return var
    }

    Byte putByte(String entryKey, String targetEntry = null) {
        final target = targetEntry ?: entryKey
        final value = logValueMap.get(entryKey)
        Byte var = null
        if (value) {
            var = value as Byte
            logValueMap.put(target, var)
        }
        return var
    }

    Float putFloat(String entryKey, String targetEntry = null) {
        final target = targetEntry ?: entryKey
        final value = logValueMap.get(entryKey)
        Float var = null
        if (value) {
            var = value as Float
            logValueMap.put(target, var)
        }
        return var
    }

    Double putDouble(String entryKey, String targetEntry = null) {
        final target = targetEntry ?: entryKey
        final value = logValueMap.get(entryKey)
        Double var = null
        if (value) {
            var = value as Double
            logValueMap.put(target, var)
        }
        return var
    }

    BigDecimal putBigDecimal(String entryKey, String targetEntry = null) {
        final target = targetEntry ?: entryKey
        final value = logValueMap.get(entryKey)
        BigDecimal var = null
        if (value) {
            var = value as BigDecimal
            logValueMap.put(target, var)
        }
        return var
    }

    Date putDate(String entryKey, SimpleDateFormat dateFormat, String targetEntry = null) {
        final target = targetEntry ?: entryKey
        final value = logValueMap.get(entryKey)
        Date var = null
        if (value) {
            var = dateFormat.parse(value.toString())
            logValueMap.put(target, var)
        }
        return var
    }

    Date putDate(String entryKey, String dateFormat, String targetEntry = null) {
        final target = targetEntry ?: entryKey
        final value = logValueMap.get(entryKey)
        Date var = null
        if (value) {
            if (!dateFormatMap.containsKey(entryKey)) {
                dateFormatMap.put(entryKey, new SimpleDateFormat(dateFormat))
            }
            var = dateFormatMap.get(entryKey).parse(value.toString())
            logValueMap.put(target, var)
        }
        return var
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
