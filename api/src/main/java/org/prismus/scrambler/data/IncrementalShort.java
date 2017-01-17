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
public class IncrementalShort extends ConstantData<Short> implements ShortArray {
    private static final short DEFAULT_STEP = Integer.valueOf(1).shortValue();
    private short step;

    public IncrementalShort() {
        this(null, DEFAULT_STEP);
    }

    public IncrementalShort(Short obj) {
        this(obj, DEFAULT_STEP);
    }

    public IncrementalShort(Short obj, Short step) {
        super(obj);
        this.step = step != null ? step : DEFAULT_STEP;
    }

    public void setStep(short step) {
        this.step = step;
    }

    short next(short obj) {
        return Integer.valueOf(obj + step).shortValue();
    }

    @Override
    protected Short doNext() {
        Short obj = get();
        return obj != null ? next(obj) : 0;
    }

    public void next(short[] array) {
        short start = object == null ? 0 : object;
        for (int i = 0; i < array.length; i++) {
            array[i] = start;
            start = next(start);
        }
        setObject(start);
    }

}
