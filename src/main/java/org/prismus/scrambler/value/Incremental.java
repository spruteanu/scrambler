package org.prismus.scrambler.value;

import org.prismus.scrambler.Value;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.Timestamp;
import java.util.*;

/**
 * Facade class that generates incremental values according to type rules: <><br/>
 * <li/>Numbers are incremented arithmetically
 * <li/>Dates are incremented using {@link Calendar} fields
 * <li/>Strings are generating using provided pattern
 * <p/>
 * Along with one type generation, it is possible to get incremental array accordingly
 *
 * @author Serge Pruteanu
 */
public class Incremental {
    private static Map<Class, Class<? extends Value>> propertyTypeMap = lookupPropertyTypeMap();

    //------------------------------------------------------------------------------------------------------------------
    // Number methods
    //------------------------------------------------------------------------------------------------------------------
    @SuppressWarnings({"unchecked"})
    public static <N extends Number> Value<N> of(N value) {
        return of((Class) value.getClass(), value, null);
    }

    @SuppressWarnings({"unchecked"})
    public static <N extends Number> Value<N> of(N value, N step) {
        return of((Class) value.getClass(), value, step);
    }

    @SuppressWarnings({"unchecked"})
    public static <N extends Number> Value<N> of(Class<N> clazzType, N defaultValue, N step) {
        if (propertyTypeMap.containsKey(clazzType)) {
            final Value value;
            if (clazzType.isArray()) {
                value = arrayOf(clazzType, defaultValue, step, null);
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

    @SuppressWarnings("unchecked")
    public static <N extends Number> Value arrayOf(N defaultValue, N step, Integer count) {
        return arrayOf(defaultValue.getClass(), defaultValue, step, count);
    }

    @SuppressWarnings({"unchecked"})
    public static Value arrayOf(Class clazzType, Object defaultValue, Object step, Integer count) {
        if (count != null && count < 0) {
            throw new IllegalArgumentException(String.format("Count should be a positive number: %s", count));
        }
        final Class<?> componentType = clazzType.isArray() ? clazzType.getComponentType() : clazzType;
        final Value value;
        if (componentType.isPrimitive()) {
            value = (Value) Util.createInstance(
                    propertyTypeMap.get(clazzType),
                    new Object[]{
                            clazzType.isInstance(defaultValue) ? defaultValue : null,
                            count,
                            (Value) Util.createInstance(
                                    propertyTypeMap.get(componentType),
                                    new Object[]{clazzType.isInstance(defaultValue) ? null : defaultValue, step},
                                    new Class[]{Util.primitiveWrapperMap.get(componentType), Util.primitiveWrapperMap.get(componentType)}
                            )},
                    new Class[]{clazzType, Integer.class, Object.class}
            );
        } else {
            value = new ValueArray(clazzType, count, (Value) Util.createInstance(
                    propertyTypeMap.get(componentType),
                    new Object[]{defaultValue, step},
                    new Class[]{componentType, componentType}
            ));
        }
        return value;
    }

    //------------------------------------------------------------------------------------------------------------------
    // Date methods
    //------------------------------------------------------------------------------------------------------------------
    public static IncrementalDate of(Date value) {
        return new IncrementalDate(value);
    }

    public static IncrementalDate of(Date value, Integer step) {
        return new IncrementalDate(value, step);
    }

    public static IncrementalDate of(Date value, Integer step, Integer calendarField) {
        return new IncrementalDate(value, step, calendarField);
    }

    public static IncrementalDate of(Map<Integer, Integer> calendarFieldStepMap) {
        return new IncrementalDate().incrementBy(calendarFieldStepMap);
    }

    //------------------------------------------------------------------------------------------------------------------
    // String methods
    //------------------------------------------------------------------------------------------------------------------
    public static IncrementalString of(String value) {
        return new IncrementalString(value);
    }

    public static ValueArray<String> of(Integer count, String value) {
        return new ValueArray<String>(String.class, count, new IncrementalString(value));
    }

    public static IncrementalString of(String value, String pattern) {
        return new IncrementalString(value, pattern);
    }

    public static ValueArray<String> of(Integer count, String value, String pattern) {
        return new ValueArray<String>(String.class, count, new IncrementalString(value, pattern));
    }

    public static IncrementalString of(String value, Integer index) {
        return new IncrementalString(value, index);
    }

    public static ValueArray<String> of(Integer count, String value, Integer index) {
        return new ValueArray<String>(String.class, count, new IncrementalString(value, index));
    }

    public static IncrementalString of(String value, String pattern, Integer index) {
        return new IncrementalString(value, pattern, index);
    }

    public static ValueArray<String> of(Integer count, String value, String pattern, Integer index) {
        return new ValueArray<String>(String.class, count, new IncrementalString(value, pattern, index));
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
