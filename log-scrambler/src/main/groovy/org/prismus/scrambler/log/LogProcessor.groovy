package org.prismus.scrambler.log

/**
 * @author Serge Pruteanu
 */
interface LogProcessor {
    LogEntry process(LogEntry entry)
}
