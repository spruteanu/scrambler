package org.prismus.scrambler.value

import org.junit.Assert
import org.prismus.scrambler.Value
import spock.lang.Specification

/**
 * todo: add description
 *
 * @author Serge Pruteanu
 */
class IncrementalTest extends Specification {

    void 'verify incremental number generation'(Number start, Number step, Integer count) {
        given:
        Value<Number> numberValue = Incremental.of(start, step)

        expect:
        numberValue.get() == start
        for (int i = 0; i < 5; i++) {
            Assert.assertEquals(numberValue.get() + step, numberValue.next())
        }

        and: "verify array creation"
        Value<Number[]> numberValues = Incremental.arrayOf(start, step, count)
        for (int i = 0; i < 5; i++) {
            Number[] values = numberValues.next()
            Assert.assertNotNull(values)
            Assert.assertTrue(values.length > 0)
            if (count != null) {
                Assert.assertEquals(count, values.length)
            }
            Assert.assertEquals(start, values[0])
            for (int j = 1; j < values.length; j++) {
                Assert.assertEquals(start + step, values[j])
                start = values[j]
            }
            start += step;
        }

        where:
        start << [1, 12L, new Integer(1).shortValue(), new Integer(1).byteValue(), new Integer(6).toBigInteger(), new Integer(7).toBigDecimal(),]
        step << [10, 10L, new Integer(3).shortValue(), new Integer(1).byteValue(), new Integer(6).toBigInteger(), new Integer(6).toBigDecimal(),]
        count << [5, 3, null, 10, 20, null]
    }

    void 'verify incremental float values'(Float start, Float step, Integer count) {
        given:
        Value<Float> numberValue = Incremental.of(start, step)

        expect:
        numberValue.get() == start
        for (int i = 0; i < 5; i++) {
            Assert.assertEquals((float) numberValue.get() + step, numberValue.next(), 0.0f)
        }

        and: "verify array creation"
        Value<Float[]> numberValues = Incremental.arrayOf(start, step, count)
        for (int i = 0; i < 5; i++) {
            Float[] values = numberValues.next()
            Assert.assertNotNull(values)
            Assert.assertTrue(values.length > 0)
            if (count != null) {
                Assert.assertEquals(count, values.length)
            }
            Assert.assertEquals(start, values[0], 0.00001f)
            for (int j = 1; j < values.length; j++) {
                Assert.assertEquals(start + step, values[j], 0.00001f)
                start = values[j]
            }
            start += step;
        }

        where:
        start << [1.0f, 5.0f]
        step << [0.2f, 0.3f]
        count << [5, null,]
    }

    void 'verify incremental double values'(Double start, Double step, Integer count) {
        given:
        Value<Double> numberValue = Incremental.of(start, step)

        expect:
        numberValue.get() == start
        for (int i = 0; i < 5; i++) {
            Assert.assertEquals(numberValue.get() + step, numberValue.next(), 0.0)
        }

        and: "verify array creation"
        Value<Double[]> numberValues = Incremental.arrayOf(start, step, count)
        for (int i = 0; i < 5; i++) {
            Double[] values = numberValues.next()
            Assert.assertNotNull(values)
            Assert.assertTrue(values.length > 0)
            if (count != null) {
                Assert.assertEquals(count, values.length)
            }
            Assert.assertEquals(start, values[0], 0.0)
            for (int j = 1; j < values.length; j++) {
                Assert.assertEquals(start + step, values[j], 0.0)
                start = values[j]
            }
            start += step;
        }

        where:
        start << [1.0, 11.0,]
        step << [0.2, 3.7,]
        count << [5, null,]
    }

    void 'verify incremental primitives generation'(Class type, Number start, Number step, Integer count) {
        given:
        Value numberValues = Incremental.arrayOf(type, start, step, count)

        expect:
        for (int i = 0; i < 5; i++) {
            final values = numberValues.next()
            Assert.assertNotNull(values)
            Assert.assertTrue(values.length > 0)
            if (count != null) {
                Assert.assertEquals(count, values.length)
            }
            Assert.assertEquals(start, values[0])
            for (int j = 1; j < values.length; j++) {
                Assert.assertEquals(start + step, values[j])
                start = values[j]
            }
            start += step;
        }

        where:
        type << [int[], long[], short[], byte[],]
        start << [1, 12L, (short)1, (byte)1, ]
        step << [10, 10L, (short)3, (byte)1, ]
        count << [5, 3, null, 10, ]
    }

    void 'verify incremental dates'(Date date, Integer calendarField, Integer step) {
        expect:
        date.before(Incremental.of(date).next())
        date.before(Incremental.of(date, 100).next())
        date.before(Incremental.of(date, 4, Calendar.MINUTE).next())

        and: "Increment by several criteria: seconds/minutes/hours/weeks/month/years"
        date.before(new IncrementalDate().seconds(5).minutes(1).hours(2).days(3).years(1).next())

        and: "verify in a loop"
        final Value<Date> incrementalDate = Incremental.of(date, calendarField, step)
        for (int i = 0; i < 5; i++) {
            final nextDate = incrementalDate.next()
            Assert.assertTrue(date.before(nextDate))
        }

        where:
        date << [new Date(), new Date(), new Date(), new Date(),]
        calendarField << [Calendar.HOUR, Calendar.DATE, Calendar.MONTH, Calendar.YEAR,]
        step << [1, 1, 1, 1,]
    }

    void 'verify string incremental'(String defaultValue, String pattern, Integer index) {
        expect:
        defaultValue != Incremental.of(defaultValue).next()
        defaultValue != Incremental.of(defaultValue, pattern).next()
        defaultValue != Incremental.of(defaultValue, pattern, index).next()

        5 == Incremental.of(5, defaultValue).next().length
        5 == Incremental.of(5, defaultValue, pattern).next().length
        5 == Incremental.of(5, defaultValue, pattern, index).next().length

        and: "verify in a loop"
        final incrementalString = Incremental.of(defaultValue, pattern, index)
        for (int i = 0; i < 5; i++) {
            Assert.assertNotEquals(defaultValue, incrementalString.next())
        }

        where:
        defaultValue << ["Attempt N", "Test string ", "I would like ", "I would like to take ",]
        pattern << ["%s%d", "%s%d", "%s%d candies", "%s%d day offs",]
        index << [1, 1, 1, 1,]
    }

}
