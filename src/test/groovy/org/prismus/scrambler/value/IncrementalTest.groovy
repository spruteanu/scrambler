package org.prismus.scrambler.value

import org.junit.Assert
import org.prismus.scrambler.Value
import spock.lang.Specification

/**
 * @author Serge Pruteanu
 */
class IncrementalTest extends Specification {

    void 'verify incremental number generation'(Number start, Number step, Integer count) {
        given:
        Value<Number> numberValue = NumberCategory.increment(start, step)

        expect:
        numberValue.get() == start
        for (int i = 0; i < 5; i++) {
            Assert.assertEquals(numberValue.get() + step, numberValue.next())
        }

        and: "verify array creation"
        Value<Number[]> numberValues = NumberCategory.incrementArray(start, step, count)
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
        Value<Float> numberValue = NumberCategory.increment(start, step)

        expect:
        numberValue.get() == start
        for (int i = 0; i < 5; i++) {
            Assert.assertEquals((float) numberValue.get() + step, numberValue.next(), 0.0f)
        }

        and: "verify array creation"
        Value<Float[]> numberValues = NumberCategory.incrementArray(start, step, count)
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
        Value<Double> numberValue = NumberCategory.increment(start, step)

        expect:
        numberValue.get() == start
        for (int i = 0; i < 5; i++) {
            Assert.assertEquals(numberValue.get() + step, numberValue.next(), 0.0)
        }

        and: "verify array creation"
        Value<Double[]> numberValues = NumberCategory.incrementArray(start, step, count)
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
        start << [1.0d, 11.0d,]
        step << [0.2d, 3.7d,]
        count << [5d, null,]
    }

    void 'verify incremental primitives generation'(Class type, Number start, Number step, Integer count) {
        given:
        Value numberValues = ClassCategory.incrementArray(type, start, step, count)

        expect:
        for (int i = 0; i < 5; i++) {
            final values = numberValues.next()
            Assert.assertNotNull(values)
            Assert.assertTrue(values.length > 0)
            if (count != null) {
                Assert.assertEquals(count, values.length)
            }
            checkEqual(start, values[0])
            for (int j = 1; j < values.length; j++) {
                final val = values[j]
                checkEqual(start + step, val)
                start = val
            }
            start += step;
        }

        where:
        type << [int[], long[], short[], byte[], float[], double[], ]
        start << [1, 12L, (short)1, (byte)1, 3.0f, 5.0d,]
        step << [10, 10L, (short)3, (byte)1, 0.25f, 0.15d, ]
        count << [5, 3, null, 10, 25, 15, ]
    }

    private static void checkEqual(def start, def val) {
        if (start instanceof Float) {
            Assert.assertEquals(start, val, 0.0f)
        } else if (start instanceof Double) {
            Assert.assertEquals(start, val, 0.0d)
        } else {
            Assert.assertEquals(start, val)
        }
    }

    void 'verify incremental dates'(Date date, Integer calendarField, Integer step) {
        expect:
        date.before(DateCategory.increment(date).next())
        date.before(DateCategory.increment(date, 100).next())
        date.before(DateCategory.increment(date, 4, Calendar.MINUTE).next())

        and: "Increment by several criteria: seconds/minutes/hours/weeks/month/years"
        date.before(new IncrementalDate().seconds(5).minutes(1).hours(2).days(3).years(1).next())

        5 == DateCategory.incrementArray(new Date(), [(Calendar.MINUTE): 2, (Calendar.HOUR): 1], 5).next().length

        and: "verify in a loop"
        final Value<Date> incrementalDate = DateCategory.increment(date, calendarField, step)
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
        defaultValue != StringValue.increment(defaultValue).next()
        defaultValue != StringValue.increment(defaultValue, pattern).next()
        defaultValue != StringValue.increment(defaultValue, pattern, index).next()

        5 == StringValue.incrementArray(defaultValue, 5).next().length
        5 == StringValue.incrementArray(defaultValue, pattern, 5).next().length
        5 == StringValue.incrementArray(defaultValue, pattern, index, 5).next().length

        and: "verify in a loop"
        final incrementalString = StringValue.increment(defaultValue, pattern, index)
        for (int i = 0; i < 5; i++) {
            Assert.assertNotEquals(defaultValue, incrementalString.next())
        }

        where:
        defaultValue << ["Attempt N", "Test string ", "I would like ", "I would like to take ",]
        pattern << ["%s%d", "%s%d", "%s%d candies", "%s%d day offs",]
        index << [1, 1, 1, 1,]
    }

}
