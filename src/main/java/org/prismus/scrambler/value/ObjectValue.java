package org.prismus.scrambler.value;

import org.prismus.scrambler.Value;

/**
 * todo: add description
 *
 * @author Serge Pruteanu
 */
public class ObjectValue {
    public static <T> Value<T> randomOf(T[] array) {
        return new ArrayRandomElement<T>(array);
    }
}
