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
public class RandomShort extends AbstractRandomRange<Short> implements ShortArray {

    private final java.util.Random random;

    public RandomShort() {
        this(null, null, null);
    }

    public RandomShort(Short value) {
        this(value, null, null);
    }

    public RandomShort(Short minimum, Short maximum) {
        this(null, minimum, maximum);
    }

    public RandomShort(Short value, Short minimum, Short maximum) {
        super(value, minimum, maximum);
        usingDefaults(Integer.valueOf(0).shortValue(), Short.MAX_VALUE);
        random = new Random();
    }

    @Override
    protected Short min(Short val1, Short val2) {
        return val1 != null ? val2 != null ? val1.compareTo(val2) > 0 ? val2 : val1 : null : null;
    }

    @Override
    protected Short max(Short val1, Short val2) {
        return val1 != null ? val2 != null ? val1.compareTo(val2) > 0 ? val1 : val2 : val1 : val2;
    }


    short nextValue() {
        final short result;
        if (minimum != null && maximum != null) {
            result = Integer.valueOf(minimum + random.nextInt(Math.abs(maximum - minimum))).shortValue();
        } else {
            result = Integer.valueOf(value != null ? random.nextInt(Math.abs(value) + 1) : random.nextInt()).shortValue();
        }
        return result;
    }

    @Override
    public Short get() {
        return value == null ? nextValue() : value;
    }

    @Override
    protected Short doNext() {
        return nextValue();
    }

    public void next(short[] values) {
        short next = nextValue();
        for (int i = 0; i < values.length; i++) {
            next = nextValue();
            values[i] = next;
        }
        setValue(next);
    }

}
