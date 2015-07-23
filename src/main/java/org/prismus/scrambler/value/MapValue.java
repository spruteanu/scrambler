package org.prismus.scrambler.value;

import org.prismus.scrambler.Value;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * todo: add description
 *
 * @author Serge Pruteanu
 */
public class MapValue<K> extends Constant<Map<K, Object>> {

    private Map<K, Value> keyValueMap = new LinkedHashMap<K, Value>();
    private Class<Map<K, Object>> clazzType;

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

    public MapValue(Class<Map<K, Object>> clazzType) {
        this(clazzType, null);
    }

    public MapValue(Class<Map<K, Object>> clazzType, Map<K, Value> keyValueMap) {
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
        Class<Map<K, Object>> clazzType = this.clazzType;
        if (clazzType == null && valueMap != null) {
            clazzType = (Class<Map<K, Object>>) valueMap.getClass();
        }
        if (clazzType == null) {
            throw new RuntimeException(String.format("Value map type is undefined, either clazzType or value map instance: %s should be provided", valueMap));
        }
        valueMap = (Map<K, Object>) Util.createInstance(clazzType, new Object[]{});
        return valueMap;
    }

}
