package org.prismus.scrambler.value

import groovy.transform.CompileStatic
import org.prismus.scrambler.Value

/**
 * todo: add description
 *
 * @author Serge Pruteanu
 */
@CompileStatic
class NumberCategory {

    public static <T extends Number> Value<T> increment(T self) {
        return ClassCategory.increment((Class<T>) self.getClass(), self, null)
    }

    public static <T extends Number> Value<T> increment(T self, T step) {
        return ClassCategory.increment((Class<T>) self.getClass(), self, step)
    }

    public static <T extends Number> Value incrementArray(T self, T step, Integer count) {
        return ClassCategory.incrementArray((Class<T>) self.getClass(), self, step, count)
    }

    public static <T extends Number> Value<T> random(T value) {
        return ClassCategory.random((Class<T>) value.getClass(), value)
    }

    public static <T extends Number> Value<T> random(T minimum, T maximum) {
        return ClassCategory.random((Class<T>) minimum.getClass(), minimum, maximum)
    }

    public static <T extends Number> Value<T> random(T val, T minimum, T maximum) {
        final Value<T> value = ClassCategory.random((Class<T>) val.getClass(), val)
        if (value instanceof AbstractRandomRange) {
            final AbstractRandomRange<Number> randomRangeValue = (AbstractRandomRange<T>) value
            randomRangeValue.between(minimum, maximum)
        } else {
            throw new UnsupportedOperationException(String.format(Types.NOT_SUPPORTED_RANGE_TYPE_MSG, val.getClass(), minimum, maximum))
        }
        return value
    }

}
