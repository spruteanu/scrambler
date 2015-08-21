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

import org.prismus.scrambler.value.CollectionValue;
import org.prismus.scrambler.value.Combinations;
import org.prismus.scrambler.value.ListRandomElement;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * {@link java.util.Collection} value methods, exposes all possible ways to generate {@link java.util.Collection} objects
 *
 * @author Serge Pruteanu
 */
public class CollectionScrambler {

    //------------------------------------------------------------------------------------------------------------------
    // Collection methods
    //------------------------------------------------------------------------------------------------------------------
    public static <V, T extends Collection<V>> CollectionValue<V, T> of(T collection, Value<V> value) {
        return of(collection, value, null);
    }

    public static <V, T extends Collection<V>> CollectionValue<V, T> of(T collection, Value<V> value, Integer count) {
        return new CollectionValue<V, T>(collection, value, count);
    }

    public static <T> Value<T> randomOf(List<T> values) {
        return new ListRandomElement<T>(values);
    }

    public static <T> Value<T> randomOf(Collection<T> collection) {
        return new ListRandomElement<T>(new ArrayList<T>(collection));
    }

    public static <V, T extends Collection<V>> CollectionValue<V, T> collectionOf(Class<T> clazzType, Value<V> value) {
        return new CollectionValue<V, T>(clazzType, value, null);
    }

    public static <T> Value<List<T>> combinationsOf(List<T> values) {
        return Combinations.of(values);
    }

    public static <T> Value<List<T>> combinationValues(List<Value<T>> values) {
        return Combinations.valuesOf(values);
    }

}
