/*
 * Data Scrambler, Data Generation API
 * Copyright (c) 2015, Sergiu Prutean. All rights reserved.
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
 *
 */

package org.prismus.scrambler.value;

import org.prismus.scrambler.Data;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

/**
 * Data that allows to generate combination arrays and list from provided source
 * Combinations are generated using Johnson Trotter algorithm, adopted from
 * <a href="http://introcs.cs.princeton.edu/java/23recursion/JohnsonTrotter.java.html">Princeton Educational courses</a>
 *
 * @author Serge Pruteanu
 */
public abstract class Combinations<T> extends ConstantData<T> {
    private int[] permutations;
    private int[] inverse;
    private int[] directions;
    private int[] iSwap;
    private int index;
    protected int count;

    Combinations(int count) {
        if (count < 2) {
            throw new IllegalArgumentException(String.format("Combinations can be generated for count more than 1; provided count: %d", count));
        }
        this.count = count;
        permutations = new int[count];
        inverse = new int[count];
        directions = new int[count];
        iSwap = new int[count];
        initialize();
    }

    protected void initialize() {
        for (int i = 0; i < count; i++) {
            directions[i] = -1;
            permutations[i] = i;
            inverse[i] = i;
            iSwap[i] = i;
        }
        index = count;
    }

    protected int[] nextCombination() {
        do {
            if (index == count) {
                index--;
                return permutations;
            } else if (iSwap[index] > 0) {
                int swapPermutation = permutations[inverse[index] + directions[index]];
                permutations[inverse[index]] = swapPermutation;
                permutations[inverse[index] + directions[index]] = index;
                inverse[swapPermutation] = inverse[index];
                inverse[index] = inverse[index] + directions[index];
                iSwap[index]--;
                index = count;
            } else {
                iSwap[index] = index;
                directions[index] = -directions[index];
                index--;
            }
        } while (index > 0);
        initialize();
        return permutations;
    }

    @SuppressWarnings("unchecked")
    @Override
    protected final T doNext() {
        final int[] combinations = nextCombination();
        final T result = create();
        for (int i = 0; i < count; i++) {
            populate(result, i, combinations[i]);
        }
        return result;
    }

    abstract T create();

    abstract void populate(T result, int i, int permIdx);

    public static <T> Data<T[]> of(T... original) {
        return new ArrayCombinations<T>(original);
    }

    public static <T> Data<T[]> valuesOf(Class<T> valueType, Data<T>... original) {
        return new ValueArrayCombinations<T>(original, valueType);
    }

    public static <T> Data<List<T>> of(List<T> original) {
        return new ListCombinations<T>(original, original.size());
    }

    public static <T> Data<List<T>> valuesOf(List<Data<T>> original) {
        return new ValueListCombinations<T>(original, original.size());
    }

    public static Data<boolean[]> of(boolean... original) {
        return new BooleanCombinations(original);
    }

    public static Data<byte[]> of(byte... original) {
        return new ByteCombinations(original);
    }

    public static Data<short[]> of(short... original) {
        return new ShortCombinations(original);
    }

    public static Data<int[]> of(int... original) {
        return new IntCombinations(original);
    }

    public static Data<long[]> of(long... original) {
        return new LongCombinations(original);
    }

    public static Data<float[]> of(float... original) {
        return new FloatCombinations(original);
    }

    public static Data<double[]> of(double... original) {
        return new DoubleCombinations(original);
    }

    static class ArrayCombinations<T> extends Combinations<T[]> {
        private T[] original;
        private Class<T> valueType;

        @SuppressWarnings("unchecked")
        ArrayCombinations(T[] original) {
            super(original.length);
            this.original = original;
            this.valueType = (Class<T>) original.getClass().getComponentType();
        }

        @SuppressWarnings("unchecked")
        T[] create() {
            return (T[]) Array.newInstance(valueType, count);
        }

        void populate(T[] result, int i, int permIdx) {
            result[i] = original[permIdx];
        }
    }

    static class ValueArrayCombinations<T> extends Combinations<T[]> {
        private Data<T>[] original;
        private Class<T> valueType;

        ValueArrayCombinations(Data<T>[] original, Class<T> valueType) {
            super(original.length);
            this.original = original;
            this.valueType = valueType;
        }

