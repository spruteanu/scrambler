package org.prismus.scrambler.log

import groovy.transform.CompileStatic

/**
 * @author Serge Pruteanu
 */
@CompileStatic
class CsvOutputConsumer implements LogConsumer, Closeable {
    Writer writer
    List<String> columns

    int flushAt
    int nOutput

    CsvOutputConsumer() {
    }

    CsvOutputConsumer(Writer writer, List<String> columns) {
        this.writer = writer
        this.columns = columns
    }

    CsvOutputConsumer flushAt(int flushAt = 100) {
        this.flushAt = flushAt
        return this
    }

    @Override
    void process(LogEntry entry) {
        final values = new ArrayList<String>(columns.size())
        for (String column : columns) {
            values.add(Objects.toString(entry.getLogValue(column)?.toString(), ''))
        }
        writer.write(values.join(', '))
        writer.write(Utils.LINE_BREAK)

        nOutput++
        if (flushAt && (nOutput % flushAt) == 0) {
            writer.flush()
        }
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
