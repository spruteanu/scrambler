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

import java.lang.reflect.Array;

/**
 * Value instance that allows to create an array of values using provided value instance strategy (@code instance)
 *
 * @author Serge Pruteanu
 */
public class ArrayValue<T> extends Constant<T[]> {
    private Integer count;
    private Value<T> instance;
    private Class<T> valueType;

    public ArrayValue() {
    }

    public ArrayValue(T[] array, Value<T> value) {
        this(array, null, value);
    }

    public ArrayValue(Class<T> valueType, Value<T> value) {
        this(valueType, null, value);
    }

    public ArrayValue(T[] array, Integer count, Value<T> value1) {
        super(array);
        this.count = count != null ? count : array != null ? array.length : null;
        this.instance = value1;
    }

    public ArrayValue(Class<T> valueType, Integer count, Value<T> value1) {
        super(null);
        this.valueType = valueType;
        this.count = count;
        this.instance = value1;
    }

    @SuppressWarnings("unchecked")
    public ArrayValue forType(Class<T> valueType) {
        this.valueType = valueType.isArray() ? (Class<T>) valueType.getComponentType() : valueType;
        return this;
    }

    @Override
    public T[] next() {
        Integer count = this.count;
        if (count == null) {
            count = new RandomInteger(1).between(1, 20).next();
        }

        final T[] value = checkCreate(get(), count);
        T start = instance.get();
        if (start == null) {
            start = instance.next();
        }
        for (int i = 0; i < count; i++) {
            value[i] = start;
            start = instance.next();
        }
        setValue(value);
        return value;
    }

    @SuppressWarnings("unchecked")
    public T[] checkCreate(T[] array, int count) {
        Class type = valueType;
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

    public Value getInstance() {
        return instance;
    }

    public void setInstance(Value<T> instance) {
        this.instance = instance;
    }

    public void setValueType(Class<T> valueType) {
        this.valueType = valueType;
    }

}
