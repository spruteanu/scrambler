package org.prismus.scrambler.log

/**
 * @author Serge Pruteanu
 */
interface ObjectProvider {
    Object get(String objectId, Object... args)
}
