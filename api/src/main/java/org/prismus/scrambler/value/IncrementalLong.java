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
public class IncrementalLong extends Constant<Long> implements LongArray {
    private static final long DEFAULT_STEP = 1L;
    private long step;

    public IncrementalLong() {
        this(null, DEFAULT_STEP);
    }

    public IncrementalLong(Long value) {
        this(value, DEFAULT_STEP);
    }

    public IncrementalLong(Long value, Long step) {
        super(value);
        this.step = step != null ? step : DEFAULT_STEP;
    }

    public void setStep(long step) {
        this.step = step;
    }

    @Override
    protected Long doNext() {
        Long value = get();
        return value != null ? value + step : 0;
    }

    public void next(long[] values) {
        long start = value == null ? 0 : value;
        for (int i = 0; i < values.length; i++) {
            values[i] = start;
            start = start + step;
        }
        setValue(start);
    }

}
