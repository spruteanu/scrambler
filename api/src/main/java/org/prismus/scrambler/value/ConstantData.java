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

import org.prismus.scrambler.Data;

/**
 * Data instance that represents a constant. Methods get/next will return same value. Also, class can be
 * used for developing custom implementations by overriding method {@link ConstantData#doNext()},
 * as next will store generated value
 * 
 * @author Serge Pruteanu
 */
public class ConstantData<T> implements Data<T> {
    protected transient T value;

    public ConstantData() {
    }

    public ConstantData(T value) {
        this.value = value;
    }

    public void setValue(T value) {
        this.value = value;
    }

    public ConstantData<T> usingValue(T value) {
        this.value = value;
        return this;
    }

    public T get() {
        return value;
    }

    public T next() {
        final T value = doNext();
        setValue(value);
        return value;
    }

    protected T doNext() {
        return value;
    }

    @Override
    public Object clone() throws CloneNotSupportedException {
        return super.clone();
    }

}
