package org.prismus.scrambler.value

import org.prismus.scrambler.builder.GroovyValueDefinition
import spock.lang.Specification

/**
 * @author Serge Pruteanu
 */
class ValueArrayTest extends Specification {

    void 'test array creation'() {
        GroovyValueDefinition.register()
        given:
        def value = ValueArray.of(10.random(), int, null)
        def array = value.next()
        expect:
        array.length > 0
        int[] == array.class
        Integer[] == Integer[].array(1.incremental(100)).next().class
    }

}
