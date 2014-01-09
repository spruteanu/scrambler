package org.prismus.scrambler.builder

import org.prismus.scrambler.Value

/**
 * todo: add description
 *
 * @author Serge Pruteanu
 */
class ValueDefinitionEntry<V> {
    ValuePredicate<V> predicate
    Value<V> value
}
