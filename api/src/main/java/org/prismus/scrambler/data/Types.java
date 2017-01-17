/*
 * Data Scrambler, Data Generation API
 * Copyright (c) 2015, Sergiu Prutean. All rights reserved.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3.0 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library.
 */

package org.prismus.scrambler.data;

import org.prismus.scrambler.Data;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.Timestamp;
import java.util.*;

/**
 * @author Serge Pruteanu
 */
public class Types {
    public static final String NOT_SUPPORTED_RANGE_TYPE_MSG = "Not supported range method for provided class type: %s, range of [%s, %s]";
    public static Map<Class, Class<? extends Data>> randomTypeMap = lookupRandomTypeMap();
    public static Map<Class, Class<? extends Data>> incrementTypeMap = lookupIncrementTypeMap();
    public static Map<Class, Class> arrayTypeMap = new LinkedHashMap<Class, Class>() {{
        put(byte.class, byte[].class);
        put(short.class, short[].class);
        put(boolean.class, boolean[].class);
        put(double.class, double[].class);
        put(float.class, float[].class);
        put(int.class, int[].class);
        put(long.class, long[].class);
    }};
    public static Map<Class, Class<? extends Data>> primitivesArrayTypeMap = lookupPrimitiveArrayTypeMap();
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

    private static Map<Class, Class<? extends Data>> lookupRandomTypeMap() {
        final Map<Class, Class<? extends Data>> typeMap = new LinkedHashMap<Class, Class<? extends Data>>();
        typeMap.put(Byte.TYPE, RandomByte.class);
        typeMap.put(Byte.class, RandomByte.class);
        typeMap.put(byte[].class, ByteDataArray.class);

        typeMap.put(Short.TYPE, RandomShort.class);
        typeMap.put(Short.class, RandomShort.class);
        typeMap.put(short[].class, ShortDataArray.class);

        typeMap.put(Boolean.TYPE, RandomBoolean.class);
        typeMap.put(Boolean.class, RandomBoolean.class);
        typeMap.put(boolean[].class, BooleanDataArray.class);

        typeMap.put(Double.TYPE, RandomDouble.class);
        typeMap.put(Double.class, RandomDouble.class);
        typeMap.put(double[].class, DoubleDataArray.class);

        typeMap.put(Float.TYPE, RandomFloat.class);
        typeMap.put(Float.class, RandomFloat.class);
        typeMap.put(float[].class, FloatDataArray.class);

        typeMap.put(Integer.TYPE, RandomInteger.class);
        typeMap.put(Integer.class, RandomInteger.class);
        typeMap.put(int[].class, IntDataArray.class);

        typeMap.put(Long.TYPE, RandomLong.class);
        typeMap.put(Long.class, RandomLong.class);
        typeMap.put(long[].class, LongDataArray.class);

        typeMap.put(BigInteger.class, RandomBigInteger.class);
        typeMap.put(BigDecimal.class, RandomBigDecimal.class);

        typeMap.put(String.class, RandomString.class);
        typeMap.put(Date.class, RandomDate.class);
        typeMap.put(java.sql.Date.class, RandomDate.class);
        typeMap.put(Timestamp.class, RandomDate.class);
        return typeMap;
    }

    private static Map<Class, Class<? extends Data>> lookupIncrementTypeMap() {
        final Map<Class, Class<? extends Data>> typeMap = new LinkedHashMap<Class, Class<? extends Data>>();
        typeMap.put(Byte.TYPE, IncrementalByte.class);
        typeMap.put(Byte.class, IncrementalByte.class);
        typeMap.put(byte[].class, ByteDataArray.class);

        typeMap.put(Short.TYPE, IncrementalShort.class);
        typeMap.put(Short.class, IncrementalShort.class);
        typeMap.put(short[].class, ShortDataArray.class);

        typeMap.put(Double.TYPE, IncrementalDouble.class);
        typeMap.put(Double.class, IncrementalDouble.class);
        typeMap.put(double[].class, DoubleDataArray.class);

        typeMap.put(Float.TYPE, IncrementalFloat.class);
        typeMap.put(Float.class, IncrementalFloat.class);
        typeMap.put(float[].class, FloatDataArray.class);

        typeMap.put(Integer.TYPE, IncrementalInteger.class);
        typeMap.put(Integer.class, IncrementalInteger.class);
        typeMap.put(int[].class, IntDataArray.class);

        typeMap.put(Long.TYPE, IncrementalLong.class);
        typeMap.put(Long.class, IncrementalLong.class);
        typeMap.put(long[].class, LongDataArray.class);

        typeMap.put(BigInteger.class, IncrementalBigInteger.class);
        typeMap.put(BigDecimal.class, IncrementalBigDecimal.class);

        typeMap.put(String.class, IncrementalString.class);
        typeMap.put(Date.class, IncrementalDate.class);
        typeMap.put(java.sql.Date.class, IncrementalDate.class);
        typeMap.put(Timestamp.class, IncrementalDate.class);
        return typeMap;
    }

    private static Map<Class, Class<? extends Data>> lookupPrimitiveArrayTypeMap() {
        final Map<Class, Class<? extends Data>> typeMap = new LinkedHashMap<Class, Class<? extends Data>>();
        typeMap.put(byte.class, ByteDataArray.class);
        typeMap.put(short.class, ShortDataArray.class);
        typeMap.put(boolean.class, BooleanDataArray.class);
        typeMap.put(double.class, DoubleDataArray.class);
        typeMap.put(float.class, FloatDataArray.class);
        typeMap.put(int.class, IntDataArray.class);
        typeMap.put(long.class, LongDataArray.class);
        return typeMap;
    }

}
