package org.prismus.scrambler.value

import org.prismus.scrambler.builder.ValueDefinition
import spock.lang.Specification

/**
 * @author Serge Pruteanu
 */
class ValueArrayTest extends Specification {

    void 'test array creation'() {
        ValueDefinition.register()
        given:
        def value = new ValueArray([] as int[], 10.random()).asPrimitive()
        def array = value.next()
        expect:
        array.length > 0
        int[] == array.class
        int[] == value.asType(Integer[]).next().class
        Integer[] == Integer[].array(1.incremental(100)).next().class
    }

}
