package org.prismus.scrambler.value;

import org.prismus.scrambler.Value;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.Timestamp;
import java.util.*;

/**
 * Facade class that generates random values according to type rules: <><br/>
 * <li/>Numbers are generated in defined range
 * <li/>Dates are generated using {@link Calendar} fields in a defined range
 * <li/>Strings are generating using provided patterns
 * <p/>
 * Along with one type generation, it is possible to get randoms array accordingly
 *
 * @author Serge Pruteanu
 */
public class Random {
    private static final String NOT_SUPPORTED_RANGE_TYPE_MSG = "Not supported range method for provided class type: %s, range of [%s, %s]";

    private static Map<Class, Class<? extends Value>> propertyTypeMap = lookupPropertyTypeMap();

    public static <T> Value<T> of(Class<T> clazzType) {
        return of(clazzType, null);
    }

    //------------------------------------------------------------------------------------------------------------------
    // Generic methods
    //------------------------------------------------------------------------------------------------------------------
    @SuppressWarnings({"unchecked"})
    public static <T> Value<T> of(Class<T> clazzType, T minimum, T maximum) {
        final Value<T> value = of(clazzType, null);
        if (value instanceof AbstractRandomRange) {
            final AbstractRandomRange<T> randomRangeValue = (AbstractRandomRange<T>) value;
            randomRangeValue.between(minimum, maximum);
        } else {
            throw new UnsupportedOperationException(String.format(NOT_SUPPORTED_RANGE_TYPE_MSG, clazzType, minimum, maximum));
        }
        return value;
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
        return new ListRandomElement<T>(values);
    }

    public static <T> Value<T> randomOf(Collection<T> collection) {
        return new ListRandomElement<T>(new ArrayList<T>(collection));
    }

    public static <T> Value<T> randomOf(T[] array) {
        return new ArrayRandomElement<T>(array);
    }

    //------------------------------------------------------------------------------------------------------------------
    // Number methods
    //------------------------------------------------------------------------------------------------------------------
    @SuppressWarnings({"unchecked"})
    public static <N extends Number> Value<N> of(N value) {
        return of((Class) value.getClass(), value);
    }

    @SuppressWarnings({"unchecked"})
    public static <N extends Number> Value<N> of(N minimum, N maximum) {
        return of((Class) minimum.getClass(), minimum, maximum);
    }

    @SuppressWarnings({"unchecked"})
    public static <N extends Number> Value<N> of(N val, N minimum, N maximum) {
        final Value<N> value = of((Class<N>) val.getClass(), val);
        if (value instanceof AbstractRandomRange) {
            final AbstractRandomRange<Number> randomRangeValue = (AbstractRandomRange<Number>) value;
            randomRangeValue.between(minimum, maximum);
        } else {
            throw new UnsupportedOperationException(String.format(NOT_SUPPORTED_RANGE_TYPE_MSG, val.getClass(), minimum, maximum));
        }
        return value;
    }

    @SuppressWarnings("unchecked")
    public static <N extends Number> Value<N> arrayOf(Class clazzType, Integer count, N minimum, N maximum) {
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
                propertyTypeMap.get(componentType),
                new Object[]{minimum, maximum},
                new Class[]{componentType, componentType}
        );
        final Value<N> value;
        if (primitive) {
            value = (Value<N>) Util.createInstance(
                    propertyTypeMap.get(clazzType),
                    new Object[]{clazzType.isInstance(defaultValue) ? defaultValue : null, count, instance},
                    new Class[]{clazzType, Integer.class, Object.class}
            );
        } else {
            value = new ValueArray(clazzType, count, instance);
        }
        return value;
    }

    //------------------------------------------------------------------------------------------------------------------
    // Dates methods
    //------------------------------------------------------------------------------------------------------------------
    public static RandomDate of(Date value) {
        return new RandomDate(value);
    }

    public static RandomDate of(Date minimum, Date maximum) {
        return new RandomDate(minimum, maximum);
    }

    @SuppressWarnings({"unchecked"})
    public static Value<Date> of(Date val, Date minimum, Date maximum) {
        return new RandomDate(val, minimum, maximum);
    }

    @SuppressWarnings({"unchecked"})
    public static ValueArray<Date> of(Integer count, Date val, Date minimum, Date maximum) {
        final RandomDate randomDate = new RandomDate(val, minimum, maximum);
        randomDate.next();
        return new ValueArray<Date>(Date.class, count, randomDate);
    }

    //------------------------------------------------------------------------------------------------------------------
    // String methods
    //------------------------------------------------------------------------------------------------------------------
    public static RandomString of(String value) {
        return new RandomString(value);
    }

    public static RandomString of(String value, Integer count) {
        return new RandomString(value, count);
    }

    public static ValueArray<String> of(Integer arrayCount, String value) {
        return new ValueArray<String>(String.class, arrayCount, of(value));
    }

    public static ValueArray<String> of(Integer arrayCount, String value, Integer count) {
        return new ValueArray<String>(String.class, arrayCount, of(value, count));
    }

    //------------------------------------------------------------------------------------------------------------------
    // Boolean methods
    //------------------------------------------------------------------------------------------------------------------
    public static Value<Boolean> of(Boolean value) {
        return new RandomBoolean(value);
    }

    @SuppressWarnings("unchecked")
    public static Value of(Class clazzType, Integer count, Boolean value) {
        Util.checkPositiveCount(count);

        if (clazzType == null) {
            clazzType = value != null ? value.getClass() : null;
        }
        if (clazzType == null) {
            throw new IllegalArgumentException(String.format("Either classType: %s or value: %s should be not null", null, value));
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
                propertyTypeMap.get(componentType),
                new Object[]{value},
                new Class[]{componentType}
        );
        final Value valueArray;
        if (primitive) {
            valueArray = (Value) Util.createInstance(
                    propertyTypeMap.get(clazzType),
                    new Object[]{clazzType.isInstance(value) ? value : null, count, instance},
                    new Class[]{clazzType, Integer.class, Object.class}
            );
        } else {
            valueArray = new ValueArray(clazzType, count, instance);
        }
        return valueArray;
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
