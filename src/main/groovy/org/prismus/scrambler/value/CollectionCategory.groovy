package org.prismus.scrambler.value

import groovy.transform.CompileStatic
import org.prismus.scrambler.Value

/**
 * todo: add description
 *
 * @author Serge Pruteanu
 */
@CompileStatic
class CollectionCategory {

    public static <V, T extends Collection<V>> CollectionValue<V, T> of(T collection, Value<V> value) {
        return new CollectionValue<V, T>(collection, value)
    }

    public static <T> Value<T> randomOf(List<T> values) {
        return new ListRandomElement<T>(values)
    }

    public static <T> Value<T> randomOf(Collection<T> collection) {
        return new ListRandomElement<T>(new ArrayList<T>(collection))
    }

}
