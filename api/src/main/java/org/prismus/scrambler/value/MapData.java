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

import org.prismus.scrambler.Data;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Data instance that allows to create a map of values using provided values strategy (@code keyValueMap)
 *
 * @author Serge Pruteanu
 */
public class MapData<K> extends ConstantData<Map<K, Object>> {

    private Map<K, Data> keyValueMap = new LinkedHashMap<K, Data>();
    private Class<Map> clazzType;

    public MapData() {
        this(new LinkedHashMap<K, Object>());
    }

    public MapData(Map<K, Object> value) {
        super(value);
    }

    public MapData(Map<K, Object> value, Map<K, Data> keyValueMap) {
        super(value);
        this.keyValueMap = keyValueMap;
    }

    public MapData(Class<Map> clazzType) {
        this(clazzType, null);
    }

    public MapData(Class<Map> clazzType, Map<K, Data> keyValueMap) {
        super(null);
        this.clazzType = clazzType;
        this.keyValueMap = keyValueMap;
    }

    public MapData<K> of(K key, Data data) {
        keyValueMap.put(key, data);
        return this;
    }

    public MapData<K> usingValueMap(Map<K, Data> keyValueMap) {
        this.keyValueMap = keyValueMap;
        return this;
    }

    @Override
    protected Map<K, Object> doNext() {
        final Map<K, Object> kvMap = checkCreate();
        for (Map.Entry<K, Data> entry : keyValueMap.entrySet()) {
            kvMap.put(entry.getKey(), entry.getValue().next());
        }
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
            throw new RuntimeException(String.format("Data map type is undefined, either clazzType or value map instance: %s should be provided", valueMap));
        }
        valueMap = (Map<K, Object>) Util.createInstance(clazzType, new Object[]{});
        return valueMap;
    }

}
