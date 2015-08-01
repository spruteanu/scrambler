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

import org.prismus.scrambler.Value;

/**
 * Value instance that represents a constant. Methods get/next will return same value
 * 
 * @author Serge Pruteanu
 */
public class Constant<T> implements Value<T> {
    protected transient T value;

    public Constant() {
    }

    public Constant(T value) {
        this.value = value;
    }

    public void setValue(T value) {
        this.value = value;
    }

    public Constant<T> usingValue(T value) {
        this.value = value;
        return this;
    }

    public T get() {
        return value;
    }

    public T next() {
        return value;
    }

    @Override
    public Object clone() throws CloneNotSupportedException {
        return super.clone();
    }

}
