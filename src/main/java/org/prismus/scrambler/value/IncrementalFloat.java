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
class IncrementalFloat extends Constant<Float> implements FloatArray{
    private float step;
    private static final float DEFAULT_STEP = 1.0F;

    public IncrementalFloat() {
        this(null, DEFAULT_STEP);
    }

    public IncrementalFloat(Float value) {
        this(value, DEFAULT_STEP);
    }

    public IncrementalFloat(Float value, Float step) {
        super(value);
        this.step = step != null ? step : DEFAULT_STEP;
    }

    @Override
    protected Float doNext() {
        Float value = get();
        return value != null ? value + step : 0.0f;
    }

    public void next(float[] values) {
        float start = value != null ? value : 0.0f;
        for (int i = 0; i < values.length; i++) {
            values[i] = start;
            start = start + step;
        }
        setValue(start);
    }

}
