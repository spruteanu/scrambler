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

import java.math.BigDecimal;

/**
 * @author Serge Pruteanu
 */
public class IncrementalBigDecimal extends ConstantData<BigDecimal> {
    private BigDecimal step;
    private static final BigDecimal DEFAULT_STEP = BigDecimal.valueOf(1L);

    public IncrementalBigDecimal() {
        this(null, DEFAULT_STEP);
    }

    public IncrementalBigDecimal(BigDecimal obj) {
        this(obj, DEFAULT_STEP);
    }

    public IncrementalBigDecimal(BigDecimal obj, BigDecimal step) {
        super(obj);
        this.step = step != null ? step : DEFAULT_STEP;
    }

    public void setStep(BigDecimal step) {
        this.step = step;
    }

    @Override
    protected BigDecimal doNext() {
        BigDecimal obj = get();
        return obj != null ? obj.add(step) : BigDecimal.ZERO;
    }

}
