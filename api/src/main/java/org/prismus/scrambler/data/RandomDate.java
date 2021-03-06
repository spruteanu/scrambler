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

import org.prismus.scrambler.Data;

import java.sql.Timestamp;
import java.util.Date;

/**
 * @author Serge Pruteanu
 */
public class RandomDate extends AbstractRandomRange<Date> {

    private final RandomLong randomLong;
    private Data<Date> dataInstance;

    public RandomDate() {
        this(null, null, null);
    }

    public RandomDate(Date date) {
        this(date, null, null);
    }

    public RandomDate(Date minimum, Date maximum) {
        this(null, minimum, maximum);
    }

    public RandomDate(Date date, Date minimum, Date maximum) {
        super(date, minimum, maximum);
        randomLong = new RandomLong();
        dataInstance = new ConstantData<Date>(new IncrementalDate().usingDefaults().next());
    }

    @Override
    protected Date min(Date val1, Date val2) {
        return val1 != null ? val2 != null ? val1.compareTo(val2) > 0 ? val2 : val1 : null : null;
    }

    @Override
    protected Date max(Date val1, Date val2) {
        return val1 != null ? val2 != null ? val1.compareTo(val2) > 0 ? val1 : val2 : val1 : val2;
    }

    protected Date doNext() {
        Date value = get();
        if (minimum != null && maximum != null) {
            value = nextValue(minimum, maximum);
        } else {
            if (value == null) {
                value = minimum;
            }
            if (value == null) {
                value = maximum;
            }
            if (value == null) {
                value = new Timestamp(System.currentTimeMillis());
            }
            value = nextValue(value, dataInstance.next());
        }
        return value;
    }

    Date nextValue(Date minimum, Date maximum) {
        return new Date(randomLong.between(minimum.getTime(), maximum.getTime()).next());
    }

}
