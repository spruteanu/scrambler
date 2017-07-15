package org.prismus.scrambler.log

/**
 * @author Serge Pruteanu
 */
interface ProcessorProvider {
    EntryProcessor get(String processorId, Object... args)
}
