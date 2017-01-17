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

import org.prismus.scrambler.value.MapData;
import org.prismus.scrambler.value.DataDefinition;

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
    public static <K> MapData<K> of(Map<K, Data> keyValueMap) {
        return new MapData<K>((Class<Map>) keyValueMap.getClass(), keyValueMap);
    }

    public static <K> MapData<K> of(Map<K, Object> self, Map<K, Data> keyValueMap) {
        return new MapData<K>(self, keyValueMap);
    }

    @SuppressWarnings("unchecked")
    public static <K> MapData<K> of(Class<? extends Map> mapType, Map<K, Data> keyValueMap) {
        return new MapData<K>((Class<Map>) mapType, keyValueMap);
    }

    public static <K> MapData<K> of(Collection<K> self, Map<DataPredicate, Data> definitionMap) {
        final Map<K, Object> valueMap = new LinkedHashMap<K, Object>();
        final Map<K, Data> keyValueMap = matchKeyValueMap(self, definitionMap, valueMap);
        return new MapData<K>(valueMap, keyValueMap);
    }

    public static <K> MapData<K> mapOf(Map<K, Object> self, Map<String, Object> contextMap, String... definitions) {
        final Map<K, Data> keyValueMap = lookupKeValueMap(self, contextMap, definitions);
        return of(self, keyValueMap);
    }

    public static <K> MapData<K> mapOf(Class<Map> mapType, Collection<K> self, String... definitions) {
        return mapOf(mapType, self, null, definitions);
    }

    @SuppressWarnings("unchecked")
    public static <K> MapData<K> mapOf(Class<Map> self, Collection<K> keys, Map<String, Object> contextMap, String... definitions) {
        final Map<K, Data> keValueMap = lookupKeValueMap(keys, contextMap, definitions);
        return new MapData<K>(self, keValueMap);
    }

    public static <K> MapData<K> mapOf(Collection<K> self, String... definitions) {
        return mapOf(self, null, definitions);
    }

    public static <K> MapData<K> mapOf(Collection<K> self, Map<String, Object> contextMap, String... definitions) {
        return of(lookupKeValueMap(self, contextMap, definitions));
    }

    static <K> Map<K, Data> matchKeyValueMap(Collection<K> self, Map<DataPredicate, Data> definitionMap, Map<K, Object> valueMap) {
        final Map<K, Data> keyValueMap = new LinkedHashMap<K, Data>();
        for (Map.Entry<DataPredicate, Data> entry : definitionMap.entrySet()) {
            for (K key : self) {
                final DataPredicate predicate = entry.getKey();
                final Data data = entry.getValue();
                if (predicate.apply(key.toString(), data.get())) {
                    keyValueMap.put(key, data);
                    valueMap.put(key, data.get());
                    break;
                }
            }
        }
        return keyValueMap;
    }

    static <K> Map<K, Data> lookupKeValueMap(Collection<K> self, Map<String, Object> contextMap, String... definitions) {
        final Map<K, Object> map = new LinkedHashMap<K, Object>();
        for (K k : self) {
            map.put(k, null);
        }
        return lookupKeValueMap(map, contextMap, definitions);
    }

    static <K> Map<K, Data> lookupKeValueMap(Map<K, Object> self, Map<String, Object> contextMap, String... definitions) {
        final DataDefinition definition = new DataDefinition().usingContext(contextMap);
        if (definitions != null && definitions.length > 0) {
            definition.scanDefinitions(Arrays.asList(definitions));
        } else {
            definition.usingLibraryDefinitions(null);
        }
        final Map<K, Data> keyValueMap = new LinkedHashMap<K, Data>(self.size());
        for (Map.Entry<K, Object> entry : self.entrySet()) {
            Data data = null;
            final K k = entry.getKey();
            if (k instanceof DataPredicate) {
                data = definition.lookupValue((DataPredicate) k);
            } else if (k instanceof String){
                final Object o = entry.getValue();
                data = definition.lookupValue((String) k, o != null ? o.getClass() : null);
            } else if (k instanceof Pattern) {
                data = definition.lookupValue(DataPredicates.matchProperty((Pattern) k));
            }
            if (data != null) {
                keyValueMap.put(k, data);
            }
        }
        return keyValueMap;
    }

}
