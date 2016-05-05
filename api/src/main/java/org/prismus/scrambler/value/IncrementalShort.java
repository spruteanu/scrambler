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
public class IncrementalShort extends Constant<Short> implements ShortArray {
    private static final short DEFAULT_STEP = Integer.valueOf(1).shortValue();
    private short step;

    public IncrementalShort() {
        this(null, DEFAULT_STEP);
    }

    public IncrementalShort(Short value) {
        this(value, DEFAULT_STEP);
    }

    public IncrementalShort(Short value, Short step) {
        super(value);
        this.step = step != null ? step : DEFAULT_STEP;
    }

    public void setStep(short step) {
        this.step = step;
    }

    short next(short value) {
        return Integer.valueOf(value + step).shortValue();
    }

    @Override
    protected Short doNext() {
        Short value = get();
        return value != null ? next(value) : 0;
    }

    public void next(short[] values) {
        short start = value == null ? 0 : value;
        for (int i = 0; i < values.length; i++) {
            values[i] = start;
            start = next(start);
        }
        setValue(start);
    }

}
