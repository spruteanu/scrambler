package org.prismus.scrambler.log

import groovy.transform.CompileStatic

/**
 * @author Serge Pruteanu
 */
@CompileStatic
abstract class LogReader implements Closeable {
    static final String LINE_BREAK = System.getProperty('line.separator')

    protected LogContext context
    protected Object source
    protected LogEntry currentEntry
    protected int currentRow

    boolean multiline = true

    LogReader() {
    }

    LogReader(LogContext context) {
        this.context = context
    }

    LogReader withContext(LogContext context) {
        this.context = context
        return this
    }

    protected abstract String readLine()
    protected abstract void doClose()

    LogEntry read() {
        String line = readLine()
        if (line) {
            final logEntry = new LogEntry(source, line, ++currentRow)
            final lineEntry = context.handle(logEntry)
            if (!lineEntry || lineEntry.isEmpty()) {
                if (multiline && currentEntry) {
                    currentEntry.line += line + LINE_BREAK
                    currentEntry = context.handle(currentEntry)
                }
            } else {
                currentEntry = lineEntry
            }
        } else {
            close()
        }
        return currentEntry
    }

    @Override
    void close() throws IOException {
        doClose()
        currentEntry = null
        currentRow = 0
        source = null
    }

    LogReader oneLineEntry() {
        multiline = false
        return this
    }

}
