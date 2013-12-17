package org.prismus.scrambler.property;

import org.prismus.scrambler.Property;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * @author Serge Pruteanu
 */
public class Incremental extends Generic {
    private static Map<Class, Class<? extends Property>> propertyTypeMap = lookupPropertyTypeMap();

    @Override
    @SuppressWarnings({"unchecked"})
    public Object value() {
        Object value = super.value();
        if (value == null) {
            throw new IllegalStateException("Value object can't be null");
        }
        final Class valueClassType;
        if (value instanceof Class) {
            valueClassType = ((Class) value);
            value = null;
        } else {
            valueClassType = value.getClass();
        }
        return of(getName(), valueClassType, value).value();
    }

    @SuppressWarnings({"unchecked"})
    public static <T> Property<T> of(String propertyName, T value) {
        return of(propertyName, (Class<T>) value.getClass(), value, null);
    }

    @SuppressWarnings({"unchecked"})
    public static <T> Property<T> of(String propertyName, T value, Number step) {
        return of(propertyName, (Class<T>) value.getClass(), value, step);
    }

    public static <T> Property<T> of(String propertyName, Class<T> clazzType) {
        return of(propertyName, clazzType, null, null);
    }

    public static <T> Property<T> of(String propertyName, Class<T> clazzType, T defaultValue) {
        return of(propertyName, clazzType, defaultValue, null);
    }

    public static <T> Property<T> of(String propertyName, Class<T> clazzType, Number step) {
        return of(propertyName, clazzType, null, step);
    }

    @SuppressWarnings({"unchecked"})
    public static <T> Property<T> of(String propertyName,
                                     Class<T> clazzType,
                                     T defaultValue,
                                     Number step) {
        if (propertyTypeMap.containsKey(clazzType)) {
            return (Property<T>) Util.createInstance(
                    propertyTypeMap.get(clazzType),
                    new Object[]{propertyName, defaultValue, step},
                    new Class[]{String.class, clazzType, clazzType}
            );
        }
        throw new UnsupportedOperationException(String.format("The of method is not supported for property: %s, class type: %s, default value: %s",
                propertyName, clazzType, defaultValue));
    }

    static Map<Class, Class<? extends Property>> lookupPropertyTypeMap() {
        final Map<Class, Class<? extends Property>> typeMap = new LinkedHashMap<Class, Class<? extends Property>>();
        typeMap.put(Byte.class, IncrementalByte.class);
        typeMap.put(Short.class, IncrementalShort.class);
        typeMap.put(Double.class, IncrementalDouble.class);
        typeMap.put(BigDecimal.class, IncrementalBigDecimal.class);
        typeMap.put(Float.class, IncrementalFloat.class);
        typeMap.put(Integer.class, IncrementalInteger.class);
        typeMap.put(Long.class, IncrementalLong.class);
        typeMap.put(String.class, IncrementalString.class);
        typeMap.put(Date.class, IncrementalDate.class);
        typeMap.put(java.sql.Date.class, IncrementalDate.class);
        typeMap.put(Timestamp.class, IncrementalDate.class);
        return typeMap;
    }
}
