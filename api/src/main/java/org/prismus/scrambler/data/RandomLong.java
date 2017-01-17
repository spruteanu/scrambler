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

import java.util.Random;

/**
 * @author Serge Pruteanu
 */
public class RandomLong extends AbstractRandomRange<Long> implements LongArray {

    private final Random random;

    public RandomLong() {
        this(null, null, null);
    }

    public RandomLong(Long obj) {
        this(obj, null, null);
    }

    public RandomLong(Long minimum, Long maximum) {
        this(null, minimum, maximum);
    }

    public RandomLong(Long obj, Long minimum, Long maximum) {
        super(obj, minimum, maximum);
        usingDefaults(0L, Long.MAX_VALUE);
        random = new Random();
    }

    @Override
    protected Long min(Long val1, Long val2) {
        return val1 != null ? val2 != null ? val1.compareTo(val2) > 0 ? val2 : val1 : null : null;
    }

    @Override
    protected Long max(Long val1, Long val2) {
        return val1 != null ? val2 != null ? val1.compareTo(val2) > 0 ? val1 : val2 : val1 : val2;
    }

    long nextValue() {
        if (minimum != null && maximum != null) {
            final long interval = Math.abs(maximum - minimum);
            object = minimum + Math.abs(random.nextLong()) % interval;
        } else {
            object = random.nextLong();
        }
        return object;
    }

    @Override
    public Long get() {
        return object == null ? nextValue() : object;
    }

    @Override
    protected Long doNext() {
        return nextValue();
    }

    public void next(long[] array) {
        long next = 0;
        for (int i = 0; i < array.length; i++) {
            next = nextValue();
            array[i] = next;
        }
        setObject(next);
    }

}
