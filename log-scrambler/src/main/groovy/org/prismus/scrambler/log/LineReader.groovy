package org.prismus.scrambler.log

import groovy.transform.CompileStatic

/**
 * @author Serge Pruteanu
 */
abstract class LineReader implements Closeable {
    static final String LINE_BREAK = System.getProperty('line.separator')
    abstract String readLine()

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
    static LogEntry newLogSource(Object source, Object sourceName) {
        return addSourceName(new LogEntry(source: source), sourceName)
    }

    @CompileStatic
    private static LogEntry addSourceName(LogEntry entry, Object sourceName) {
        entry.putLogValue('SourceName', sourceName)
        return entry
    }

    @CompileStatic
    static Object toSourceName(LogEntry entry) {
        return entry.getLogValue('SourceName')
    }

    @CompileStatic
    private static class IoLineReader extends LineReader {
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
    private static class RandomAccessFileLineReader extends LineReader {
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
