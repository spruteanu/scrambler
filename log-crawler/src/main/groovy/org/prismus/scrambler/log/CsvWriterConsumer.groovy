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
import org.apache.commons.lang3.StringUtils

/**
 * @author Serge Pruteanu
 */
@CompileStatic
class CsvWriterConsumer implements LogConsumer, Closeable {
    Writer writer
    List<String> columns

    int flushAt
    private int nOutput

    boolean allValues
    String separator = ','
    String fieldSeparator = '"'

    CsvWriterConsumer() {
    }

    CsvWriterConsumer(Writer writer, List<String> columns) {
        this.writer = writer
        this.columns = columns
    }

    CsvWriterConsumer withSeparators(String separator, String fieldSeparator = '') {
        this.separator = separator
        setFieldSeparator(fieldSeparator)
        return this
    }

    CsvWriterConsumer flushAt(int flushAt = 100) {
        this.flushAt = flushAt
        return this
    }

    protected void writeLine(String line) {
        writer.write(line)
        writer.write(LineReader.LINE_BREAK)
    }

    protected String buildLine(List<String> values) {
        List<String> result = values
        if (fieldSeparator) {
            result = new ArrayList<String>(values.size())
            for (String value : values) {
                if (value.contains(separator)) {
                    value = fieldSeparator + value + fieldSeparator
                } else if (value.contains(fieldSeparator)) {
                    value = StringUtils.replaceChars(value, fieldSeparator, fieldSeparator + fieldSeparator)
                }
                result.add(value)
            }
        }
        return result.join(separator)
    }

    protected synchronized void doWrite(List<String> values) {
        if (!nOutput) {
            writeLine(buildLine(columns))
        }
        writeLine(buildLine(values))
        nOutput++
        if (flushAt && (nOutput % flushAt) == 0) {
            writer.flush()
        }
    }

    @Override
    void consume(LogEntry entry) {
        final values = new ArrayList(columns.size())
        final logValueMap = entry.logValueMap
        for (String column : columns) {
            values.add(Objects.toString(logValueMap.get(column)?.toString(), ''))
        }
        if (allValues) {
            final notIncludedColumns = new LinkedHashSet(logValueMap.keySet())
            notIncludedColumns.removeAll(columns)
            if (notIncludedColumns) {
                values.addAll(logValueMap.subMap(notIncludedColumns).values())
            }
        }
        doWrite(values)
    }

    @Override
    void close() throws IOException {
        Utils.closeQuietly(writer)
    }

    static CsvWriterConsumer of(Writer writer, String... columns) {
        return new CsvWriterConsumer(writer: writer, columns: Arrays.asList(columns))
    }

    static CsvWriterConsumer of(File file, String... columns) {
        return new CsvWriterConsumer(writer: new BufferedWriter(new FileWriter(file)), columns: Arrays.asList(columns))
    }

    static CsvWriterConsumer of(String filePath, String... columns) {
        return of(new File(filePath), columns)
    }

    static class Builder extends ConsumerBuilder<CsvWriterConsumer> {
        Builder() {
        }

        Builder(LogCrawler.Builder contextBuilder, Object consumer, Object... args) {
            super(contextBuilder, consumer, args)
        }

        Builder writer(Writer writer) {
            getConsumer().writer = writer
            return this
        }

        Builder columns(String... columns) {
            getConsumer().columns = columns.toList()
            return this
        }

        Builder columns(List<String> columns) {
            getConsumer().columns = columns
            return this
        }

        Builder separators(String separator, String fieldSeparator = '') {
            getConsumer().withSeparators(separator, fieldSeparator)
            return this
        }

        Builder fieldSeparator(String fieldSeparator) {
            getConsumer().fieldSeparator = fieldSeparator
            return this
        }

        Builder flushAt(int flushAt = 100) {
            getConsumer().flushAt(flushAt)
            return this
        }

    }
}
