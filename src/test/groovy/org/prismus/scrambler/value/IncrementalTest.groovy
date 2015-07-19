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

}
