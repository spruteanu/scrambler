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

import java.util.Random;

/**
 * @author Serge Pruteanu
 */
class RandomByte extends AbstractRandomRange<Byte> implements ByteArray {
    private final java.util.Random random;

    public RandomByte() {
        this(null, null, null);
    }

    public RandomByte(Byte value) {
        this(value, null, null);
    }

    public RandomByte(Byte minimum, Byte maximum) {
        this(null, minimum, maximum);
    }

    public RandomByte(Byte value, Byte minimum, Byte maximum) {
        super(value, minimum, maximum);
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
            result = Integer.valueOf(value != null ? random.nextInt(Math.abs(value) + 1) : random.nextInt()).byteValue();
        }
        return result;
    }

    @Override
    public Byte get() {
        return value == null ? nextValue() : value;
    }

    @Override
    protected Byte doNext() {
        return nextValue();
    }

    public void next(byte[] values) {
        byte next = nextValue();
        for (int i = 0; i < values.length; i++) {
            next = nextValue();
            values[i] = next;
        }
        setValue(next);
    }

}
