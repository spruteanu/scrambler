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

import java.util.Collection;

/**
 * Data instance that allows to create collection of objects using provided object instance strategy (@code instance)
 *
 * @author Serge Pruteanu
 */
public class CollectionData<V, T extends Collection<V>> extends ConstantData<T> {
    private Integer count;
    private Data<V> instance;
    private Class<T> clazzType;

    public CollectionData(T collection, Data<V> data) {
        this(collection, data, null);
    }

    public CollectionData(T obj, Data<V> data1, Integer count) {
        super(obj);
        this.instance = data1;
        this.count = count;
    }

    public CollectionData(Class<T> clazzType, Data<V> data1, Integer count) {
        super(null);
        this.clazzType = clazzType;
        this.instance = data1;
        this.count = count;
    }

    public CollectionData<V, T> count(Integer count) {
        this.count = count;
        return this;
    }

    public void setCount(Integer count) {
        this.count = count;
    }

    public Data<V> getInstance() {
        return instance;
    }

    protected T doNext() {
        Integer count = this.count;
        if (count == null) {
            count = new RandomInteger(1).between(1, 20).next();
        }
        final T obj = checkCreate(count);
        for (int i = 0; i < count; i++) {
            obj.add(instance.next());
        }
        return obj;
    }

    @SuppressWarnings("unchecked")
    T checkCreate(int count) {
        T collection = get();
        Class<T> clazzType = this.clazzType;
        if (clazzType == null && collection != null) {
            clazzType = (Class<T>) collection.getClass();
        }
        if (clazzType == null) {
            throw new RuntimeException(String.format("Data map type is undefined, either clazzType or collection instance: %s should be provided", collection));
        }

        collection = (T) Util.createInstance(clazzType, new Object[]{});
        return collection;
    }

}
