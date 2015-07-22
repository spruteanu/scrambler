package org.prismus.scrambler.value

import org.junit.Assert
import org.prismus.scrambler.Value
import spock.lang.Specification

/**
 * @author Serge Pruteanu
 */
class RandomTest extends Specification {

    static boolean isBetween(def minimum, def maximum, def value) {
        return value >= minimum && value <= maximum
    }

    void 'verify random number generation'(Number minimum, Number maximum, Integer count) {
        given:
        Value<Number> randomNumber = NumberValue.random(minimum, maximum)

        expect:
        null != NumberValue.random(maximum).next()

        for (int i = 0; i < 100; i++) {
            Assert.assertTrue(isBetween(minimum, maximum, randomNumber.next()))
        }

        and: "verify array creation"
        Number[] numberValues = ClassValue.randomArray(null, minimum, maximum, count).next()
        for (int i = 0; i < numberValues.length; i++) {
            Assert.assertTrue(isBetween(minimum, maximum, numberValues[i]))
        }

        where:
        minimum << [1, 100L, new Integer(1).shortValue(), new Integer(1).byteValue(),
                    1.0f, 5.0f, 1.0d, 5.0d,
                    new Integer(6).toBigInteger(), new Integer(7).toBigDecimal(),]
        maximum << [10, 1000L, new Integer(300).shortValue(), new Integer(127).byteValue(),
                    10001.0f, 50005.0f, 10001.0d, 50005.0d,
                    new Integer(600).toBigInteger(), new Integer(6389).toBigDecimal(),]
        count << [5, 3, null, 10, 20, null, 13, null, 20, null,]
    }

    void 'verify random primitives generation'(Class type, Number minimum, Number maximum, Integer count) {
        given:
        Value numberValues = ClassValue.randomArray(type, minimum, maximum, count)

        expect:
        for (int i = 0; i < 5; i++) {
            final values = numberValues.next()
            Assert.assertNotNull(values)
            Assert.assertTrue(values.length > 0)
            if (count != null) {
                Assert.assertEquals(count, values.length)
            }
            for (int j = 0; j < values.length; j++) {
                Assert.assertTrue(isBetween(minimum, maximum, values[j]))
            }
        }

        where:
        type << [int[], long[], short[], byte[], float[], double[],]
        minimum << [1, 100L, new Integer(1).shortValue(), new Integer(1).byteValue(), 1.0f, 1.0d,]
        maximum << [10, 1000L, new Integer(300).shortValue(), new Integer(127).byteValue(), 10001.0f, 10001.0d,]
        count << [5, 3, null, 10, 20, null,]
    }

    void 'verify random boolean generation'(Class type, Boolean value, Integer count) {
        expect:
        null != BooleanValue.random(value).next()
        null != BooleanValue.random(value).next()

        and: ''
        final numberValues = BooleanValue.random(value, count, type)
        for (int i = 0; i < 5; i++) {
            final values = numberValues.next()
            Assert.assertNotNull(values)
            Assert.assertTrue(values.length > 0)
            if (count != null) {
                Assert.assertEquals(count, values.length)
            }
        }

        where:
        type << [boolean[], null,]
        value << [Boolean.TRUE, Boolean.FALSE,]
        count << [5, null,]
    }

    void 'verify random dates'(Date date, Date minimum, Date maximum, Integer count) {
        expect:
        date.before(DateValue.random(date).next())
        date.before(DateValue.random(minimum, (Date) null).usingValue(date).next())
        isBetween(minimum, maximum, DateValue.random(date, minimum, maximum).next())

        and: "verify in a loop"
        final Value<Date[]> randomDate = DateValue.randomArray(date, minimum, maximum, count)
        final Date[] dates = randomDate.next();
        if (count != null) {
            Assert.assertEquals(count, dates.length)
        }
        for (int i = 0; i < dates.length; i++) {
            Assert.assertTrue(isBetween(minimum, maximum, dates[i]))
        }

        where:
        date << [new Date(), new Date(), new Date(), new Date(),]
        minimum << [new Date(), new Date(), new Date(), new Date(),]
        maximum << [DateValue.increment(new Date()).next(), DateValue.increment(new Date()).next(), DateValue.increment(new Date()).next(), DateValue.increment(new Date()).next(),]
        count << [5, 3, null, 10,]
    }

    void 'verify random string'(String pattern, Integer count) {
        expect:
        pattern != StringValue.random(pattern).next()
        pattern != StringValue.random(pattern, count).next()

        5 == StringValue.randomArray(pattern, 5).next().length
        5 == StringValue.randomArray(pattern, count, 5).next().length

        and: "verify in a loop"
        final randomValue = StringValue.random(pattern, count)
        for (int i = 0; i < 5; i++) {
            final generatedString = randomValue.next()
            Assert.assertTrue(generatedString.length() > 0)
            Assert.assertNotEquals(pattern, generatedString)
            if (count != null) {
                Assert.assertEquals(count, generatedString.length())
            }
        }

        where:
        pattern << ["Attempt N", "Test string ", "I would like ", "I would like to take ",]
        count << [100, null, null, 64,]
    }

    void 'verify random collection element'() {
        given:
        final randomElement = CollectionValue.randomOf(randoms)
        final containerSet =  new HashSet(new ArrayList(randoms))

        expect:
        for (int i = 0; i < randoms.size(); i++) {
            Assert.assertTrue(containerSet.contains(randomElement.next()))
        }

        where:
        randoms << [[1..30], new HashSet<>(['a'..'z']), ]
    }

    void 'verify random array element'() {
        given:
        final randomElement = ObjectValue.randomOf(randoms)
        final containerSet = new HashSet(Arrays.asList(randoms))

        expect:
        for (int i = 0; i < randoms.size(); i++) {
            Assert.assertTrue(containerSet.contains(randomElement.next()))
        }

        where:
        randoms << [['aaa', 'bbb', 'vvv'] as String[], [1, 2, 3, 4, 5] as int[]]
    }

}
