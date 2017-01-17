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
public class IncrementalString extends ConstantData<String> {
    private static final String DEFAULT_PATTERN = "%s%010d";
    private static final int DEFAULT_STEP = 1;

    private Integer index;
    private String pattern;

    public IncrementalString() {
        this(null, DEFAULT_PATTERN, DEFAULT_STEP);
    }

    public IncrementalString(String obj) {
        this(obj, DEFAULT_PATTERN, DEFAULT_STEP);
    }

    public IncrementalString(String obj, String pattern) {
        this(obj, pattern, DEFAULT_STEP);
    }

    public IncrementalString(String obj, Integer index) {
        this(obj, DEFAULT_PATTERN, index);
    }

    public IncrementalString(String obj, String pattern, Integer index) {
        super(obj);
        validatePattern(pattern);
        this.pattern = pattern;
        this.index = index != null ? index : DEFAULT_STEP;
    }

    void validatePattern(String pattern) {
        int i = pattern.indexOf('%');
        if (i >= 0) {
            i = pattern.indexOf('%', i + 1);
            if (i > 0) {
                return;
            }
        }
        throw new IllegalArgumentException(String.format("Invalid pattern provided: %s; It must contain 2 formatter characters.", pattern));
    }

    public void setIndex(Integer index) {
        this.index = index;
    }

    public void setPattern(String pattern) {
        this.pattern = pattern;
    }

    String generateString(Integer index) {
        if (object == null) {
            object = "RandomString";
        }
        return String.format(checkPattern(), object, index);
    }

    public String get() {
        return generateString(index);
    }

    protected Integer nextIndex() {
        return ++index;
    }

    @Override
    public String next() {
        return generateString(nextIndex());
    }

    String checkPattern() {
        String pattern = this.pattern;
        if (pattern == null) {
            pattern = DEFAULT_PATTERN;
        }
        return pattern;
    }

}
