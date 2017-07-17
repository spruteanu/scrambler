package org.prismus.scrambler.log

/**
 * @author Serge Pruteanu
 */
@FunctionalInterface
interface LogConsumer {
    void process(LogEntry entry)
}
