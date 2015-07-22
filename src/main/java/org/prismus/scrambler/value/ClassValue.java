package org.prismus.scrambler.value;

import org.prismus.scrambler.Value;

import java.util.Collection;
import java.util.Map;

/**
 * todo: add description
 *
 * @author Serge Pruteanu
 */
public class ClassValue {

    @SuppressWarnings({"unchecked"})
    public static <T> Value<T> incrementArray(Class<T> self, T defaultValue, Object step, Integer count) {
        Util.checkPositiveCount(count);
        final Class<?> componentType = self.isArray() ? self.getComponentType() : self;
        final Value value;
        if (componentType.isPrimitive()) {
            value = (Value) Util.createInstance(
                    Types.incrementTypeMap.get(self),
                    new Object[]{
                            self.isInstance(defaultValue) ? defaultValue : null,
                            count,
                            (Value) Util.createInstance(
                                    Types.incrementTypeMap.get(componentType),
                                    new Object[]{self.isInstance(defaultValue) ? null : defaultValue, step},
                                    new Class[]{Util.primitiveWrapperMap.get(componentType), Util.primitiveWrapperMap.get(componentType)}
                            )},
                    new Class[]{self, Integer.class, Object.class}
            );
        } else {
            value = new ArrayValue(self, count, (Value) Util.createInstance(
                    Types.incrementTypeMap.get(componentType),
                    new Object[]{defaultValue, step},
                    new Class[]{componentType, componentType}
            ));
        }
        return value;
    }

    @SuppressWarnings({"unchecked"})
    public static <N extends Number> Value<N> increment(Class<N> self, N defaultValue, N step) {
        if (Types.incrementTypeMap.containsKey(self)) {
            final Value value;
            if (self.isArray()) {
                value = incrementArray(self, defaultValue, step, null);
            } else {
                value = (Value) Util.createInstance(
                        Types.incrementTypeMap.get(self),
                        new Object[]{defaultValue, step},
                        new Class[]{self, self}
                );
            }
            return value;
        }
        throw new UnsupportedOperationException(String.format("The of method is not supported for class type: %s, default value: %s", self, self));
    }

    public static <T> Value<T> random(Class<T> self) {
        return random(self, null);
    }

    @SuppressWarnings({"unchecked"})
    public static <T> Value<T> random(Class<T> self, T defaultValue) {
        if (Types.randomTypeMap.containsKey(self)) {
            if (self.isArray()) {
                return random(self, defaultValue, (Integer) null);
            } else {
                if (self.isPrimitive()) {
                    return (Value) Util.createInstance(
                            Types.randomTypeMap.get(self), null, null
                    );
                } else {
                    return (Value) Util.createInstance(
                            Types.randomTypeMap.get(self),
                            new Object[]{defaultValue},
                            new Class[]{self}
                    );
                }
            }
        }
        throw new UnsupportedOperationException(String.format("The of method is not supported for class type: %s, default value: %s",
                self, defaultValue));
    }

    public static <T> Value<T> random(Class<T> self, T minimum, T maximum) {
        final Value<T> value = random(self, null);
        if (value instanceof AbstractRandomRange) {
            final AbstractRandomRange<T> randomRangeValue = (AbstractRandomRange<T>) value;
            randomRangeValue.between(minimum, maximum);
        } else {
            throw new UnsupportedOperationException(String.format(Types.NOT_SUPPORTED_RANGE_TYPE_MSG, self, minimum, maximum));
        }
        return value;
    }

    @SuppressWarnings({"unchecked"})
    public static <T> Value<T> random(Class<T> self, T defaultValue, Integer count) {
        final Class<?> componentType = self.getComponentType();
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
                    Types.randomTypeMap.get(self),
                    new Object[]{defaultValue, count, valueType}
                    , new Class[]{self, Integer.class, Object.class}
            );
        } else {
            value = new ArrayValue(self, valueType);
        }
        return value;
    }

    @SuppressWarnings("unchecked")
    public static <T> Value<T> of(Class clazzType, Value val, Integer count) {
        if (clazzType.isPrimitive()) {
            final Class<? extends Value> arrayValueType = Types.primitivesTypeMap.get(clazzType);
            return (Value) Util.createInstance(
                    arrayValueType,
                    new Object[]{null, count, val}
                    , new Class[]{Types.arrayTypeMap.get(clazzType), Integer.class, Object.class}
            );
        } else {
            return new ArrayValue(clazzType, count, val);
        }
    }

    @SuppressWarnings("unchecked")
    public static <N extends Number> Value<N> randomArray(Class self, N minimum, N maximum, Integer count) {
        Util.checkPositiveCount(count);

        Object defaultValue = minimum != null ? minimum : maximum != null ? maximum : null;
        if (self == null) {
            self = defaultValue != null ? defaultValue.getClass() : null;
        }
        if (self == null) {
            throw new IllegalArgumentException(String.format("Either minimum: %s or maximum: %s should be not null", minimum, maximum));
        }
        boolean primitive = false;
        Class<?> componentType;
        if (self.isArray()) {
            componentType = self.getComponentType();
            if (componentType.isPrimitive()) {
                primitive = true;
                componentType = Util.primitiveWrapperMap.get(componentType);
            }
        } else {
            componentType = self;
        }

        Value instance = (Value) Util.createInstance(
                Types.randomTypeMap.get(componentType),
                new Object[]{minimum, maximum},
                new Class[]{componentType, componentType}
        );
        final Value<N> value;
        if (primitive) {
            value = (Value<N>) Util.createInstance(
                    Types.randomTypeMap.get(self),
                    new Object[]{self.isInstance(defaultValue) ? defaultValue : null, count, instance},
                    new Class[]{self, Integer.class, Object.class}
            );
        } else {
            value = new ArrayValue(self, count, instance);
        }
        return value;
    }

    public static <K> MapValue<K> mapOf(Class<Map<K, Object>> mapType, Map<K, Value> keyValueMap) {
        return new MapValue<K>(mapType, keyValueMap);
    }

    public static <V, T extends Collection<V>> CollectionValue<V, T> collectionOf(Class<V> clazzType, Value<V> value) {
        return new CollectionValue<V, T>(clazzType, value, null);
    }

}
