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
        return increment(self, null, (Class<N>) self.getClass());
    }

    @SuppressWarnings({"unchecked"})
    public static <N extends Number> Value<N> increment(N self, N step) {
        return increment(self, step, (Class<N>) self.getClass());
    }

    @SuppressWarnings("unchecked")
    public static <N extends Number> Value incrementArray(N self, N step, Integer count) {
        return ClassValue.incrementArray((Class<N>) self.getClass(), self, step, count);
    }

    @SuppressWarnings({"unchecked"})
    public static <N extends Number> Value<N> increment(N self, N step, Class<N> clazzType) {
        if (Types.incrementTypeMap.containsKey(clazzType)) {
            final Value value;
            if (clazzType.isArray()) {
                value = ClassValue.incrementArray(clazzType, self, step, null);
            } else {
                value = (Value) Util.createInstance(
                        Types.incrementTypeMap.get(clazzType),
                        new Object[]{self, step},
                        new Class[]{clazzType, (step != null ? step.getClass() : clazzType)}
                );
            }
            return value;
        }
        throw new UnsupportedOperationException(String.format("The of method is not supported for class type: %s, default value: %s", clazzType, self));
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

    @SuppressWarnings("unchecked")
    public static <N extends Number> Value<N> randomArray(N minimum, N maximum, Class clazzType, Integer count) {
        Util.checkPositiveCount(count);

        Object defaultValue = minimum != null ? minimum : maximum != null ? maximum : null;
        if (clazzType == null) {
            clazzType = defaultValue != null ? defaultValue.getClass() : null;
        }
        if (clazzType == null) {
            throw new IllegalArgumentException(String.format("Either minimum: %s or maximum: %s should be not null", minimum, maximum));
        }
        boolean primitive = false;
        Class<?> componentType;
        if (clazzType.isArray()) {
            componentType = clazzType.getComponentType();
            if (componentType.isPrimitive()) {
                primitive = true;
                componentType = Util.primitiveWrapperMap.get(componentType);
            }
        } else {
            componentType = clazzType;
        }

        Value instance = (Value) Util.createInstance(
                Types.randomTypeMap.get(componentType),
                new Object[]{minimum, maximum},
                new Class[]{componentType, componentType}
        );
        final Value<N> value;
        if (primitive) {
            value = (Value<N>) Util.createInstance(
                    Types.randomTypeMap.get(clazzType),
                    new Object[]{clazzType.isInstance(defaultValue) ? defaultValue : null, count, instance},
                    new Class[]{clazzType, Integer.class, Object.class}
            );
        } else {
            value = new ArrayValue(clazzType, count, instance);
        }
        return value;
    }
}
