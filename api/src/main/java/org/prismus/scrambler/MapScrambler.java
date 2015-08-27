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
import org.prismus.scrambler.value.ValueDefinition;

import java.util.*;
import java.util.regex.Pattern;

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

    @SuppressWarnings("unchecked")
    public static <K> MapValue<K> of(Class<? extends Map> mapType, Map<K, Value> keyValueMap) {
        return new MapValue<K>((Class<Map>) mapType, keyValueMap);
    }

    public static <K> MapValue<K> of(Collection<K> self, Map<ValuePredicate, Value> definitionMap) {
        final Map<K, Object> valueMap = new LinkedHashMap<K, Object>();
        final Map<K, Value> keyValueMap = matchKeyValueMap(self, definitionMap, valueMap);
        return new MapValue<K>(valueMap, keyValueMap);
    }

    public static <K> MapValue<K> mapOf(Map<K, Object> self, Map<String, Object> contextMap, String... definitions) {
        final Map<K, Value> keyValueMap = lookupKeValueMap(self, contextMap, definitions);
        return of(self, keyValueMap);
    }

    public static <K> MapValue<K> mapOf(Class<Map> mapType, Collection<K> self, String... definitions) {
        return mapOf(mapType, self, null, definitions);
    }

    @SuppressWarnings("unchecked")
    public static <K> MapValue<K> mapOf(Class<Map> self, Collection<K> keys, Map<String, Object> contextMap, String... definitions) {
        final Map<K, Value> keValueMap = lookupKeValueMap(keys, contextMap, definitions);
        return new MapValue<K>(self, keValueMap);
    }

    public static <K> MapValue<K> mapOf(Collection<K> self, String... definitions) {
        return mapOf(self, null, definitions);
    }

    public static <K> MapValue<K> mapOf(Collection<K> self, Map<String, Object> contextMap, String... definitions) {
        return of(lookupKeValueMap(self, contextMap, definitions));
    }

    static <K> Map<K, Value> matchKeyValueMap(Collection<K> self, Map<ValuePredicate, Value> definitionMap, Map<K, Object> valueMap) {
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
        return keyValueMap;
    }

    static <K> Map<K, Value> lookupKeValueMap(Collection<K> self, Map<String, Object> contextMap, String... definitions) {
        final Map<K, Object> map = new LinkedHashMap<K, Object>();
        for (K k : self) {
            map.put(k, null);
        }
        return lookupKeValueMap(map, contextMap, definitions);
    }

    static <K> Map<K, Value> lookupKeValueMap(Map<K, Object> self, Map<String, Object> contextMap, String... definitions) {
        final ValueDefinition definition = new ValueDefinition().usingContext(contextMap);
        if (definitions != null && definitions.length > 0) {
            definition.scanDefinitions(Arrays.asList(definitions));
        } else {
            definition.usingLibraryDefinitions(null);
        }
        final Map<K, Value> keyValueMap = new LinkedHashMap<K, Value>(self.size());
        for (Map.Entry<K, Object> entry : self.entrySet()) {
            Value value = null;
            final K k = entry.getKey();
            if (k instanceof ValuePredicate) {
                value = definition.lookupValue((ValuePredicate) k);
            } else if (k instanceof String){
                final Object o = entry.getValue();
                value = definition.lookupValue((String) k, o != null ? o.getClass() : null);
            } else if (k instanceof Pattern) {
                value = definition.lookupValue(ValuePredicates.matchProperty((Pattern) k));
            }
            if (value != null) {
                keyValueMap.put(k, value);
            }
        }
        return keyValueMap;
    }

}
