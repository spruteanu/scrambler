package org.prismus.scrambler.log

/**
 * @author Serge Pruteanu
 */
interface EntryProcessor {
    LogEntry process(LogEntry entry)
}
