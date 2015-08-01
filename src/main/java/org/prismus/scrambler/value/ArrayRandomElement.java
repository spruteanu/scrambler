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
 * Value instance that returns randomly an element from provided {@code values} array
 *
 * @author Serge Pruteanu
 */
public class ArrayRandomElement<T> extends Constant<T> {
    private final Random random;
    private T[] values;

    public ArrayRandomElement(T[] values) {
        super();
        this.values = values;
        setValue(values[0]);
        random = new Random();
    }

    public void setValues(T[] values) {
        this.values = values;
    }

    public T next() {
        final T value = values[random.nextInt(values.length)];
        setValue(value);
        return value;
    }

    public T[] getValues() {
        return values;
    }

}
