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
 * Value instance that returns randomly an element from provided {@code array} array
 *
 * @author Serge Pruteanu
 */
public class ArrayRandomElement<T> extends RandomElementData<T> {
    private T[] array;

    public ArrayRandomElement(T[] array) {
        super();
        this.array = array;
        setObject(array[0]);
    }

    public void setArray(T[] array) {
        this.array = array;
    }

    protected T doNext() {
        return array[random.nextInt(array.length)];
    }

    public T[] getArray() {
        return array;
    }

}
