package org.prismus.scrambler.value;

import org.prismus.scrambler.Value;

/**
 * todo: add description
 *
 * @author Serge Pruteanu
 */
public class NumberValue {
    @SuppressWarnings({"unchecked"})
    public static <N extends Number> Value<N> increment(N self) {
        return ClassValue.increment((Class<N>) self.getClass(), self, null);
    }

    @SuppressWarnings({"unchecked"})
    public static <N extends Number> Value<N> increment(N self, N step) {
        return ClassValue.increment((Class<N>) self.getClass(), self, step);
    }

    @SuppressWarnings("unchecked")
    public static <N extends Number> Value incrementArray(N self, N step, Integer count) {
        return ClassValue.incrementArray((Class<N>) self.getClass(), self, step, count);
    }

    @SuppressWarnings({"unchecked"})
    public static <N extends Number> Value<N> random(N value) {
        return ClassValue.random((Class<N>) value.getClass(), value);
    }

    @SuppressWarnings({"unchecked"})
    public static <N extends Number> Value<N> random(N minimum, N maximum) {
        return ClassValue.random((Class<N>) minimum.getClass(), minimum, maximum);
    }

    @SuppressWarnings({"unchecked"})
    public static <N extends Number> Value<N> random(N val, N minimum, N maximum) {
        final Value<N> value = ClassValue.random((Class<N>) val.getClass(), val);
        if (value instanceof AbstractRandomRange) {
            final AbstractRandomRange<Number> randomRangeValue = (AbstractRandomRange<Number>) value;
            randomRangeValue.between(minimum, maximum);
        } else {
            throw new UnsupportedOperationException(String.format(Types.NOT_SUPPORTED_RANGE_TYPE_MSG, val.getClass(), minimum, maximum));
        }
        return value;
    }

}
