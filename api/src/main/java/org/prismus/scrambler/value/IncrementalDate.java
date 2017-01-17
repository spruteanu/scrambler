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

import java.sql.Timestamp;
import java.util.*;

/**
 * Increments date by seconds/minutes/hours/weeks/month/years values using provided or default calendar
 *
 * @author Serge Pruteanu
 */
public class IncrementalDate extends ConstantData<Date> {
    private static final int DEFAULT_STEP = 1;
    private static final int DEFAULT_CALENDAR_FIELD = Calendar.DATE;

    private Map<Integer, Integer> calendarFieldStepMap = new LinkedHashMap<Integer, Integer>();
    private Calendar calendar = Calendar.getInstance();

    public IncrementalDate() {
        super();
    }

    public IncrementalDate(Integer step) {
        this(new Date(), DEFAULT_CALENDAR_FIELD, step);
    }

    public IncrementalDate(Date value) {
        this(value, DEFAULT_CALENDAR_FIELD, DEFAULT_STEP);
    }

    public IncrementalDate(Date value, Integer step) {
        this(value, DEFAULT_CALENDAR_FIELD, step);
    }

    public IncrementalDate(Date value, Integer calendarField, Integer step) {
        super(value);
        calendarFieldStepMap.put(calendarField != null ? calendarField : DEFAULT_CALENDAR_FIELD, step != null ? step : DEFAULT_STEP);
    }

    public void setCalendar(Calendar calendar) {
        this.calendar = calendar;
    }

    public IncrementalDate withCalendar(Calendar calendar) {
        this.calendar = calendar;
        return this;
    }

    public IncrementalDate forLocale(Locale locale) {
        this.calendar = Calendar.getInstance(locale);
        return this;
    }

    public IncrementalDate milliseconds(Integer step) {
        calendarFieldStepMap.put(Calendar.MILLISECOND, step);
        return this;
    }

    public IncrementalDate seconds(Integer step) {
        calendarFieldStepMap.put(Calendar.SECOND, step);
        return this;
    }

    public IncrementalDate minutes(Integer step) {
        calendarFieldStepMap.put(Calendar.MINUTE, step);
        return this;
    }

    public IncrementalDate hours(Integer step) {
        calendarFieldStepMap.put(Calendar.HOUR, step);
        return this;
    }

    public IncrementalDate days(Integer step) {
        calendarFieldStepMap.put(Calendar.DATE, step);
        return this;
    }

    public IncrementalDate weeks(Integer step) {
        calendarFieldStepMap.put(Calendar.WEEK_OF_YEAR, step);
        return this;
    }

    public IncrementalDate months(Integer step) {
        calendarFieldStepMap.put(Calendar.MONTH, step);
        return this;
    }

    public IncrementalDate years(Integer step) {
        calendarFieldStepMap.put(Calendar.YEAR, step);
        return this;
    }

    public IncrementalDate incrementBy(Map<Integer, Integer> calendarFieldStepMap) {
        this.calendarFieldStepMap.putAll(calendarFieldStepMap);
        return this;
    }

    public IncrementalDate withCalendarFields(Map<Integer, Integer> calendarFieldStepMap) {
        this.calendarFieldStepMap = calendarFieldStepMap;
        return this;
    }

    public IncrementalDate usingDefaults() {
        calendarFieldStepMap.put(DEFAULT_CALENDAR_FIELD, DEFAULT_STEP);
        return this;
    }

    @SuppressWarnings("MagicConstant")
    protected Date doNext() {
        Date value = get();
        if (value == null) {
            value = new Timestamp(System.currentTimeMillis());
        }

        calendar.setTime(value);
        for (Map.Entry<Integer, Integer> entry : calendarFieldStepMap.entrySet()) {
            calendar.add(entry.getKey(), entry.getValue());
        }
        value = calendar.getTime();
        return value;
    }

}
