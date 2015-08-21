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

import org.prismus.scrambler.Value;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

/**
 * todo: add description, implement me
 * Combinations are generated using Johnson Trotter algorithm, adopted algorithm from
 * <a href="http://introcs.cs.princeton.edu/java/23recursion/JohnsonTrotter.java.html">Princeton Educational cources</a>
 *
 * @author Serge Pruteanu
 */
public abstract class CombinationValue<T> extends Constant<T> {
    private int[] permutations;
    private int[] inverse;
    private int[] directions;
    private int[] iSwap;
    private int index;
    protected int count;

    CombinationValue(int count) {
        this.count = count;
        permutations = new int[count];
        inverse = new int[count];
        directions = new int[count];
        iSwap = new int[count];
        initialize();
    }

    void initialize() {
        for (int i = 0; i < count; i++) {
            directions[i] = -1;
            permutations[i] = i;
            inverse[i] = i;
            iSwap[i] = i;
        }
        index = count;
    }

    int[] nextCombination() {
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
        final T result = (T) create();
        for (int i = 0; i < count; i++) {
            populate(result, i, combinations[i]);
        }
        return result;
    }

    abstract Object create();

    abstract void populate(T result, int i, int permIdx);

    static class ArrayCombinationValue<T> extends CombinationValue<T[]> {
        private T[] original;
        private Class<T> valueType;

        @SuppressWarnings("unchecked")
        ArrayCombinationValue(T[] original) {
            super(original.length);
            this.original = original;
            this.valueType = (Class<T>) original.getClass().getComponentType();
        }

        Object create() {
            return Array.newInstance(valueType, count);
        }

        void populate(T[] result, int i, int permIdx) {
            result[i] = original[permIdx];
        }

    }

    static class ValueCombinations<T> extends CombinationValue<Value<T>[]> {
        private Value<T>[] original;
        private Class<T> valueType;

        ValueCombinations(Value<T>[] original, Class<T> valueType) {
            super(original.length);
            this.original = original;
            this.valueType = valueType;
        }

        Object create() {
            return Array.newInstance(valueType, count);
        }

        void populate(T[] result, int i, int permIdx) {
            result[i] = original[permIdx].next();
        }

    }

    static class ListCombinationValue<T> extends CombinationValue<List<T>> {
        private List<T> original;

        @SuppressWarnings("unchecked")
        ListCombinationValue(List<T> original, int count) {
            super(count);
            this.original = original;
        }

        @SuppressWarnings("unchecked")
        List<T> create() {
            return new ArrayList<T>(count);
        }

        void populate(List<T> result, int i, int permIdx) {
            result.set(i, original.get(permIdx));
        }

    }

}
