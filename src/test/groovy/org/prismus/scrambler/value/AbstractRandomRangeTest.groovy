package org.prismus.scrambler.value

import spock.lang.Specification

import java.sql.Timestamp

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

        and:
        minimum == valueInstance.min(minimum, maximum)
        null == valueInstance.min(null, maximum)
        null == valueInstance.min(minimum, null)

        minimum == valueInstance.max(minimum, null)
        maximum == valueInstance.max(null, maximum)

        where:
        minimum << [
                10, 10L, new Date(),
                10.shortValue(), 10.byteValue(),
                10.0.floatValue(), 10.0.doubleValue(),
                10.0.toBigDecimal(), 10.toBigInteger(),
        ]
        maximum << [
                50, 50L, new Date(new Timestamp(System.currentTimeMillis() + 24 * 60 * 1000).time),
                50.shortValue(), 50.byteValue(),
                50.0.floatValue(), 50.0.doubleValue(),
                50.0.toBigDecimal(), 50.toBigInteger(),
        ]
    }

    void 'test date ranges generation'() {
        final min = new Date()
        Date max = new Date()

        final calendar = Calendar.getInstance()
        calendar.setTime(max)
        calendar.add(Calendar.DATE, 10)
        calendar.add(Calendar.HOUR, 10)
        calendar.add(Calendar.MINUTE, 10)
        calendar.add(Calendar.SECOND, 10)
        max = calendar.getTime()

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
