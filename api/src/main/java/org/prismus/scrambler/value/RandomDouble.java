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
class RandomDouble extends AbstractRandomRange<Double> implements DoubleArray {

    private final Random random;

    public RandomDouble() {
        this(null, null, null);
    }

    public RandomDouble(Double value) {
        this(value, null, null);
    }

    public RandomDouble(Double minimum, Double maximum) {
        this(null, minimum, maximum);
    }

    public RandomDouble(Double value, Double minimum, Double maximum) {
        super(value, minimum, maximum);
        usingDefaults(0.0, Double.MAX_VALUE);
        random = new Random();
    }

    @Override
    protected Double min(Double val1, Double val2) {
        return val1 != null ? val2 != null ? val1.compareTo(val2) > 0 ? val2 : val1 : null : null;
    }

    @Override
    protected Double max(Double val1, Double val2) {
        return val1 != null ? val2 != null ? val1.compareTo(val2) > 0 ? val1 : val2 : val1 : val2;
    }

    double nextValue() {
        final double result;
        if (minimum != null && maximum != null) {
            double interval = Math.abs(maximum - minimum);
            result = minimum + interval * random.nextDouble();
        } else {
            result = value != null ? random.nextDouble() * value : random.nextDouble() * Math.abs(random.nextInt());
        }
        return result;
    }

    @Override
    public Double get() {
        return value == null ? nextValue() : value;
    }

    @Override
    protected Double doNext() {
        return nextValue();
    }

    public void next(double[] values) {
        double next = nextValue();
        for (int i = 0; i < values.length; i++) {
            next = nextValue();
            values[i] = next;
        }
        setValue(next);
    }

}
