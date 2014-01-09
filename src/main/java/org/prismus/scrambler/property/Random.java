package org.prismus.scrambler.property;

import org.prismus.scrambler.Value;

import java.math.BigDecimal;
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

    public static RandomString of(String value, Integer count, boolean includeLetters) {
        return new RandomString(value, count, includeLetters);
    }

    public static RandomString of(String value, Integer count, boolean includeLetters, boolean includeNumbers) {
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
        final Value<Number> value = of((Class<Number>)val.getClass(), null);
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
            return (Value) Util.createInstance(
                    propertyTypeMap.get(clazzType),
                    new Object[]{defaultValue},
                    new Class[]{clazzType}
            );
        }
        throw new UnsupportedOperationException(String.format("The of method is not supported for class type: %s, default value: %s",
                clazzType, defaultValue));
    }

    public static <T> Value<T> randomOf(List<T> values) {
        return new RandomElement<T>(values);
    }

    public static <T> Value<T> randomOf(Collection<T> collection) {
        return new RandomElement<T>(new ArrayList<T>(collection));
    }

    static Map<Class, Class<? extends Value>> lookupPropertyTypeMap() {
        final Map<Class, Class<? extends Value>> typeMap = new LinkedHashMap<Class, Class<? extends Value>>();
        typeMap.put(Byte.class, RandomByte.class);
        typeMap.put(Short.class, RandomShort.class);
        typeMap.put(Boolean.class, RandomBoolean.class);
        typeMap.put(Double.class, RandomDouble.class);
        typeMap.put(BigDecimal.class, RandomBigDecimal.class);
        typeMap.put(Float.class, RandomFloat.class);
        typeMap.put(Integer.class, RandomInteger.class);
        typeMap.put(Long.class, RandomLong.class);
        typeMap.put(String.class, RandomString.class);
        typeMap.put(Date.class, RandomDate.class);
        typeMap.put(java.sql.Date.class, RandomDate.class);
        typeMap.put(Timestamp.class, RandomDate.class);
        return typeMap;
    }

}
