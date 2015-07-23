package org.prismus.scrambler.value

import groovy.transform.CompileStatic
import org.prismus.scrambler.Value

/**
 * todo: add description
 *
 * @author Serge Pruteanu
 */
class NumberCategory {

    @CompileStatic
    public static <N extends Number> Value<N> increment(N self) {
        return ClassCategory.increment((Class<N>) self.getClass(), self, null)
    }

    @CompileStatic
    public static <N extends Number> Value<N> increment(N self, N step) {
        return ClassCategory.increment((Class<N>) self.getClass(), self, step)
    }

    @CompileStatic
    public static <N extends Number> Value incrementArray(N self, N step, Integer count) {
        return ClassCategory.incrementArray((Class<N>) self.getClass(), self, step, count)
    }

    @CompileStatic
    public static <N extends Number> Value<N> random(N value) {
        return ClassCategory.random((Class<N>) value.getClass(), value)
    }

    @CompileStatic
    public static <N extends Number> Value<N> random(N minimum, N maximum) {
        return ClassCategory.random((Class<N>) minimum.getClass(), minimum, maximum)
    }

    @CompileStatic
    public static <N extends Number> Value<N> random(N val, N minimum, N maximum) {
        final Value<N> value = ClassCategory.random((Class) val.getClass(), val)
        if (value instanceof AbstractRandomRange) {
            final AbstractRandomRange<Number> randomRangeValue = (AbstractRandomRange<N>) value
            randomRangeValue.between(minimum, maximum)
        } else {
            throw new UnsupportedOperationException(String.format(Types.NOT_SUPPORTED_RANGE_TYPE_MSG, val.getClass(), minimum, maximum))
        }
        return value
    }

}
