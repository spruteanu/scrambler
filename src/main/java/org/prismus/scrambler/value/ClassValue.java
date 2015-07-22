package org.prismus.scrambler.value;

import org.prismus.scrambler.Value;

/**
 * todo: add description
 *
 * @author Serge Pruteanu
 */
public class ClassValue {
    @SuppressWarnings({"unchecked"})
    public static Value incrementArray(Class clazzType, Object defaultValue, Object step, Integer count) {
        Util.checkPositiveCount(count);
        final Class<?> componentType = clazzType.isArray() ? clazzType.getComponentType() : clazzType;
        final Value value;
        if (componentType.isPrimitive()) {
            value = (Value) Util.createInstance(
                    Types.incrementTypeMap.get(clazzType),
                    new Object[]{
                            clazzType.isInstance(defaultValue) ? defaultValue : null,
                            count,
                            (Value) Util.createInstance(
                                    Types.incrementTypeMap.get(componentType),
                                    new Object[]{clazzType.isInstance(defaultValue) ? null : defaultValue, step},
                                    new Class[]{Util.primitiveWrapperMap.get(componentType), Util.primitiveWrapperMap.get(componentType)}
                            )},
                    new Class[]{clazzType, Integer.class, Object.class}
            );
        } else {
            value = new ArrayValue(clazzType, count, (Value) Util.createInstance(
                    Types.incrementTypeMap.get(componentType),
                    new Object[]{defaultValue, step},
                    new Class[]{componentType, componentType}
            ));
        }
        return value;
    }

    public static <T> Value<T> random(Class<T> clazzType) {
        return random(clazzType, null);
    }

    public static <T> Value<T> random(Class<T> clazzType, T minimum, T maximum) {
        final Value<T> value = random(clazzType, null);
        if (value instanceof AbstractRandomRange) {
            final AbstractRandomRange<T> randomRangeValue = (AbstractRandomRange<T>) value;
            randomRangeValue.between(minimum, maximum);
        } else {
            throw new UnsupportedOperationException(String.format(Types.NOT_SUPPORTED_RANGE_TYPE_MSG, clazzType, minimum, maximum));
        }
        return value;
    }

    @SuppressWarnings({"unchecked"})
    public static <T> Value<T> random(Class<T> clazzType, T defaultValue) {
        if (Types.randomTypeMap.containsKey(clazzType)) {
            if (clazzType.isArray()) {
                return random(clazzType, defaultValue, (Integer) null);
            } else {
                if (clazzType.isPrimitive()) {
                    return (Value) Util.createInstance(
                            Types.randomTypeMap.get(clazzType), null, null
                    );
                } else {
                    return (Value) Util.createInstance(
                            Types.randomTypeMap.get(clazzType),
                            new Object[]{defaultValue},
                            new Class[]{clazzType}
                    );
                }
            }
        }
        throw new UnsupportedOperationException(String.format("The of method is not supported for class type: %s, default value: %s",
                clazzType, defaultValue));
    }

    @SuppressWarnings({"unchecked"})
    public static <T> Value<T> random(Class<T> clazzType, T defaultValue, Integer count) {
        final Class<?> componentType = clazzType.getComponentType();
        Value valueType;
        if (defaultValue != null) {
            valueType = (Value) Util.createInstance(
                    Types.randomTypeMap.get(componentType),
                    new Object[]{defaultValue,},
                    new Class[]{componentType,}
            );
        } else {
            valueType = (Value) Util.createInstance(
                    Types.randomTypeMap.get(componentType),
                    new Object[]{},
                    new Class[]{}
            );
        }
        final Value<T> value;
        if (componentType.isPrimitive()) {
            value = (Value) Util.createInstance(
                    Types.randomTypeMap.get(clazzType),
                    new Object[]{defaultValue, count, valueType}
                    , new Class[]{clazzType, Integer.class, Object.class}
            );
        } else {
            value = new ArrayValue(clazzType, valueType);
        }
        return value;
    }
}
