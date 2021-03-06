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

/**
 * @author Serge Pruteanu
 */
public class IncrementalBigInteger extends ConstantData<BigInteger> {
    private BigInteger step;
    private static final BigInteger DEFAULT_STEP = BigInteger.valueOf(1L);

    public IncrementalBigInteger() {
        this(null, DEFAULT_STEP);
    }

    public IncrementalBigInteger(BigInteger obj) {
        this(obj, DEFAULT_STEP);
    }

    public IncrementalBigInteger(BigInteger obj, BigInteger step) {
        super(obj);
        this.step = step != null ? step : DEFAULT_STEP;
    }

    public void setStep(BigInteger step) {
        this.step = step;
    }

    @Override
    protected BigInteger doNext() {
        BigInteger obj = get();
        return obj != null ? obj.add(step) : BigInteger.ZERO;
    }

}
