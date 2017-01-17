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

import java.util.Random;

/**
 * @author Serge Pruteanu
 */
public class RandomByte extends AbstractRandomRange<Byte> implements ByteArray {
    private final java.util.Random random;

    public RandomByte() {
        this(null, null, null);
    }

    public RandomByte(Byte obj) {
        this(obj, null, null);
    }

    public RandomByte(Byte minimum, Byte maximum) {
        this(null, minimum, maximum);
    }

    public RandomByte(Byte obj, Byte minimum, Byte maximum) {
        super(obj, minimum, maximum);
        usingDefaults(Integer.valueOf(0).byteValue(), Byte.MAX_VALUE);
        random = new Random();
    }

    @Override
    protected Byte min(Byte val1, Byte val2) {
        return val1 != null ? val2 != null ? val1.compareTo(val2) > 0 ? val2 : val1 : null : null;
    }

    @Override
    protected Byte max(Byte val1, Byte val2) {
        return val1 != null ? val2 != null ? val1.compareTo(val2) > 0 ? val1 : val2 : val1 : val2;
    }

    byte nextValue() {
        final byte result;
        if (minimum != null && maximum != null) {
            result = Integer.valueOf(minimum + random.nextInt(Math.abs(maximum - minimum))).byteValue();
        } else {
            result = Integer.valueOf(object != null ? random.nextInt(Math.abs(object) + 1) : random.nextInt()).byteValue();
        }
        return result;
    }

    @Override
    public Byte get() {
        return object == null ? nextValue() : object;
    }

    @Override
    protected Byte doNext() {
        return nextValue();
    }

    public void next(byte[] array) {
        byte next = nextValue();
        for (int i = 0; i < array.length; i++) {
            next = nextValue();
            array[i] = next;
        }
        setObject(next);
    }

}
