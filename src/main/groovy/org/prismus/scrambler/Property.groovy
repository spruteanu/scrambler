package org.prismus.scrambler

import groovy.transform.CompileStatic

/**
 * todo: add description
 *
 * @author Serge Pruteanu
 */
@CompileStatic
interface Property<T> extends Serializable, Cloneable {
    String getName()

    T value()
}
