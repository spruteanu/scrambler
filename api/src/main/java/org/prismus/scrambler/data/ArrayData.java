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

import java.lang.reflect.Array;

/**
 * Data instance that allows to create an array of objects using provided object instance strategy (@code instance)
 *
 * @author Serge Pruteanu
 */
public class ArrayData<T> extends ConstantData<T[]> {
    private Integer count;
    private Data<T> instance;
    private Class<T> dataType;

    public ArrayData() {
    }

    public ArrayData(T[] array, Data<T> data) {
        this(array, null, data);
    }

    public ArrayData(Class<T> dataType, Data<T> data) {
        this(dataType, null, data);
    }

    public ArrayData(T[] array, Integer count, Data<T> data1) {
        super(array);
        this.count = count != null ? count : array != null ? array.length : null;
        this.instance = data1;
    }

    public ArrayData(Class<T> dataType, Integer count, Data<T> data1) {
        super(null);
        this.dataType = dataType;
        this.count = count;
        this.instance = data1;
    }

    @SuppressWarnings("unchecked")
    public ArrayData forType(Class<T> dataType) {
        this.dataType = dataType.isArray() ? (Class<T>) dataType.getComponentType() : dataType;
        return this;
    }

    protected T[] doNext() {
        Integer count = this.count;
        if (count == null) {
            count = new RandomInteger(1).between(1, 20).next();
        }

        final T[] array = checkCreate(get(), count, dataType);
        T start = instance.get();
        if (start == null) {
            start = instance.next();
        }
        for (int i = 0; i < count; i++) {
            array[i] = start;
            start = instance.next();
        }
        return array;
    }

    @SuppressWarnings("unchecked")
    static <T> T[] checkCreate(T[] array, int count, Class type) {
        if (type == null) {
            if (array.length != count || (array.length > 0 && array[0] != null)) {
                type = array.getClass();
            } else {
                return array;
            }
        }
        if (type.isArray()) {
            type = type.getComponentType();
        }
        array = (T[]) Array.newInstance(type, count);
        return array;
    }

    public Integer getCount() {
        return count;
    }

    public void setCount(Integer count) {
        this.count = count;
    }

    public Data getInstance() {
        return instance;
    }

    public void setInstance(Data<T> instance) {
        this.instance = instance;
    }

}
