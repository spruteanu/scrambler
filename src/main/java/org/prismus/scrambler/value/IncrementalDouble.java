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

/**
 * @author Serge Pruteanu
 */
class IncrementalDouble extends Constant<Double> implements DoubleArray {
    private static final double DEFAULT_STEP = 1.0;
    private double step;

    public IncrementalDouble() {
        this(null, DEFAULT_STEP);
    }

    public IncrementalDouble(Double value) {
        this(value, DEFAULT_STEP);
    }

    public IncrementalDouble(Double value, Double step) {
        super(value);
        this.step = step != null ? step : DEFAULT_STEP;
    }

    public void setStep(double step) {
        this.step = step;
    }

    @Override
    public Double next() {
        Double value = get();
        value = value != null ? value + step : 0.0;
        setValue(value);
        return value;
    }

    public void next(double[] values) {
        double start = value != null ? value : 0.0;
        for (int i = 0; i < values.length; i++) {
            values[i] = start;
            start = start + step;
        }
        setValue(start);
    }

}
