package org.prismus.scrambler.value

import spock.lang.Specification

/**
 * @author Serge Pruteanu
 */
class ArrayDataTest extends Specification {

    void 'test array creation'() {
        GroovyDataDefinition.register()
        given:
        def value = int.arrayOf(10.random(), null)
        def array = value.next()

        expect:
        array.length > 0
        int[] == array.class
        Integer[] == Integer[].arrayOf(1.increment(100)).next().class
    }

}
