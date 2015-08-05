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

import java.util.Collection;

/**
 * Value instance that allows to create collection of values using provided value instance strategy (@code instance)
 *
 * @author Serge Pruteanu
 */
public class CollectionValue<V, T extends Collection<V>> extends Constant<T> {
    private Integer count;
    private Value<V> instance;
    private Class<T> clazzType;

    public CollectionValue(T collection, Value<V> value) {
        this(collection, value, null);
    }

    public CollectionValue(T value, Value<V> value1, Integer count) {
        super(value);
        this.instance = value1;
        this.count = count;
    }

    public CollectionValue(Class<T> clazzType, Value<V> value1, Integer count) {
        super(null);
        this.clazzType = clazzType;
        this.instance = value1;
        this.count = count;
    }

    public CollectionValue<V, T> count(Integer count) {
        this.count = count;
        return this;
    }

    public void setCount(Integer count) {
        this.count = count;
    }

    public Value<V> getInstance() {
        return instance;
    }

    protected T doNext() {
        Integer count = this.count;
        if (count == null) {
            count = new RandomInteger(1).between(1, 20).next();
        }
        final T value = checkCreate(count);
        for (int i = 0; i < count; i++) {
            value.add(instance.next());
        }
        return value;
    }

    @SuppressWarnings("unchecked")
    T checkCreate(int count) {
        T collection = get();
        Class<T> clazzType = this.clazzType;
        if (clazzType == null && collection != null) {
            clazzType = (Class<T>) collection.getClass();
        }
        if (clazzType == null) {
            throw new RuntimeException(String.format("Value map type is undefined, either clazzType or collection instance: %s should be provided", collection));
        }

        collection = (T) Util.createInstance(clazzType, new Object[]{});
        return collection;
    }

}
