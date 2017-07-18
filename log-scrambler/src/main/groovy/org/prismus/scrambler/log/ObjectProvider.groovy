package org.prismus.scrambler.log

/**
 * @author Serge Pruteanu
 */
interface ObjectProvider {
    Object get(Object objectId, Object... args)
}
