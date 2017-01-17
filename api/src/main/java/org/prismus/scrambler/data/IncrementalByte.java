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
public class IncrementalByte extends ConstantData<Byte> implements ByteArray {
    private static final byte DEFAULT_STEP = Integer.valueOf(1).byteValue();
    private byte step;

    public IncrementalByte() {
        this(null, DEFAULT_STEP);
    }

    public IncrementalByte(Byte obj) {
        this(obj, DEFAULT_STEP);
    }

    public IncrementalByte(Byte obj, Byte step) {
        super(obj);
        this.step = step != null ? step : DEFAULT_STEP;
    }

    public void setStep(byte step) {
        this.step = step;
    }

    byte next(byte b) {
        return Integer.valueOf(b + step).byteValue();
    }

    @Override
    protected Byte doNext() {
        Byte b = get();
        return b != null ? next(b) : 0;
    }

    public void next(byte[] array) {
        byte start = object == null ? 0 : object;
        for (int i = 0; i < array.length; i++) {
            array[i] = start;
            start = next(start);
        }
        setObject(start);
    }

}
