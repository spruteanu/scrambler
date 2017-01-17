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
public class RandomFloat extends AbstractRandomRange<Float> implements FloatArray {
    private final Random random;

    public RandomFloat() {
        this(null, null, null);
    }

    public RandomFloat(Float obj) {
        this(obj, null, null);
    }

    public RandomFloat(Float minimum, Float maximum) {
        this(null, minimum, maximum);
    }

    public RandomFloat(Float obj, Float minimum, Float maximum) {
        super(obj, minimum, maximum);
        usingDefaults(0F, Float.MAX_VALUE);
        random = new Random();
    }

    @Override
    protected Float min(Float val1, Float val2) {
        return val1 != null ? val2 != null ? val1.compareTo(val2) > 0 ? val2 : val1 : null : null;
    }

    @Override
    protected Float max(Float val1, Float val2) {
        return val1 != null ? val2 != null ? val1.compareTo(val2) > 0 ? val1 : val2 : val1 : val2;
    }

    float nextValue() {
        final float result;
        if (minimum != null && maximum != null) {
            float interval = Math.abs(maximum - minimum);
            result = minimum + interval * random.nextFloat();
        } else {
            result = object != null ? random.nextFloat() * object : random.nextFloat() * Math.abs(random.nextInt());
        }
        return result;
    }

    @Override
    public Float get() {
        return object == null ? nextValue() : object;
    }

    @Override
    protected Float doNext() {
        return nextValue();
    }

    public void next(float[] array) {
        float next = nextValue();
        for (int i = 0; i < array.length; i++) {
            next = nextValue();
            array[i] = next;
        }
        setObject(next);
    }

}
