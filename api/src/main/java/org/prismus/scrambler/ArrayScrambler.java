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

package org.prismus.scrambler;

import org.prismus.scrambler.data.*;

import java.util.Arrays;

/**
 * Facade class that exposes generation of arrays in various forms
 *
 * @author Serge Pruteanu
 */
public class ArrayScrambler {
    public static <T> Data<T> arrayOf(Class<T> self, Data val) {
        return of(self, val, null);
    }

    public static <T> Data<T> arrayOf(Class<T> self, Data val, Integer count) {
        return of(self, val, count);
    }

    public static <T> Data<T> arrayOf(Object self, Data data) {
        return arrayOf(self, data, null);
    }

    @SuppressWarnings("unchecked")
    public static <T> Data<T> arrayOf(Object self, Data data, Integer count) {
        Util.checkNull(self);
        final Class<?> selfClass = self.getClass();
        if (!selfClass.isArray()) {
            throw new IllegalArgumentException(String.format("An array instance must be provided; provided: %s", self));
        }
        final Class<?> componentType = selfClass.getComponentType();
        if (componentType.isPrimitive()) {
            return (Data) Util.createInstance(Types.primitivesArrayTypeMap.get(componentType),
                    new Object[]{self, count, data}, new Class[]{selfClass, Integer.class, Object.class}
            );
        } else {
            return new ArrayData((T[]) self, count, data);
        }
    }

    @SuppressWarnings("unchecked")
    public static <T> Data<T> incrementArray(Object self, Object step, Integer count) {
        return incrementArray((Class<T>) self.getClass(), self, step, count);
    }

    public static <T> Data<T> randomOf(T[] array) {
        return new ArrayRandomElement<T>(array);
    }

    @SuppressWarnings("unchecked")
    public static <T> Data<T> randomArray(Object data) {
        return (Data<T>) randomArray(data.getClass(), data, null);
    }

    @SuppressWarnings("unchecked")
    public static <T> Data<T> randomArray(Object data, Integer count) {
        return (Data<T>) randomArray(data.getClass(), (Object) data, count);
    }

    @SuppressWarnings({"unchecked"})
    public static <T> Data<T> incrementArray(Class<T> self, Object defaultData, Object step, Integer count) {
        Util.checkPositiveCount(count);
        final Class<?> componentType = self.isArray() ? self.getComponentType() : self;
        final Data data;
        if (componentType.isPrimitive()) {
            data = (Data) Util.createInstance(Types.incrementTypeMap.get(self), new Object[]{
                            self.isInstance(defaultData) ? defaultData : null, count,
                            (Data) Util.createInstance(
                                    Types.incrementTypeMap.get(componentType),
                                    new Object[]{self.isInstance(defaultData) ? null : defaultData, step},
                                    new Class[]{Types.primitiveWrapperMap.get(componentType), Types.primitiveWrapperMap.get(componentType)}
                            )}, new Class[]{self, Integer.class, Object.class}
            );
        } else {
            data = new ArrayData(componentType, count, (Data) Util.createInstance(
                    Types.incrementTypeMap.get(componentType),
                    new Object[]{componentType.isInstance(defaultData) ? defaultData : null, step}, new Class[]{componentType, componentType}
            ));
        }
        return data;
    }

    @SuppressWarnings({"unchecked"})
    public static <T> Data<T> randomArray(Class<T> self, Object defaultData, Integer count) {
        final Class<?> componentType = self.isArray() ? self.getComponentType() : self;
        final Class<?> dataType = componentType.isPrimitive() ? Types.primitiveWrapperMap.get(componentType) : componentType;
        final Data data;
        if (defaultData != null && !defaultData.getClass().isArray()) {
            data = (Data) Util.createInstance(Types.randomTypeMap.get(dataType), new Object[]{defaultData,}, new Class[]{dataType,});
        } else {
            data = (Data) Util.createInstance(Types.randomTypeMap.get(dataType), new Object[]{}, new Class[]{});
        }
        if (componentType.isPrimitive()) {
            final Class<? extends Data> arrayDataType = Types.primitivesArrayTypeMap.get(componentType);
            return (Data) Util.createInstance(arrayDataType, new Object[]{defaultData, count, data}, new Class[]{self, Integer.class, Object.class});
        } else {
            return new ArrayData(componentType, count, data);
        }
    }

    @SuppressWarnings("unchecked")
    public static <T> Data<T> of(Class clazzType, Data val, Integer count) {
        if (clazzType.isPrimitive()) {
            final Class<? extends Data> arrayDataType = Types.primitivesArrayTypeMap.get(clazzType);
            return (Data) Util.createInstance(arrayDataType, new Object[]{null, count, val}, new Class[]{Types.arrayTypeMap.get(clazzType), Integer.class, Object.class});
        } else {
            return new ArrayData(clazzType, count, val);
        }
    }

    public static ArrayContainerData of(Data... datas) {
        return new ArrayContainerData(Arrays.asList(datas));
    }

    public static Data<Boolean> randomOf(final boolean[] dataArray) {
        return new RandomElementData<Boolean>() {
            @Override
            protected Boolean doNext() {
                return dataArray[random.nextInt(dataArray.length)];
            }
        };
    }

    public static Data<Byte> randomOf(final byte[] dataArray) {
        return new RandomElementData<Byte>() {
            @Override
            protected Byte doNext() {
                return dataArray[random.nextInt(dataArray.length)];
            }
        };
    }

    public static Data<Short> randomOf(final short[] dataArray) {
        return new RandomElementData<Short>() {
            @Override
            protected Short doNext() {
                return dataArray[random.nextInt(dataArray.length)];
            }
        };
    }

    public static Data<Integer> randomOf(final int[] dataArray) {
        return new RandomElementData<Integer>() {
            @Override
            protected Integer doNext() {
                return dataArray[random.nextInt(dataArray.length)];
            }
        };
    }

    public static Data<Long> randomOf(final long[] dataArray) {
        return new RandomElementData<Long>() {
            @Override
            protected Long doNext() {
                return dataArray[random.nextInt(dataArray.length)];
            }
        };
    }

    public static Data<Float> randomOf(final float[] dataArray) {
        return new RandomElementData<Float>() {
            @Override
            protected Float doNext() {
                return dataArray[random.nextInt(dataArray.length)];
            }
        };
    }

    public static Data<Double> randomOf(final double[] dataArray) {
        return new RandomElementData<Double>() {
            @Override
            protected Double doNext() {
                return dataArray[random.nextInt(dataArray.length)];
            }
        };
    }

    public static <T> Data<T[]> combinationsOf(T... data) {
        return Combinations.of(data);
    }

    public static <T> Data<T[]> combinationsOf(Class<T> dataType, Data<T>... data) {
        return Combinations.dataOf(dataType, data);
    }

    public static Data<boolean[]> combinationsOf(final boolean... data) {
        return Combinations.of(data);
    }

    public static Data<byte[]> combinationsOf(final byte... data) {
        return Combinations.of(data);
    }

    public static Data<short[]> combinationsOf(final short... data) {
        return Combinations.of(data);
    }

    public static Data<int[]> combinationsOf(final int... data) {
        return Combinations.of(data);
    }

    public static Data<long[]> combinationsOf(final long... data) {
        return Combinations.of(data);
    }

    public static Data<float[]> combinationsOf(final float... data) {
        return Combinations.of(data);
    }

    public static Data<double[]> combinationsOf(final double... data) {
        return Combinations.of(data);
    }
}
