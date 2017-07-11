package org.prismus.scrambler.log

/**
 * @author Serge Pruteanu
 */
interface LineParser {
    LogEntry parse(String line)
}
