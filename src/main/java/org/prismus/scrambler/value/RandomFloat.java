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
class RandomFloat extends AbstractRandomRange<Float> implements FloatArray {
    private final Random random;

    public RandomFloat() {
        this(null, null, null);
    }

    public RandomFloat(Float value) {
        this(value, null, null);
    }

    public RandomFloat(Float minimum, Float maximum) {
        this(null, minimum, maximum);
    }

    public RandomFloat(Float value, Float minimum, Float maximum) {
        super(value, minimum, maximum);
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
            result = value != null ? random.nextFloat() * value : random.nextFloat() * Math.abs(random.nextInt());
        }
        return result;
    }

    @Override
    public Float get() {
        return value == null ? nextValue() : value;
    }

    @Override
    public Float next() {
        final Float result = nextValue();
        setValue(result);
        return result;
    }

    public void next(float[] values) {
        float next = nextValue();
        for (int i = 0; i < values.length; i++) {
            next = nextValue();
            values[i] = next;
        }
        setValue(next);
    }

}
