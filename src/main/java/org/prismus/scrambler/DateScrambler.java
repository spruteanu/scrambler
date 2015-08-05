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

package org.prismus.scrambler;

import org.prismus.scrambler.value.ArrayValue;
import org.prismus.scrambler.value.IncrementalDate;
import org.prismus.scrambler.value.RandomDate;

import java.util.Date;
import java.util.Map;

/**
 * {@link Date} value methods, exposes all possible ways to generate {@link Date} objects
 *
 * @author Serge Pruteanu
 */
public class DateScrambler {
    //------------------------------------------------------------------------------------------------------------------
    // Dates methods
    //------------------------------------------------------------------------------------------------------------------
    public static IncrementalDate increment(Date self) {
        return new IncrementalDate(self);
    }

    public static IncrementalDate increment(Date self, Integer step) {
        return new IncrementalDate(self, step);
    }

    public static IncrementalDate increment(Date self, Integer step, Integer calendarField) {
        return new IncrementalDate(self, calendarField, step);
    }

    public static ArrayValue<Date> incrementArray(Date self, Integer step, Integer count) {
        return new ArrayValue<Date>(Date.class, count, increment(self, step));
    }

    public static IncrementalDate increment(Date self, Map<Integer, Integer> calendarFieldStepMap) {
        return new IncrementalDate(self).incrementBy(calendarFieldStepMap);
    }

    public static ArrayValue<Date> incrementArray(Date self, Map<Integer, Integer> calendarFieldStepMap, Integer count) {
        return new ArrayValue<Date>(Date.class, count, increment(self, calendarFieldStepMap));
    }

    public static ArrayValue<Date> incrementArray(Date self, Integer step, Integer calendarField, Integer count) {
        return new ArrayValue<Date>(Date.class, count, increment(self, step, calendarField));
    }

    public static ArrayValue<Date> incrementArray(Date self, Integer count) {
        return new ArrayValue<Date>(Date.class, count, increment(self));
    }

    public static ArrayValue<Date> arrayOf(Value<Date> value, Integer count) {
        return new ArrayValue<Date>(Date.class, count, value);
    }

    public static RandomDate random(Date value) {
        return new RandomDate(value);
    }

    public static RandomDate random(Date minimum, Date maximum) {
        return new RandomDate(minimum, maximum);
    }

    public static RandomDate random(Date self, Date minimum, Date maximum) {
        return new RandomDate(self, minimum, maximum);
    }

    public static ArrayValue<Date> randomArray(Date self, Date minimum, Date maximum, Integer count) {
        final RandomDate randomDate = new RandomDate(self, minimum, maximum);
        randomDate.next();
        return new ArrayValue<Date>(Date.class, count, randomDate);
    }

}
