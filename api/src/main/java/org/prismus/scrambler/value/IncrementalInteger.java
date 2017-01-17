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
public class IncrementalInteger extends ConstantData<Integer> implements IntArray {
    private static final int DEFAULT_STEP = 1;
    private int step;

    public IncrementalInteger() {
        this(null, DEFAULT_STEP);
    }

    public IncrementalInteger(Integer value) {
        this(value, DEFAULT_STEP);
    }

    public IncrementalInteger(Integer value, Integer step) {
        super(value);
        this.step = step != null ? step : DEFAULT_STEP;
    }

    public void setStep(int step) {
        this.step = step;
    }

    int nextValue(int start) {
        return start + step;
    }

    @Override
    protected Integer doNext() {
        Integer value = get();
        return value != null ? nextValue(value) : 0;
    }

    public void next(int[] values) {
        int start = value == null ? 0 : value;
        for (int i = 0; i < values.length; i++) {
            values[i] = start;
            start = nextValue(start);
        }
        setValue(start);
    }

}
