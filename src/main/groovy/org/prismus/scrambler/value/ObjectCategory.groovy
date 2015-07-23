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

    public static <T> Value<T> constant(T value) {
        return new Constant<T>(value)
    }

    public static <T> Value<T> randomOf(T[] array) {
        return new ArrayRandomElement<T>(array)
    }

}
