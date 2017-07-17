package org.prismus.scrambler.log

import groovy.transform.CompileStatic

/**
 * @author Serge Pruteanu
 */
class LogReaderConsumer implements LogConsumer {

    LogContext context
    boolean multiline = true

    LogReaderConsumer() {
    }

    LogReaderConsumer withContext(LogContext context) {
        this.context = context
        return this
    }

    LogReaderConsumer oneLineEntry() {
        multiline = false
        return this
    }

    @Override
    @CompileStatic
    void process(LogEntry entry) {
        final lineReader = toLineReader(entry)
        final source = toSource(entry)
        try {
            LogEntry lastEntry = null
            int currentRow = 0
            String line
            while ((line = lineReader.readLine()) != null) {
                final logEntry = new LogEntry(source, line, ++currentRow)
                context.process(logEntry)
                if (!logEntry || logEntry.isEmpty()) {
                    if (multiline && lastEntry) {
                        lastEntry.line += line + Utils.LINE_BREAK
                        context.process(lastEntry)
                    }
                } else {
                    lastEntry = logEntry
                }
            }
        } finally {
            Utils.closeQuietly(lineReader)
        }
    }

    static LineReader toLineReader(LogEntry entry) {
        return toLineReader(entry.source)
    }

    @CompileStatic
    static LineReader toLineReader(RandomAccessFile rf) {
        return new RandomAccessFileLineReader(rf)
    }

    @CompileStatic
    static LineReader toLineReader(InputStream inputStream) {
        return new IoLineReader(new BufferedReader(new InputStreamReader(inputStream)))
    }

    @CompileStatic
    static LineReader toLineReader(Reader reader) {
        return new IoLineReader(reader instanceof BufferedReader ? reader : new BufferedReader(reader))
    }

    @CompileStatic
    static LineReader toLineReader(File file) {
        return new IoLineReader(new BufferedReader(new FileReader(file)))
    }

    @CompileStatic
    static LineReader toLineReader(String content) {
        return new IoLineReader(new StringReader(content))
    }

    @CompileStatic
    static private Object toSource(LogEntry entry) {
        return entry.getLogValue('Source')
    }

    @CompileStatic
    private static class IoLineReader implements LineReader {
        final Reader reader

        IoLineReader(Reader reader) {
            this.reader = reader
        }

        @Override
        String readLine() {
            return reader.readLine()
        }

        @Override
        void close() throws IOException {
            reader.close()
        }
    }

    @CompileStatic
    private static class RandomAccessFileLineReader implements LineReader {
        final RandomAccessFile raf

        RandomAccessFileLineReader(RandomAccessFile raf) {
            this.raf = raf
        }

        @Override
        String readLine() {
            return raf.readLine()
        }

        @Override
        void close() throws IOException {
            raf.close()
        }
    }

}
