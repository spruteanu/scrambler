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

package org.prismus.scrambler;

import org.prismus.scrambler.data.CollectionData;
import org.prismus.scrambler.data.Combinations;
import org.prismus.scrambler.data.ListRandomElement;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * {@link java.util.Collection} object methods, exposes all possible ways to generate {@link java.util.Collection} objects
 *
 * @author Serge Pruteanu
 */
public class CollectionScrambler {

    //------------------------------------------------------------------------------------------------------------------
    // Collection methods
    //------------------------------------------------------------------------------------------------------------------
    public static <V, T extends Collection<V>> CollectionData<V, T> of(T collection, Data<V> data) {
        return of(collection, data, null);
    }

    public static <V, T extends Collection<V>> CollectionData<V, T> of(T collection, Data<V> data, Integer count) {
        return new CollectionData<V, T>(collection, data, count);
    }

    public static <T> Data<T> randomOf(List<T> list) {
        return new ListRandomElement<T>(list);
    }

    public static <T> Data<T> randomOf(Collection<T> collection) {
        return new ListRandomElement<T>(new ArrayList<T>(collection));
    }

    public static <V, T extends Collection<V>> CollectionData<V, T> collectionOf(Class<T> clazzType, Data<V> data) {
        return new CollectionData<V, T>(clazzType, data, null);
    }

    public static <T> Data<List<T>> combinationsOf(List<T> dataList) {
        return Combinations.of(dataList);
    }

    public static <T> Data<List<T>> dataCombinations(List<Data<T>> dataList) {
        return Combinations.dataOf(dataList);
    }

}
