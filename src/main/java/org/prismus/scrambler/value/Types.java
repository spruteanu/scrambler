package org.prismus.scrambler.value;

import org.prismus.scrambler.Value;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.Timestamp;
import java.util.*;

/**
 * @author Serge Pruteanu
 */
public class Types {
    public static final String NOT_SUPPORTED_RANGE_TYPE_MSG = "Not supported range method for provided class type: %s, range of [%s, %s]";
    public static Map<Class, Class<? extends Value>> randomTypeMap = lookupRandomTypeMap();
    public static Map<Class, Class<? extends Value>> incrementTypeMap = lookupIncrementTypeMap();
    public static Map<Class, Class> arrayTypeMap = new LinkedHashMap<Class, Class>() {{
        put(byte.class, byte[].class);
        put(short.class, short[].class);
        put(boolean.class, boolean[].class);
        put(double.class, double[].class);
        put(float.class, float[].class);
        put(int.class, int[].class);
        put(long.class, long[].class);
    }};
    public static Map<Class, Class<? extends Value>> primitivesArrayTypeMap = lookupPrimitiveArrayTypeMap();
    public static Map<Class, Class> primitiveWrapperMap = new LinkedHashMap<Class, Class>() {{
        put(byte.class, Byte.class);
        put(short.class, Short.class);
        put(boolean.class, Boolean.class);
        put(double.class, Double.class);
        put(float.class, Float.class);
        put(int.class, Integer.class);
        put(long.class, Long.class);
    }};

    public static boolean isSupportedRandomType(Class type) {
        return randomTypeMap.containsKey(type);
    }

    public static Set<Class> getSupportedRandomTypes() {
        return Collections.unmodifiableSet(randomTypeMap.keySet());
    }

    public static Set<Class> getSupportedIncrementTypes() {
        return Collections.unmodifiableSet(incrementTypeMap.keySet());
    }

    private static Map<Class, Class<? extends Value>> lookupRandomTypeMap() {
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

    private static Map<Class, Class<? extends Value>> lookupIncrementTypeMap() {
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

    private static Map<Class, Class<? extends Value>> lookupPrimitiveArrayTypeMap() {
        final Map<Class, Class<? extends Value>> typeMap = new LinkedHashMap<Class, Class<? extends Value>>();
        typeMap.put(byte.class, ByteValueArray.class);
        typeMap.put(short.class, ShortValueArray.class);
        typeMap.put(boolean.class, BooleanValueArray.class);
        typeMap.put(double.class, DoubleValueArray.class);
        typeMap.put(float.class, FloatValueArray.class);
        typeMap.put(int.class, IntValueArray.class);
        typeMap.put(long.class, LongValueArray.class);
        return typeMap;
    }

}
