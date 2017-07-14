package org.prismus.scrambler.log

/**
 * @author Serge Pruteanu
 */
interface EntryProcessorProvider {
    EntryProcessor get(String processorId, Object... args)
}
