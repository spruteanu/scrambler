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

/**
 * @author Serge Pruteanu
 */
public class IncrementalFloat extends ConstantData<Float> implements FloatArray{
    private float step;
    private static final float DEFAULT_STEP = 1.0F;

    public IncrementalFloat() {
        this(null, DEFAULT_STEP);
    }

    public IncrementalFloat(Float obj) {
        this(obj, DEFAULT_STEP);
    }

    public IncrementalFloat(Float obj, Float step) {
        super(obj);
        this.step = step != null ? step : DEFAULT_STEP;
    }

    @Override
    protected Float doNext() {
        Float obj = get();
        return obj != null ? obj + step : 0.0f;
    }

    public void next(float[] array) {
        float start = object != null ? object : 0.0f;
        for (int i = 0; i < array.length; i++) {
            array[i] = start;
            start = start + step;
        }
        setObject(start);
    }

}
