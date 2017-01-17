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
public class IncrementalInteger extends ConstantData<Integer> implements IntArray {
    private static final int DEFAULT_STEP = 1;
    private int step;

    public IncrementalInteger() {
        this(null, DEFAULT_STEP);
    }

    public IncrementalInteger(Integer obj) {
        this(obj, DEFAULT_STEP);
    }

    public IncrementalInteger(Integer obj, Integer step) {
        super(obj);
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
        Integer obj = get();
        return obj != null ? nextValue(obj) : 0;
    }

    public void next(int[] array) {
        int start = object == null ? 0 : object;
        for (int i = 0; i < array.length; i++) {
            array[i] = start;
            start = nextValue(start);
        }
        setObject(start);
    }

}
