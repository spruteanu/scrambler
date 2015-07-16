package org.prismus.scrambler.value;

import org.prismus.scrambler.Value;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.Timestamp;
import java.util.*;

/**
 * @author Serge Pruteanu
 */
public class Random {
    private static final String NOT_SUPPORTED_RANGE_TYPE_MSG = "Not supported range method for provided class type: %s, range of [%s, %s]";

    private static Map<Class, Class<? extends Value>> propertyTypeMap = lookupPropertyTypeMap();

    public static <T> Value<T> of(Class<T> clazzType) {
        return of(clazzType, null);
    }

    @SuppressWarnings({"unchecked"})
    public static Value<Number> of(Number value) {
        return of((Class) value.getClass(), value);
    }

    public static RandomDate of(Date value) {
        return new RandomDate(value);
    }

    @SuppressWarnings({"unchecked"})
    public static Value<Number> of(Number minimum, Number maximum) {
        return of((Class) minimum.getClass(), minimum, maximum);
    }

    public static RandomDate of(Date minimum, Date maximum) {
        return new RandomDate(minimum, maximum);
    }

    public static RandomString of(String value) {
        return new RandomString(value);
    }

    public static RandomString of(String value, Integer count) {
        return new RandomString(value, count);
    }

    public static RandomString of(String value, Integer count, Boolean includeLetters) {
        return new RandomString(value, count, includeLetters);
    }

    public static RandomString of(String value, Integer count, Boolean includeLetters, Boolean includeNumbers) {
        return new RandomString(value, count, includeLetters, includeNumbers);
    }

    @SuppressWarnings({"unchecked"})
    public static <T> Value<T> of(Class<T> clazzType, T minimum, T maximum) {
        final Value<T> value = of(clazzType, null);
        if (value instanceof AbstractRandomRange) {
            final AbstractRandomRange<T> randomRangeValue = (AbstractRandomRange<T>) value;
            randomRangeValue.minimumBound(minimum).maximumBound(maximum);
        } else {
            throw new UnsupportedOperationException(String.format(NOT_SUPPORTED_RANGE_TYPE_MSG, clazzType, minimum, maximum));
        }
        return value;
    }

    @SuppressWarnings({"unchecked"})
    public static Value<Number> of(Number val, Number minimum, Number maximum) {
        final Value<Number> value = of((Class<Number>) val.getClass(), val);
        if (value instanceof AbstractRandomRange) {
            final AbstractRandomRange<Number> randomRangeValue = (AbstractRandomRange<Number>) value;
            randomRangeValue.minimumBound(minimum).maximumBound(maximum);
        } else {
            throw new UnsupportedOperationException(String.format(NOT_SUPPORTED_RANGE_TYPE_MSG, val.getClass(), minimum, maximum));
        }
        return value;
    }

    @SuppressWarnings({"unchecked"})
    public static Value<Date> of(Date val, Date minimum, Date maximum) {
        return new RandomDate(val, minimum, maximum);
    }

    @SuppressWarnings({"unchecked"})
    public static <T> Value<T> of(Class<T> clazzType, T defaultValue) {
        if (propertyTypeMap.containsKey(clazzType)) {
            if (clazzType.isArray()) {
                return of(clazzType, defaultValue, (Integer) null);
            } else {
                if (clazzType.isPrimitive()) {
                    return (Value) Util.createInstance(
                            propertyTypeMap.get(clazzType), null, null
                    );
                } else {
                    return (Value) Util.createInstance(
                            propertyTypeMap.get(clazzType),
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
    public static <T> Value<T> of(Class<T> clazzType, T defaultValue, Integer count) {
        final Class<?> componentType = clazzType.getComponentType();
        Value valueType;
        if (defaultValue != null) {
            valueType = (Value) Util.createInstance(
                    propertyTypeMap.get(componentType),
                    new Object[]{defaultValue,},
                    new Class[]{componentType,}
            );
        } else {
            valueType = (Value) Util.createInstance(
                    propertyTypeMap.get(componentType),
                    new Object[]{},
                    new Class[]{}
            );
        }
        final Value<T> value;
        if (componentType.isPrimitive()) {
            value = (Value) Util.createInstance(
                    propertyTypeMap.get(clazzType),
                    new Object[]{defaultValue, count, valueType}
                    , new Class[]{clazzType, Integer.class, Object.class}
            );
        } else {
            value = new ValueArray(clazzType, valueType);
        }
        return value;
    }

    public static <T> Value<T> randomOf(List<T> values) {
        return new RandomElement<T>(values);
    }

    public static <T> Value<T> randomOf(Collection<T> collection) {
        return new RandomElement<T>(new ArrayList<T>(collection));
    }

    public static boolean isSupportedType(Class type) {
        return propertyTypeMap.containsKey(type);
    }

    public static Set<Class> getSupportedTypes() {
        return Collections.unmodifiableSet(propertyTypeMap.keySet());
    }

    static Map<Class, Class<? extends Value>> lookupPropertyTypeMap() {
        final Map<Class, Class<? extends Value>> typeMap = new LinkedHashMap<Class, Class<? extends Value>>();
        typeMap.put(Byte.TYPE, RandomByte.class);
        typeMap.put(Byte.class, RandomByte.class);
        typeMap.put(byte[].class, ByteValueArray.class);

        typeMap.put(Short.TYPE, RandomShort.class);
        typeMap.put(Short.class, RandomShort.class);
        typeMap.put(short[].class, ShortValueArray.class);

        typeMap.put(Boolean.TYPE, RandomBoolean.class);
        typeMap.put(Boolean.class, RandomBoolean.class);
        typeMap.put(boolean[].class, BooleanValueArray.class);

        typeMap.put(Double.TYPE, RandomDouble.class);
        typeMap.put(Double.class, RandomDouble.class);
        typeMap.put(double[].class, DoubleValueArray.class);

        typeMap.put(Float.TYPE, RandomFloat.class);
        typeMap.put(Float.class, RandomFloat.class);
        typeMap.put(float[].class, FloatValueArray.class);

        typeMap.put(Integer.TYPE, RandomInteger.class);
        typeMap.put(Integer.class, RandomInteger.class);
        typeMap.put(int[].class, IntValueArray.class);

        typeMap.put(Long.TYPE, RandomLong.class);
        typeMap.put(Long.class, RandomLong.class);
        typeMap.put(long[].class, LongValueArray.class);

        typeMap.put(BigInteger.class, RandomBigInteger.class);
        typeMap.put(BigDecimal.class, RandomBigDecimal.class);

        typeMap.put(String.class, RandomString.class);
        typeMap.put(Date.class, RandomDate.class);
        typeMap.put(java.sql.Date.class, RandomDate.class);
        typeMap.put(Timestamp.class, RandomDate.class);
        return typeMap;
    }

}
