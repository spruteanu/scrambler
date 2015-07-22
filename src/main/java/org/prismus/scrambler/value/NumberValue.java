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
        return increment(self, null, (Class) self.getClass());
    }

    @SuppressWarnings({"unchecked"})
    public static <N extends Number> Value<N> increment(N self, N step) {
        return increment(self, step, (Class) self.getClass());
    }

    @SuppressWarnings("unchecked")
    public static <N extends Number> Value incrementArray(N self, N step, Integer count) {
        return incrementArray(self, step, count, self.getClass());
    }

    @SuppressWarnings({"unchecked"})
    public static <N extends Number> Value<N> increment(N self, N step, Class<N> clazzType) {
        if (Types.incrementTypeMap.containsKey(clazzType)) {
            final Value value;
            if (clazzType.isArray()) {
                value = incrementArray(self, step, null, clazzType);
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
    // todo Serge: fix Object self, define 2 explicit methods that creates Number[] and primitive array
    public static Value incrementArray(Object self, Object step, Integer count, Class clazzType) {
        Util.checkPositiveCount(count);
        final Class<?> componentType = clazzType.isArray() ? clazzType.getComponentType() : clazzType;
        final Value value;
        if (componentType.isPrimitive()) {
            value = (Value) Util.createInstance(
                    Types.incrementTypeMap.get(clazzType),
                    new Object[]{
                            clazzType.isInstance(self) ? self : null,
                            count,
                            (Value) Util.createInstance(
                                    Types.incrementTypeMap.get(componentType),
                                    new Object[]{clazzType.isInstance(self) ? null : self, step},
                                    new Class[]{Util.primitiveWrapperMap.get(componentType), Util.primitiveWrapperMap.get(componentType)}
                            )},
                    new Class[]{clazzType, Integer.class, Object.class}
            );
        } else {
            value = new ArrayValue(clazzType, count, (Value) Util.createInstance(
                    Types.incrementTypeMap.get(componentType),
                    new Object[]{self, step},
                    new Class[]{componentType, componentType}
            ));
        }
        return value;
    }

    @SuppressWarnings({"unchecked"})
    public static <N extends Number> Value<N> random(N value) {
        return ClassValue.random((Class) value.getClass(), value);
    }

    @SuppressWarnings({"unchecked"})
    public static <N extends Number> Value<N> random(N minimum, N maximum) {
        return ClassValue.random((Class) minimum.getClass(), minimum, maximum);
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