        @SuppressWarnings("unchecked")
        T[] create() {
            return (T[]) Array.newInstance(valueType, count);
        }

        void populate(T[] result, int i, int permIdx) {
            result[i] = original[permIdx].next();
        }
    }

    static class ListCombinations<T> extends Combinations<List<T>> {
        private List<T> original;

        @SuppressWarnings("unchecked")
        ListCombinations(List<T> original, int count) {
            super(count);
            this.original = original;
        }

        @SuppressWarnings("unchecked")
        List<T> create() {
            return new ArrayList<T>(count);
        }

        void populate(List<T> result, int i, int permIdx) {
            if (result.size() == 0) {
                for (int j = 0; j < count; j++) {
                    result.add(null);
                }
            }
            result.set(i, original.get(permIdx));
        }
    }

    static class ValueListCombinations<T> extends Combinations<List<T>> {
        private List<Data<T>> original;

        @SuppressWarnings("unchecked")
        ValueListCombinations(List<Data<T>> original, int count) {
            super(count);
            this.original = original;
        }

        @SuppressWarnings("unchecked")
        List<T> create() {
            return new ArrayList<T>(count);
        }

        void populate(List<T> result, int i, int permIdx) {
            if (result.size() == 0) {
                for (int j = 0; j < count; j++) {
                    result.add(null);
                }
            }
            result.set(i, original.get(permIdx).next());
        }
    }

    static class BooleanCombinations extends Combinations<boolean[]> {
        private boolean[] original;

        @SuppressWarnings("unchecked")
        BooleanCombinations(boolean[] original) {
            super(original.length);
            this.original = original;
        }

        @SuppressWarnings("unchecked")
        boolean[] create() {
            return new boolean[count];
        }

        void populate(boolean[] result, int i, int permIdx) {
            result[i] = original[permIdx];
        }
    }

    static class ByteCombinations extends Combinations<byte[]> {
        private byte[] original;

        @SuppressWarnings("unchecked")
        ByteCombinations(byte[] original) {
            super(original.length);
            this.original = original;
        }

        @SuppressWarnings("unchecked")
        byte[] create() {
            return new byte[count];
        }

        void populate(byte[] result, int i, int permIdx) {
            result[i] = original[permIdx];
        }
    }

    static class ShortCombinations extends Combinations<short[]> {
        private short[] original;

        @SuppressWarnings("unchecked")
        ShortCombinations(short[] original) {
            super(original.length);
            this.original = original;
        }

        @SuppressWarnings("unchecked")
        short[] create() {
            return new short[count];
        }

        void populate(short[] result, int i, int permIdx) {
            result[i] = original[permIdx];
        }
    }

    static class IntCombinations extends Combinations<int[]> {
        private int[] original;

        @SuppressWarnings("unchecked")
        IntCombinations(int[] original) {
            super(original.length);
            this.original = original;
        }

        @SuppressWarnings("unchecked")
        int[] create() {
            return new int[count];
        }

        void populate(int[] result, int i, int permIdx) {
            result[i] = original[permIdx];
        }
    }

    static class LongCombinations extends Combinations<long[]> {
        private long[] original;

        @SuppressWarnings("unchecked")
        LongCombinations(long[] original) {
            super(original.length);
            this.original = original;
        }

        @SuppressWarnings("unchecked")
        long[] create() {
            return new long[count];
        }

        void populate(long[] result, int i, int permIdx) {
            result[i] = original[permIdx];
        }
    }

    static class FloatCombinations extends Combinations<float[]> {
        private float[] original;

        @SuppressWarnings("unchecked")
        FloatCombinations(float[] original) {
            super(original.length);
            this.original = original;
        }

        @SuppressWarnings("unchecked")
        float[] create() {
            return new float[count];
        }

        void populate(float[] result, int i, int permIdx) {
            result[i] = original[permIdx];
        }
    }

    static class DoubleCombinations extends Combinations<double[]> {
        private double[] original;

        @SuppressWarnings("unchecked")
        DoubleCombinations(double[] original) {
            super(original.length);
            this.original = original;
        }

        @SuppressWarnings("unchecked")
        double[] create() {
            return new double[count];
        }

        void populate(double[] result, int i, int permIdx) {
            result[i] = original[permIdx];
        }
    }

}
