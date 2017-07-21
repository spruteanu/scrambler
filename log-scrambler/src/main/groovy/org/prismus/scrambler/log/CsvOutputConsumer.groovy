package org.prismus.scrambler.log

import groovy.transform.CompileStatic
import org.apache.commons.lang3.StringUtils

/**
 * @author Serge Pruteanu
 */
@CompileStatic
class CsvOutputConsumer implements LogConsumer, Closeable {
    Writer writer
    List<String> columns

    int flushAt
    private int nOutput

    boolean writeHeader
    boolean includeSource
    String separator = ', '
    String fieldSeparator = ''

    CsvOutputConsumer() {
    }

    CsvOutputConsumer(Writer writer, List<String> columns) {
        this.writer = writer
        this.columns = columns
    }

    protected void adjustSeparator(String fieldSeparator) {
        this.separator = fieldSeparator + separator + fieldSeparator
    }

    void setFieldSeparator(String fieldSeparator) {
        this.fieldSeparator = fieldSeparator
        adjustSeparator(fieldSeparator)
    }

    CsvOutputConsumer withSeparators(String separator, String fieldSeparator = '') {
        this.separator = separator
        setFieldSeparator(fieldSeparator)
        return this
    }

    CsvOutputConsumer writeHeader() {
        writeHeader = true
        return this
    }

    CsvOutputConsumer includeSource() {
        includeSource
        return this
    }

    CsvOutputConsumer flushAt(int flushAt = 100) {
        this.flushAt = flushAt
        return this
    }

    protected void writeLine(String line) {
        if (fieldSeparator) {
            writer.write(fieldSeparator)
            writer.write(line)
            writer.write(fieldSeparator)
        } else {
            writer.write(line)
        }
        writer.write(LineReader.LINE_BREAK)
    }

    protected String buildLine(List<String> values) {
        List<String> result = values
        if (fieldSeparator) {
            result = new ArrayList<String>(values.size())
            for (String value : values) {
                result.add(StringUtils.replaceChars(value, fieldSeparator, fieldSeparator + fieldSeparator))
            }
        }
        return result.join(separator)
    }

    protected synchronized void doWrite(ArrayList<String> values) {
        if (writeHeader && !nOutput) {
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
        final values = new ArrayList<String>(columns.size())
        for (String column : columns) {
            values.add(Objects.toString(entry.getLogValue(column)?.toString(), ''))
        }
        if (includeSource) {
            values.add("Line: $entry.line; Source: ${Objects.toString(entry.source?.toString(), 'unspecified')}".toString())
        }
        doWrite(values)
    }

    @Override
    void close() throws IOException {
        Utils.closeQuietly(writer)
    }

    static CsvOutputConsumer of(Writer writer, String... columns) {
        return new CsvOutputConsumer(writer: writer, columns: Arrays.asList(columns))
    }

    static CsvOutputConsumer of(File file, String... columns) {
        return new CsvOutputConsumer(writer: new BufferedWriter(new FileWriter(file)), columns: Arrays.asList(columns))
    }

    static CsvOutputConsumer of(String filePath, String... columns) {
        return of(new File(filePath), columns)
    }

}
