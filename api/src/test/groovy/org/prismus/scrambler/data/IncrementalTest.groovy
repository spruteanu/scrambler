package org.prismus.scrambler.data

import org.junit.Assert
import org.prismus.scrambler.ArrayScrambler
import org.prismus.scrambler.DateScrambler
import org.prismus.scrambler.NumericScrambler
import org.prismus.scrambler.StringScrambler
import org.prismus.scrambler.Data
import spock.lang.Specification

/**
 * @author Serge Pruteanu
 */
class IncrementalTest extends Specification {

    void 'verify incremental number generation'(Number start, Number step, Integer count) {
        given:
        Data<Number> numberData = NumericScrambler.increment(start, step)

        expect:
        numberData.get() == start
        for (int i = 0; i < 5; i++) {
            Assert.assertEquals(numberData.get() + step, numberData.next())
        }

        and: "verify array creation"
        Data<Number[]> dataNumbers = ArrayScrambler.incrementArray(start, step, count)
        for (int i = 0; i < 5; i++) {
            Number[] data = dataNumbers.next()
            Assert.assertNotNull(data)
            Assert.assertTrue(data.length > 0)
            if (count != null) {
                Assert.assertEquals(count, data.length)
            }
            Assert.assertEquals(start, data[0])
            for (int j = 1; j < data.length; j++) {
                Assert.assertEquals(start + step, data[j])
                start = data[j]
            }
            start += step;
        }

        where:
        start << [1, 12L, new Integer(1).shortValue(), new Integer(1).byteValue(), new Integer(6).toBigInteger(), new Integer(7).toBigDecimal(),]
        step << [10, 10L, new Integer(3).shortValue(), new Integer(1).byteValue(), new Integer(6).toBigInteger(), new Integer(6).toBigDecimal(),]
        count << [5, 3, null, 10, 20, null]
    }

    void 'verify incremental float data'(Float start, Float step, Integer count) {
        given:
        Data<Float> dataNumber = NumericScrambler.increment(start, step)

        expect:
        dataNumber.get() == start
        for (int i = 0; i < 5; i++) {
            Assert.assertEquals(Float.sum(dataNumber.get(), step), dataNumber.next(), 0.0f)
        }

        and: "verify array creation"
        Data<Float[]> data = ArrayScrambler.incrementArray(start, step, count)
        for (int i = 0; i < 5; i++) {
            Float[] floats = data.next()
            Assert.assertNotNull(floats)
            Assert.assertTrue(floats.length > 0)
            if (count != null) {
                Assert.assertEquals(count, floats.length)
            }
            Assert.assertEquals(start, floats[0], 0.00001f)
            for (int j = 1; j < floats.length; j++) {
                Assert.assertEquals(start + step, floats[j], 0.00001f)
                start = floats[j]
            }
            start += step;
        }

        where:
        start << [1.0f, 5.0f]
        step << [0.2f, 0.3f]
        count << [5, null,]
    }

    void 'verify incremental double data'(Double start, Double step, Integer count) {
        given:
        Data<Double> numberData = NumericScrambler.increment(start, step)

        expect:
        numberData.get() == start
        for (int i = 0; i < 5; i++) {
            Assert.assertEquals(numberData.get() + step, numberData.next(), 0.0)
        }

        and: "verify array creation"
        Data<Double[]> data = ArrayScrambler.incrementArray(start, step, count)
        for (int i = 0; i < 5; i++) {
            Double[] doubles = data.next()
            Assert.assertNotNull(doubles)
            Assert.assertTrue(doubles.length > 0)
            if (count != null) {
                Assert.assertEquals(count, doubles.length)
            }
            Assert.assertEquals(start, doubles[0], 0.0)
            for (int j = 1; j < doubles.length; j++) {
                Assert.assertEquals(start + step, doubles[j], 0.0)
                start = doubles[j]
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
        Data numberData = ArrayScrambler.incrementArray(type, start, step, count)

        expect:
        for (int i = 0; i < 5; i++) {
            final data = numberData.next()
            Assert.assertNotNull(data)
            Assert.assertTrue(data.length > 0)
            if (count != null) {
                Assert.assertEquals(count, data.length)
            }
            checkEqual(start, data[0])
            for (int j = 1; j < data.length; j++) {
                final val = data[j]
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
        date.before(DateScrambler.increment(date).next())
        date.before(DateScrambler.increment(date, calendarField).next())
        date.before(DateScrambler.increment(date, calendarField, Calendar.MINUTE).next())

        and: "Increment by several criteria: seconds/minutes/hours/weeks/month/years"
        date.before(new IncrementalDate().seconds(5).minutes(1).hours(2).days(3).years(1).next())

        5 == DateScrambler.incrementArray(new Date(), [(Calendar.MINUTE): 2, (Calendar.HOUR): 1], 5).next().length

        and: "verify in a loop"
        final Data<Date> incrementalDate = DateScrambler.increment(date, calendarField, step)
        for (int i = 0; i < 5; i++) {
            final nextDate = incrementalDate.next()
            Assert.assertTrue(date.before(nextDate))
        }

        where:
        date << [new Date(), new Date(), new Date(), new Date(),]
        calendarField << [Calendar.HOUR, Calendar.DATE, Calendar.MONTH, Calendar.YEAR,]
        step << [1, 1, 1, 1,]
    }

    void 'verify string incremental'(String data, String pattern, Integer index) {
        expect:
        data != StringScrambler.increment(data).next()
        data != StringScrambler.increment(data, pattern).next()
        data != StringScrambler.increment(data, pattern, index).next()

        5 == StringScrambler.incrementArray(data, 5).next().length
        5 == StringScrambler.incrementArray(data, pattern, 5).next().length
        5 == StringScrambler.incrementArray(data, pattern, index, 5).next().length

        and: "verify in a loop"
        final incrementalString = StringScrambler.increment(data, pattern, index)
        for (int i = 0; i < 5; i++) {
            Assert.assertNotEquals(data, incrementalString.next())
        }

        where:
        data << ["Attempt N", "Test string ", "I would like ", "I would like to take ",]
        pattern << ["%s%d", "%s%d", "%s%d candies", "%s%d day offs",]
        index << [1, 1, 1, 1,]
    }

}
