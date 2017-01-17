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
public class RandomBigDecimal extends AbstractRandomRange<BigDecimal> {

    public RandomBigDecimal() {
        this(null, null, null);
    }

    public RandomBigDecimal(BigDecimal obj) {
        this(obj, null, null);
    }

    public RandomBigDecimal(BigDecimal minimum, BigDecimal maximum) {
        this(null, minimum, maximum);
    }

    public RandomBigDecimal(BigDecimal obj, BigDecimal minimum, BigDecimal maximum) {
        super(obj, minimum, maximum);
        usingDefaults(BigDecimal.ZERO, BigDecimal.valueOf(Double.MAX_VALUE));
    }

    @Override
    protected BigDecimal min(BigDecimal val1, BigDecimal val2) {
        return val1 != null ? val2 != null ? val1.compareTo(val2) > 0 ? val2 : val1 : null : null;
    }

    @Override
    protected BigDecimal max(BigDecimal val1, BigDecimal val2) {
        return val1 != null ? val2 != null ? val1.compareTo(val2) > 0 ? val1 : val2 : val1 : val2;
    }

    BigDecimal nextValue() {
        final BigDecimal result;
        if (minimum != null && maximum != null) {
            result = minimum.add(maximum.subtract(minimum).multiply(new BigDecimal(Math.random())));
        } else {
            result = defaultMaximum.multiply(new BigDecimal(Math.random()));
        }
        return result;
    }

    @Override
    public BigDecimal get() {
        return object == null ? nextValue() : object;
    }

    @Override
    protected BigDecimal doNext() {
        return nextValue();
    }

}
