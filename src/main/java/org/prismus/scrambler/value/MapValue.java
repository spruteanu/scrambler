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

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Value instance that allows to create a map of values using provided values strategy (@code keyValueMap)
 *
 * @author Serge Pruteanu
 */
public class MapValue<K> extends Constant<Map<K, Object>> {

    private Map<K, Value> keyValueMap = new LinkedHashMap<K, Value>();
    private Class<Map> clazzType;

    public MapValue() {
        this(new LinkedHashMap<K, Object>());
    }

    public MapValue(Map<K, Object> value) {
        super(value);
    }

    public MapValue(Map<K, Object> value, Map<K, Value> keyValueMap) {
        super(value);
        this.keyValueMap = keyValueMap;
    }

    public MapValue(Class<Map> clazzType) {
        this(clazzType, null);
    }

    public MapValue(Class<Map> clazzType, Map<K, Value> keyValueMap) {
        super(null);
        this.clazzType = clazzType;
        this.keyValueMap = keyValueMap;
    }

    public MapValue<K> of(K key, Value value) {
        keyValueMap.put(key, value);
        return this;
    }

    public MapValue<K> usingValueMap(Map<K, Value> keyValueMap) {
        this.keyValueMap = keyValueMap;
        return this;
    }

    @Override
    public Map<K, Object> next() {
        final Map<K, Object> kvMap = checkCreate();
        for (Map.Entry<K, Value> entry : keyValueMap.entrySet()) {
            kvMap.put(entry.getKey(), entry.getValue().next());
        }
        setValue(kvMap);
        return kvMap;
    }

    @SuppressWarnings("unchecked")
    Map<K, Object> checkCreate() {
        Map<K, Object> valueMap = get();
        Class<Map> clazzType = this.clazzType;
        if (clazzType == null && valueMap != null) {
            clazzType = (Class<Map>) valueMap.getClass();
        }
        if (clazzType == null) {
            throw new RuntimeException(String.format("Value map type is undefined, either clazzType or value map instance: %s should be provided", valueMap));
        }
        valueMap = (Map<K, Object>) Util.createInstance(clazzType, new Object[]{});
        return valueMap;
    }

}
