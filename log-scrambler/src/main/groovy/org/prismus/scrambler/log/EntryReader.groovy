package org.prismus.scrambler.log

import groovy.transform.CompileStatic

/**
 * @author Serge Pruteanu
 */
@CompileStatic
abstract class EntryReader implements Closeable {
    static final String LINE_BREAK = System.getProperty('line.separator')

    protected Object source
    protected Queue<String> lineQueue
    protected LogEntry currentEntry
    protected LogContext context
    protected int currentRow

    boolean multiline = true

    EntryReader(LogContext context) {
        this.context = context
        lineQueue = new LinkedList<String>()
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
        currentEntry = null
        doClose()
    }

    EntryReader oneLineEntry() {
        multiline = false
        return this
    }

}
