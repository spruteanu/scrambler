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
public class RandomBoolean extends Constant<Boolean> implements BooleanArray {

    private final Random random;

    public RandomBoolean() {
        this(null);
    }

    public RandomBoolean(Boolean value) {
        super(value);
        random = new Random();
    }

    boolean nextValue() {
        return random.nextBoolean();
    }

    @Override
    public Boolean get() {
        return value == null ? nextValue() : value;
    }

    @Override
    protected Boolean doNext() {
        return nextValue();
    }

    public void next(boolean[] values) {
        for (int i = 0; i < values.length; i++) {
            values[i] = nextValue();
        }
    }

}
