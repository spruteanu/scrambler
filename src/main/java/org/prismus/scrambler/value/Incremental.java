package org.prismus.scrambler.value;

import org.prismus.scrambler.Value;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.Timestamp;
import java.util.*;

/**
 * @author Serge Pruteanu
 */
public class Incremental {
    private static Map<Class, Class<? extends Value>> propertyTypeMap = lookupPropertyTypeMap();

    @SuppressWarnings({"unchecked"})
    public static Value<Number> of(Number value) {
        return of((Class) value.getClass(), value, null);
    }

    @SuppressWarnings({"unchecked"})
    public static Value<Number> of(Number value, Number step) {
        return of((Class) value.getClass(), value, step);
    }

    public static IncrementalDate of(Date value) {
        return new IncrementalDate(value);
    }

    public static IncrementalDate of(Date value, Integer step) {
        return new IncrementalDate(value, step);
    }

    public static IncrementalDate of(Date value, Integer step, Integer calendarField) {
        return new IncrementalDate(value, step, calendarField);
    }

    public static IncrementalString of(String value) {
        return new IncrementalString(value);
    }

    public static IncrementalString of(String value, String pattern) {
        return new IncrementalString(value, pattern);
    }

    public static IncrementalString of(String value, Integer index) {
        return new IncrementalString(value, index);
    }

    public static IncrementalString of(String value, String pattern, Integer index) {
        return new IncrementalString(value, pattern, index);
    }

    @SuppressWarnings({"unchecked"})
    public static <T> Value<T> of(Class<T> clazzType, T defaultValue, Number step) {
        if (propertyTypeMap.containsKey(clazzType)) {
            final Value value;
            if (clazzType.isArray()) {
                value = of(clazzType, defaultValue, null, step);
            } else {
                value = (Value) Util.createInstance(
                        propertyTypeMap.get(clazzType),
                        new Object[]{defaultValue, step},
                        new Class[]{clazzType, (step != null ? step.getClass() : clazzType)}
                );
            }
            return value;
        }
        throw new UnsupportedOperationException(String.format("The of method is not supported for class type: %s, default value: %s", clazzType, defaultValue));
    }

    @SuppressWarnings({"unchecked"})
    public static <T> Value<T> of(Class<T> clazzType, T defaultValue, Integer count, Number step) {
        final Class<?> componentType = clazzType.getComponentType();
        final Value valueType = (Value) Util.createInstance(
                propertyTypeMap.get(componentType),
                new Object[]{null, step},
                new Class[]{componentType, (step != null ? step.getClass() : componentType)}
        );
        final Value<T> value;
        if (componentType.isPrimitive()) {
            value = (Value) Util.createInstance(
                    propertyTypeMap.get(clazzType),
                    new Object[]{defaultValue, count, valueType},
                    new Class[]{clazzType, Integer.class, valueType.getClass()}
            );
        } else {
            value = new ValueArray(clazzType, valueType);
        }
        return value;
    }

    public static boolean isSupportedType(Class type) {
        return propertyTypeMap.containsKey(type);
    }

    public static Set<Class> getSupportedTypes() {
        return Collections.unmodifiableSet(propertyTypeMap.keySet());
    }

    static Map<Class, Class<? extends Value>> lookupPropertyTypeMap() {
        final Map<Class, Class<? extends Value>> typeMap = new LinkedHashMap<Class, Class<? extends Value>>();
        typeMap.put(Byte.TYPE, IncrementalByte.class);
        typeMap.put(Byte.class, IncrementalByte.class);
        typeMap.put(byte[].class, ByteValueArray.class);

        typeMap.put(Short.TYPE, IncrementalShort.class);
        typeMap.put(Short.class, IncrementalShort.class);
        typeMap.put(short[].class, ShortValueArray.class);

        typeMap.put(Double.TYPE, IncrementalDouble.class);
        typeMap.put(Double.class, IncrementalDouble.class);
        typeMap.put(double[].class, DoubleValueArray.class);

        typeMap.put(Float.TYPE, IncrementalFloat.class);
        typeMap.put(Float.class, IncrementalFloat.class);
        typeMap.put(float[].class, FloatValueArray.class);

        typeMap.put(Integer.TYPE, IncrementalInteger.class);
        typeMap.put(Integer.class, IncrementalInteger.class);
        typeMap.put(int[].class, IntValueArray.class);

        typeMap.put(Long.TYPE, IncrementalLong.class);
        typeMap.put(Long.class, IncrementalLong.class);
        typeMap.put(long[].class, LongValueArray.class);

        typeMap.put(BigInteger.class, IncrementalBigInteger.class);
        typeMap.put(BigDecimal.class, IncrementalBigDecimal.class);

        typeMap.put(String.class, IncrementalString.class);
        typeMap.put(Date.class, IncrementalDate.class);
        typeMap.put(java.sql.Date.class, IncrementalDate.class);
        typeMap.put(Timestamp.class, IncrementalDate.class);
        return typeMap;
    }

}
