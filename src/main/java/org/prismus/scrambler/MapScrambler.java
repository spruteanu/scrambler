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

import org.prismus.scrambler.value.MapValue;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

/**
 * {@link java.util.Map} value methods, exposes all possible ways to generate {@link java.util.Map} objects
 *
 * @author Serge Pruteanu
 */
public class MapScrambler {
    //------------------------------------------------------------------------------------------------------------------
    // Map methods
    //------------------------------------------------------------------------------------------------------------------
    @SuppressWarnings("unchecked")
    public static <K> MapValue<K> of(Map<K, Value> keyValueMap) {
        return new MapValue<K>((Class<Map>) keyValueMap.getClass(), keyValueMap);
    }

    public static <K> MapValue<K> of(Map<K, Object> self, Map<K, Value> keyValueMap) {
        return new MapValue<K>(self, keyValueMap);
    }

    public static <K> MapValue<K> mapOf(Set<K> self, Map<ValuePredicate, Value> definitionMap) {
        final Map<K, Object> valueMap = new LinkedHashMap<K, Object>();
        final Map<K, Value> keyValueMap = new LinkedHashMap<K, Value>();
        for (Map.Entry<ValuePredicate, Value> entry : definitionMap.entrySet()) {
            for (K key : self) {
                final ValuePredicate predicate = entry.getKey();
                final Value value = entry.getValue();
                if (predicate.apply(key.toString(), value.get())) {
                    keyValueMap.put(key, value);
                    valueMap.put(key, value.get());
                    break;
                }
            }
        }
        return new MapValue<K>(valueMap, keyValueMap);
    }

    @SuppressWarnings("unchecked")
    public static <K> MapValue<K> mapOf(Class<? extends Map> mapType, Map<K, Value> keyValueMap) {
        return new MapValue<K>((Class<Map>) mapType, keyValueMap);
    }

}
