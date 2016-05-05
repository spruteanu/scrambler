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

package org.prismus.scrambler.value;

import java.util.Random;

/**
 * @author Serge Pruteanu
 */
public class RandomInteger extends AbstractRandomRange<Integer> implements IntArray {

    private final Random random;

    public RandomInteger() {
        this(null, null, null);
    }

    public RandomInteger(Integer value) {
        this(value, null, null);
    }

    public RandomInteger(Integer minimum, Integer maximum) {
        this(null, minimum, maximum);
    }

    public RandomInteger(Integer value, Integer minimum, Integer maximum) {
        super(value, minimum, maximum);
        usingDefaults(0, Integer.MAX_VALUE);
        random = new Random();
    }

    @Override
    protected Integer min(Integer val1, Integer val2) {
        return val1 != null ? val2 != null ? val1.compareTo(val2) > 0 ? val2 : val1 : null : null;
    }

    @Override
    protected Integer max(Integer val1, Integer val2) {
        return val1 != null ? val2 != null ? val1.compareTo(val2) > 0 ? val1 : val2 : val1 : val2;
    }

    int nextValue(int previousValue) {
        final int result;
        if (minimum != null && maximum != null) {
            result = minimum + random.nextInt(Math.abs(maximum - minimum));
        } else {
            result = random.nextInt(Math.abs(previousValue) + 1);
        }
        return result;
    }

    @Override
    public Integer get() {
        return value == null ? nextValue(random.nextInt()) : value;
    }

    @Override
    protected Integer doNext() {
        return nextValue(value == null ? random.nextInt() : value);
    }

    public void next(int[] values) {
        int start = nextValue(value == null ? random.nextInt() : value);
        for (int i = 0; i < values.length; i++) {
            final int next = nextValue(start);
            values[i] = next;
            start = next;
        }
        setValue(start);
    }

}
