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

    LogEntry put(Object entryKey, Object value) {
        logValueMap.put(entryKey, value)
        return this
    }

    Object get(Object entryKey) {
        return logValueMap.get(entryKey)
    }

    LogEntry remove(Object entryKey) {
        logValueMap.remove(entryKey)
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

    @Override
    Object clone() {
        return super.clone()
    }
}
