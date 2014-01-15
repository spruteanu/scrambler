package org.prismus.scrambler.value

import org.apache.commons.lang.time.DateUtils
import spock.lang.Specification

/**
 * todo: add description
 *
 * @author Serge Pruteanu
 */
class AbstractRandomRangeTest extends Specification {

    void 'test number ranges generation'() {
        given:
        final valueInstance = Random.of(minimum, maximum)

        expect:
        1000.times {
            final value = valueInstance.next()
            value >= minimum
            value < maximum
        }

        where:
        minimum << [
                10, 10L,
                10.shortValue(), 10.byteValue(),
                10.0.floatValue(), 10.0.doubleValue(),
                10.0.toBigDecimal(), 10.toBigInteger(),
        ]
        maximum << [
                50, 50L,
                50.shortValue(), 50.byteValue(),
                50.0.floatValue(), 50.0.doubleValue(),
                50.0.toBigDecimal(), 50.toBigInteger(),
        ]
    }

    void 'test date ranges generation'() {
        final min = new Date()
        Date max = new Date()
        max = DateUtils.addDays(max, 10)
        max = DateUtils.addHours(max, 10)
        max = DateUtils.addMinutes(max, 10)
        max = DateUtils.addSeconds(max, 10)

        final valueInstance = Random.of(min, max)

        expect:
        max > min
        1000.times {
            final value = valueInstance.next()
            value >= min
            value < max
        }
    }

}
