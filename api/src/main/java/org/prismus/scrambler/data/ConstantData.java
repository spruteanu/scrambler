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

/**
 * Data instance that represents a constant. Methods get/next will return same object. Also, class can be
 * used for developing custom implementations by overriding method {@link ConstantData#doNext()},
 * as next will store generated object
 * 
 * @author Serge Pruteanu
 */
public class ConstantData<T> implements Data<T> {
    protected transient T object;

    public ConstantData() {
    }

    public ConstantData(T obj) {
        this.object = obj;
    }

    public void setObject(T obj) {
        this.object = obj;
    }

    public ConstantData<T> usingInstance(T obj) {
        this.object = obj;
        return this;
    }

    public T get() {
        return object;
    }

    public T next() {
        final T t = doNext();
        setObject(t);
        return t;
    }

    protected T doNext() {
        return object;
    }

    @Override
    public Object clone() throws CloneNotSupportedException {
        return super.clone();
    }

}
