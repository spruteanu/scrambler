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

import java.math.BigInteger;
import java.util.Random;

/**
 * @author Serge Pruteanu
 */
public class RandomBigInteger extends AbstractRandomRange<BigInteger> {

    private final Random random;

    public RandomBigInteger() {
        this(null, null, null);
    }

    public RandomBigInteger(BigInteger obj) {
        this(obj, null, null);
    }

    public RandomBigInteger(BigInteger minimum, BigInteger maximum) {
        this(null, minimum, maximum);
    }

    public RandomBigInteger(BigInteger obj, BigInteger minimum, BigInteger maximum) {
        super(obj, minimum, maximum);
        random = new Random();
        usingDefaults(BigInteger.ZERO, BigInteger.valueOf(Long.MAX_VALUE));
    }

    @Override
    protected BigInteger min(BigInteger val1, BigInteger val2) {
        return val1 != null ? val2 != null ? val1.compareTo(val2) > 0 ? val2 : val1 : null : null;
    }

    @Override
    protected BigInteger max(BigInteger val1, BigInteger val2) {
        return val1 != null ? val2 != null ? val1.compareTo(val2) > 0 ? val1 : val2 : val1 : val2;
    }

    BigInteger nextValue() {
        final BigInteger result;
        if (minimum != null && maximum != null) {
            result = minimum.add(new BigInteger(maximum.subtract(minimum).bitCount(), random));
        } else {
            result = new BigInteger(defaultMaximum.bitCount(), random);
        }
        return result;
    }

    @Override
    public BigInteger get() {
        return object == null ? nextValue() : object;
    }

    @Override
    protected BigInteger doNext() {
        return nextValue();
    }

}
