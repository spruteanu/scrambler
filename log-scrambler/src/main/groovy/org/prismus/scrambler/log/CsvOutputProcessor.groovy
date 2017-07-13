package org.prismus.scrambler.log

import groovy.transform.CompileStatic

/**
 * @author Serge Pruteanu
 */
@CompileStatic
class CsvOutputProcessor implements EntryProcessor, Closeable {
    Writer writer
    List<String> columns

    @Override
    LogEntry process(LogEntry entry) {
        final values = new ArrayList<String>(columns.size())
        for (String column : columns) {
            values.add(Objects.toString(entry.getEntryValue(column)?.toString(), ''))
        }
        writer.write(values.join(', '))
        writer.write(EntryReader.LINE_BREAK)
        return entry
    }

    @Override
    void close() throws IOException {
        Utils.closeQuietly(writer)
    }

    static CsvOutputProcessor of(Writer writer, String... columns) {
        return new CsvOutputProcessor(writer: writer, columns: Arrays.asList(columns))
    }

    static CsvOutputProcessor of(File file, String... columns) {
        return new CsvOutputProcessor(writer: new BufferedWriter(new FileWriter(file)), columns: Arrays.asList(columns))
    }

    static CsvOutputProcessor of(String filePath, String... columns) {
        return of(new File(filePath), columns)
    }

}
