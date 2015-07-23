package org.prismus.scrambler.value

import groovy.transform.CompileStatic
import org.prismus.scrambler.Value

/**
 * todo: add description
 *
 * @author Serge Pruteanu
 */
@CompileStatic
class ClassCategory {

    public static <T> Value<T> incrementArray(Class<T> self, T defaultValue, T step, Integer count) {
        Util.checkPositiveCount(count)
        final Class<?> componentType = self.isArray() ? self.getComponentType() : self
        final Value value
        if (componentType.isPrimitive()) {
            value = (Value) Util.createInstance(
                    Types.incrementTypeMap.get(self),
                    [
                            self.isInstance(defaultValue) ? defaultValue : null, count,
                            (Value) Util.createInstance(
                                    Types.incrementTypeMap.get(componentType),
                                    [self.isInstance(defaultValue) ? null : defaultValue, step] as Object[],
                                    [Types.primitiveWrapperMap.get(componentType), Types.primitiveWrapperMap.get(componentType)] as Class[]
                            )
                    ] as Object[],
                    [self, Integer, Object] as Class[]
            )
        } else {
            value = new ArrayValue(self, count, (Value) Util.createInstance(
                    Types.incrementTypeMap.get(componentType), [defaultValue, step] as Object[], [componentType, componentType] as Class[]
            ))
        }
        return value
    }

    public static <T extends Number> Value<T> increment(Class<T> self, T defaultValue, T step) {
        if (Types.incrementTypeMap.containsKey(self)) {
            final Value<T> value
            if (self.isArray()) {
                value = incrementArray((Class<T>)self, defaultValue, step, null)
            } else {
                value = (Value<T>) Util.createInstance(Types.incrementTypeMap.get(self), [defaultValue, step] as Object[], [self, self] as Class[])
            }
            return value
        }
        throw new UnsupportedOperationException(String.format("The of method is not supported for class type: %s, default value: %s", self, self))
    }

    public static <T> Value<T> random(Class<T> self) {
        return random(self, (T)null)
    }

    public static <T> Value<T> random(Class<T> self, T defaultValue) {
        if (Types.randomTypeMap.containsKey(self)) {
            if (self.isArray()) {
                return random(self, defaultValue, (Integer) null)
            } else {
                if (self.isPrimitive()) {
                    return (Value) Util.createInstance(Types.randomTypeMap.get(self), null, null)
                } else {
                    return (Value) Util.createInstance(Types.randomTypeMap.get(self), [defaultValue] as Object[], [self] as Class[])
                }
            }
        }
        throw new UnsupportedOperationException(String.format("The of method is not supported for class type: %s, default value: %s", self, defaultValue))
    }

    public static <T> Value<T> random(Class<T> self, T minimum, T maximum) {
        final Value<T> value = random(self, (T)null)
        if (value instanceof AbstractRandomRange) {
            final AbstractRandomRange<T> randomRangeValue = (AbstractRandomRange<T>) value
            randomRangeValue.between(minimum, maximum)
        } else {
            throw new UnsupportedOperationException(String.format(Types.NOT_SUPPORTED_RANGE_TYPE_MSG, self, minimum, maximum))
        }
        return value
    }

    public static <T> Value<T> random(Class<T> self, T defaultValue, Integer count) {
        final Class<?> componentType = self.getComponentType()
        Value valueType
        if (defaultValue != null) {
            valueType = (Value) Util.createInstance(Types.randomTypeMap.get(componentType), [defaultValue,] as Object[], [componentType,] as Class[])
        } else {
            valueType = (Value) Util.createInstance(Types.randomTypeMap.get(componentType), [] as Object[], [] as Class[])
        }
        final Value<T> value
        if (componentType.isPrimitive()) {
            value = (Value) Util.createInstance(
                    Types.randomTypeMap.get(self), [defaultValue, count, valueType] as Object[], [self, Integer, Object] as Class[]
            )
        } else {
            value = new ArrayValue(self, valueType)
        }
        return value
    }

    public static <T> Value<T> of(Class clazzType, Value val, Integer count) {
        if (clazzType.isPrimitive()) {
            final Class<? extends Value> arrayValueType = Types.primitivesTypeMap.get(clazzType)
            return (Value) Util.createInstance(
                    arrayValueType,
                    [null, count, val] as Object[], [Types.arrayTypeMap.get(clazzType), Integer, Object] as Class[]
            )
        } else {
            return new ArrayValue(clazzType, count, val)
        }
    }

    public static <N extends Number> Value<N> randomArray(Class self, N minimum, N maximum, Integer count) {
        Util.checkPositiveCount(count)

        Object defaultValue = minimum != null ? minimum : maximum != null ? maximum : null
        if (self == null) {
            self = defaultValue != null ? defaultValue.getClass() : null
        }
        if (self == null) {
            throw new IllegalArgumentException(String.format("Either minimum: %s or maximum: %s should be not null", minimum, maximum))
        }
        boolean primitive = false
        Class<?> componentType
        if (self.isArray()) {
            componentType = self.getComponentType()
            if (componentType.isPrimitive()) {
                primitive = true
                componentType = Types.primitiveWrapperMap.get(componentType)
            }
        } else {
            componentType = self
        }

        Value instance = (Value) Util.createInstance(
                Types.randomTypeMap.get(componentType), [minimum, maximum] as Object[], [componentType, componentType] as Class[]
        )
        final Value<N> value
        if (primitive) {
            value = (Value<N>) Util.createInstance(Types.randomTypeMap.get(self),
                    [self.isInstance(defaultValue) ? defaultValue : null, count, instance] as Object[],
                    [self, Integer.class, Object.class] as Class[]
            )
        } else {
            value = new ArrayValue(self, count, instance)
        }
        return value
    }

    public static <K> MapValue<K> mapOf(Class<Map<K, Object>> mapType, Map<K, Value> keyValueMap) {
        return new MapValue<K>(mapType, keyValueMap)
    }

    public static <V, T extends Collection<V>> CollectionValue<V, T> collectionOf(Class<V> clazzType, Value<V> value) {
        return new CollectionValue<V, T>(clazzType, value, null)
    }

    public static <T> InstanceValue<T> instanceOf(Class<T> clazzType) {
        return instanceOf(clazzType, null)
    }

    public static <T> InstanceValue<T> instanceOf(Class<T> clazzType, Map<Object, Object> fieldMap) {
        return new InstanceValue<T>(clazzType).usingDefinitions(fieldMap)
    }

    public static <T> InstanceValue<T> instanceOf(String type) {
        return instanceOf(type, null)
    }

    public static <T> InstanceValue<T> instanceOf(String type, Map<Object, Object> fieldMap) {
        return new InstanceValue<T>(type).usingDefinitions(fieldMap)
    }

}
