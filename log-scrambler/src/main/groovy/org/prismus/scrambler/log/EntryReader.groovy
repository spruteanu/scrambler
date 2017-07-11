package org.prismus.scrambler.log

import groovy.transform.CompileStatic

/**
 * @author Serge Pruteanu
 */
@CompileStatic
abstract class EntryReader {
    protected Object source
    protected Queue<String> lineQueue
    protected LogContext context
    private int currentRow

    EntryReader(LogContext context) {
        this.context = context
        lineQueue = new LinkedList<String>()
    }

    protected abstract LogEntry doRead()

    LogEntry read() {
        final logEntry = doRead()
        if (logEntry) {
            currentRow++
            logEntry.row = currentRow
            logEntry.source = source
            context.handle(logEntry)
        }
        return logEntry
    }

}
