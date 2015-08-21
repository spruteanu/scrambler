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

import java.lang.reflect.Array;

/**
 * todo: add description, implement me
 * Combinations are generated using JohnsonTrotter, adopted algorithm from
 *
 * @author Serge Pruteanu
 * @see <a href="http://introcs.cs.princeton.edu/java/23recursion/JohnsonTrotter.java.html">Princeton Educational cources</a>
 */
public class CombinationValue<T> extends Constant<T[]> {
    private T[] original;

    private final int[] permutations;
    private final int[] inverse;
    private final int[] direction;
    private int index;

    private Integer count;
    private Class<T> valueType;

    public CombinationValue(T[] value) {
        super(value);
        original = value;

        final int count = value.length;
        permutations = new int[count];
        inverse = new int[count];
        direction = new int[count];

        for (int i = 0; i < count; i++) {
            direction[i] = -1;
            permutations[i] = i;
            inverse[i] = i;
        }
    }

    void nextCombination() {
        if (index >= permutations.length) {
            displayCombination(permutations); // combination is ready
            return;
        }

        combination(index + 1, permutations, inverse, direction);
        for (int i = 0; i <= index - 1; i++) {
            int z = permutations[inverse[index] + direction[index]];
            permutations[inverse[index]] = z;
            permutations[inverse[index] + direction[index]] = index;
            inverse[z] = inverse[index];
            inverse[index] = inverse[index] + direction[index];

            combination(index + 1, permutations, inverse, direction);
        }
        direction[index] = -direction[index];
    }

    @SuppressWarnings("unchecked")
    @Override
    protected T[] doNext() {
        nextCombination();
        final T[] result = (T[]) Array.newInstance(valueType, count);
        for (int i = 0; i < result.length; i++) {
            result[i] = original[permutations[i]];
        }
        return result;
    }

    static void combination(int number) {
        int[] permutations = new int[number];
        int[] inverse = new int[number];
        int[] direction = new int[number];
        for (int i = 0; i < number; i++) {
            direction[i] = -1;
            permutations[i] = i;
            inverse[i] = i;
        }
        combination(0, permutations, inverse, direction);
    }

    private static void displayCombination(int[] permutations) {
        for (int index : permutations) {
            System.out.printf(" %d", index);
        }
        System.out.println();
    }

    static void combination(int index, int[] permutations, int[] inverse, int[] direction) {
        if (index >= permutations.length) {
            displayCombination(permutations);
            return;
        }
        combination(index + 1, permutations, inverse, direction);
        for (int i = 0; i <= index - 1; i++) {
//             System.out.printf("   (%d %d)\n", inverse[index], inverse[index] + direction[index]);
            int z = permutations[inverse[index] + direction[index]];
            permutations[inverse[index]] = z;
            permutations[inverse[index] + direction[index]] = index;
            inverse[z] = inverse[index];
            inverse[index] = inverse[index] + direction[index];

            combination(index + 1, permutations, inverse, direction);
        }
        direction[index] = -direction[index];
    }

    public static void main(String[] args) {
        combination(5);
    }

}
