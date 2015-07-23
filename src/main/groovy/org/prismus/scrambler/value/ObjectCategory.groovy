package org.prismus.scrambler.value

import groovy.transform.CompileStatic
import org.prismus.scrambler.Value

/**
 * todo: add description
 *
 * @author Serge Pruteanu
 */
class ObjectCategory {

    @CompileStatic
    public static <T> Value<T> constant(T value) {
        return new Constant<T>(value)
    }

    @CompileStatic
    public static <T> Value<T> randomOf(T[] array) {
        return new ArrayRandomElement<T>(array)
    }

}
