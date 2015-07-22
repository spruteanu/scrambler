package org.prismus.scrambler.value

import spock.lang.Specification

/**
 * @author Serge Pruteanu
 */
class ArrayValueTest extends Specification {

    void 'test array creation'() {
        GroovyValueDefinition.register()
        given:
        def value = int.array(10.random(), null)
        def array = value.next()

        expect:
        array.length > 0
        int[] == array.class
        Integer[] == Integer[].array(1.incremental(100)).next().class
    }

}
