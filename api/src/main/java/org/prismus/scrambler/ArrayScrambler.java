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

import org.prismus.scrambler.value.*;

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
        Util.checkNullValue(self);
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
    public static <T> Data<T> randomArray(Object value) {
        return (Data<T>) randomArray(value.getClass(), value, null);
    }

    @SuppressWarnings("unchecked")
    public static <T> Data<T> randomArray(Object value, Integer count) {
        return (Data<T>) randomArray(value.getClass(), (Object) value, count);
    }

    @SuppressWarnings({"unchecked"})
    public static <T> Data<T> incrementArray(Class<T> self, Object defaultValue, Object step, Integer count) {
        Util.checkPositiveCount(count);
        final Class<?> componentType = self.isArray() ? self.getComponentType() : self;
        final Data data;
        if (componentType.isPrimitive()) {
            data = (Data) Util.createInstance(Types.incrementTypeMap.get(self), new Object[]{
                            self.isInstance(defaultValue) ? defaultValue : null, count,
                            (Data) Util.createInstance(
                                    Types.incrementTypeMap.get(componentType),
                                    new Object[]{self.isInstance(defaultValue) ? null : defaultValue, step},
                                    new Class[]{Types.primitiveWrapperMap.get(componentType), Types.primitiveWrapperMap.get(componentType)}
                            )}, new Class[]{self, Integer.class, Object.class}
            );
        } else {
            data = new ArrayData(componentType, count, (Data) Util.createInstance(
                    Types.incrementTypeMap.get(componentType),
                    new Object[]{componentType.isInstance(defaultValue) ? defaultValue : null, step}, new Class[]{componentType, componentType}
            ));
        }
        return data;
    }

    @SuppressWarnings({"unchecked"})
    public static <T> Data<T> randomArray(Class<T> self, Object defaultValue, Integer count) {
        final Class<?> componentType = self.isArray() ? self.getComponentType() : self;
        final Class<?> valueClassType = componentType.isPrimitive() ? Types.primitiveWrapperMap.get(componentType) : componentType;
        final Data dataType;
        if (defaultValue != null && !defaultValue.getClass().isArray()) {
            dataType = (Data) Util.createInstance(Types.randomTypeMap.get(valueClassType), new Object[]{defaultValue,}, new Class[]{valueClassType,});
        } else {
            dataType = (Data) Util.createInstance(Types.randomTypeMap.get(valueClassType), new Object[]{}, new Class[]{});
        }
        if (componentType.isPrimitive()) {
            final Class<? extends Data> arrayValueType = Types.primitivesArrayTypeMap.get(componentType);
            return (Data) Util.createInstance(arrayValueType, new Object[]{defaultValue, count, dataType}, new Class[]{self, Integer.class, Object.class});
        } else {
            return new ArrayData(componentType, count, dataType);
        }
    }

    @SuppressWarnings("unchecked")
    public static <T> Data<T> of(Class clazzType, Data val, Integer count) {
        if (clazzType.isPrimitive()) {
            final Class<? extends Data> arrayValueType = Types.primitivesArrayTypeMap.get(clazzType);
            return (Data) Util.createInstance(arrayValueType, new Object[]{null, count, val}, new Class[]{Types.arrayTypeMap.get(clazzType), Integer.class, Object.class});
        } else {
            return new ArrayData(clazzType, count, val);
        }
    }

    public static ArrayContainerData of(Data... datas) {
        return new ArrayContainerData(Arrays.asList(datas));
    }

    public static Data<Boolean> randomOf(final boolean[] values) {
        return new RandomElementData<Boolean>() {
            @Override
            protected Boolean doNext() {
                return values[random.nextInt(values.length)];
            }
        };
    }

    public static Data<Byte> randomOf(final byte[] values) {
        return new RandomElementData<Byte>() {
            @Override
            protected Byte doNext() {
                return values[random.nextInt(values.length)];
            }
        };
    }

    public static Data<Short> randomOf(final short[] values) {
        return new RandomElementData<Short>() {
            @Override
            protected Short doNext() {
                return values[random.nextInt(values.length)];
            }
        };
    }

    public static Data<Integer> randomOf(final int[] values) {
        return new RandomElementData<Integer>() {
            @Override
            protected Integer doNext() {
                return values[random.nextInt(values.length)];
            }
        };
    }

    public static Data<Long> randomOf(final long[] values) {
        return new RandomElementData<Long>() {
            @Override
            protected Long doNext() {
                return values[random.nextInt(values.length)];
            }
        };
    }

    public static Data<Float> randomOf(final float[] values) {
        return new RandomElementData<Float>() {
            @Override
            protected Float doNext() {
                return values[random.nextInt(values.length)];
            }
        };
    }

    public static Data<Double> randomOf(final double[] values) {
        return new RandomElementData<Double>() {
            @Override
            protected Double doNext() {
                return values[random.nextInt(values.length)];
            }
        };
    }

    public static <T> Data<T[]> combinationsOf(T... values) {
        return Combinations.of(values);
    }

    public static <T> Data<T[]> combinationsOf(Class<T> valueType, Data<T>... datas) {
        return Combinations.valuesOf(valueType, datas);
    }

    public static Data<boolean[]> combinationsOf(final boolean... values) {
        return Combinations.of(values);
    }

    public static Data<byte[]> combinationsOf(final byte... values) {
        return Combinations.of(values);
    }

    public static Data<short[]> combinationsOf(final short... values) {
        return Combinations.of(values);
    }

    public static Data<int[]> combinationsOf(final int... values) {
        return Combinations.of(values);
    }

    public static Data<long[]> combinationsOf(final long... values) {
        return Combinations.of(values);
    }

    public static Data<float[]> combinationsOf(final float... values) {
        return Combinations.of(values);
    }

    public static Data<double[]> combinationsOf(final double... values) {
        return Combinations.of(values);
    }
}
