package org.prismus.scrambler.property;

import org.prismus.scrambler.Property;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.*;

/**
 * @author Serge Pruteanu
 */
public class Random {
    private static final String NOT_SUPPORTED_RANGE_TYPE_MSG = "Not supported range method for provided class type: %s, range of [%s, %s]";

    private static Map<Class, Class<? extends Property>> propertyTypeMap = lookupPropertyTypeMap();

    public static <T> Property<T> of(String propertyName, Class<T> clazzType) {
        return of(propertyName, clazzType, null);
    }

    @SuppressWarnings({"unchecked"})
    public static <T> Property<T> of(String propertyName, T value) {
        return of(propertyName, (Class<T>) value.getClass(), value);
    }

    @SuppressWarnings({"unchecked"})
    public static <T> Property<T> of(String propertyName, T minimum, T maximum) {
        return of(propertyName, (Class<T>) minimum.getClass(), minimum, maximum);
    }

    @SuppressWarnings({"unchecked"})
    public static <T> Property<T> of(String propertyName, Class<T> clazzType, T minimum, T maximum) {
        final Property<T> property = of(propertyName, clazzType, null);
        if (property instanceof AbstractRandomRange) {
            final AbstractRandomRange<T> randomRangeValue = (AbstractRandomRange<T>) property;
            randomRangeValue.minimumBound(minimum).maximumBound(maximum);
        } else {
            throw new UnsupportedOperationException(String.format(NOT_SUPPORTED_RANGE_TYPE_MSG, clazzType, minimum, maximum));
        }
        return property;
    }

    @SuppressWarnings({"unchecked"})
    public static <T> Property<T> of(String propertyName, Class<T> clazzType, T defaultValue) {
        if (propertyTypeMap.containsKey(clazzType)) {
            return (Property) Util.createInstance(
                    propertyTypeMap.get(clazzType),
                    new Object[]{propertyName, defaultValue},
                    new Class[]{String.class, clazzType}
            );
        }
        throw new UnsupportedOperationException(String.format("The of method is not supported for property: %s, class type: %s, default value: %s",
                propertyName, clazzType, defaultValue));
    }

    public static <T> Property<T> of(String propertyName, List<T> values) {
        return new RandomElement<T>(propertyName, values);
    }

    public static <T> Property<T> of(String propertyName, Collection<T> collection) {
        return new RandomElement<T>(propertyName, new ArrayList<T>(collection));
    }

    static Map<Class, Class<? extends Property>> lookupPropertyTypeMap() {
        final Map<Class, Class<? extends Property>> typeMap = new LinkedHashMap<Class, Class<? extends Property>>();
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
