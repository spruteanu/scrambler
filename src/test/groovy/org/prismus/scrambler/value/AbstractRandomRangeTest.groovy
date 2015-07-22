package org.prismus.scrambler.value

import spock.lang.Specification

import java.sql.Timestamp

/**
 * @author Serge Pruteanu
 */
class AbstractRandomRangeTest extends Specification {

    void 'test number ranges generation'() {
        given:
        final valueInstance = NumberValue.random(minimum, maximum)

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
        final minimum = new Date()
        Date maximum = new Date(new Timestamp(System.currentTimeMillis() + 24 * 60 * 1000).time)

        final calendar = Calendar.getInstance()
        calendar.setTime(maximum)
        calendar.add(Calendar.DATE, 10)
        calendar.add(Calendar.HOUR, 10)
        calendar.add(Calendar.MINUTE, 10)
        calendar.add(Calendar.SECOND, 10)
        maximum = calendar.getTime()

        final valueInstance = DateValue.random(minimum, maximum)

        expect:
        maximum > minimum
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
    }

}
