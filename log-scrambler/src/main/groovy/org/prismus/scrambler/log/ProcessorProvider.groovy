package org.prismus.scrambler.log

/**
 * @author Serge Pruteanu
 */
interface ProcessorProvider {
    LogProcessor get(String processorId, Object... args)
}
