package org.prismus.scrambler.data

import org.prismus.scrambler.NumberScrambler
import spock.lang.Specification

/**
 * @author Serge Pruteanu
 */
class CombinationsTest extends Specification {

    void 'verify array combinations'() {
        final combinations = Combinations.of(array)

        expect:
        combinations.next().length == array.length
        combinations.next().length == array.length
        combinations.next().length == array.length
        combinations.next().length == array.length
        combinations.next().length == array.length

        where:
        array << [
                [1, 2, 3, 4] as Integer[], [1L, 2L, 3L, 4L] as Long[],
                [true, false] as boolean[],
                [1.byteValue(), 2.byteValue(), 3.byteValue()] as byte[],
                [5.shortValue(), 6.shortValue(), 7.shortValue(), 8.shortValue()] as short[],
                [5, 6, 7, 8] as int[], [5L, 6L, 7L, 8L] as long[],
                [1.0f, 2.0f, 3.0f] as float[], [1.0d, 2.0d, 3.0d] as double[],
        ]
    }

    void 'verify illegal array length combination generation'() {
        when:
        Combinations.of(array)

        then:
        thrown(IllegalArgumentException)

        where:
        array << [[5] as int[], [] as int[]]
    }

    void 'verify values and list combinations'() {
        def combinations = Combinations.dataOf(Integer, NumberScrambler.random(1, 100), NumberScrambler.random(200, 400), NumberScrambler.random(-100, -10))

        expect: 'check array array generation'
        combinations.next().length > 0

        and: 'check list generation'
        Combinations.of([1, 2, 3, 4, 5]).next().size() > 0

        and: 'check list array generation'
        Combinations.dataOf(
                [NumberScrambler.random(1, 100), NumberScrambler.random(200, 400), NumberScrambler.random(-100, -10)]
        ).next().size() > 0
    }

}
