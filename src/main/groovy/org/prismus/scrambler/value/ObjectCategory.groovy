package org.prismus.scrambler.value

import groovy.transform.CompileStatic
import org.prismus.scrambler.Value

/**
 * todo: add description
 *
 * @author Serge Pruteanu
 */
@CompileStatic
class ObjectCategory {

    static <T> Value<T> constant(T self) {
        return new Constant<T>(self)
    }

    static <T> Value<T> randomOf(T[] self) {
        return new ArrayRandomElement<T>(self)
    }

}
