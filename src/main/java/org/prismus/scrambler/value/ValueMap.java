package org.prismus.scrambler.value;

import org.prismus.scrambler.Value;

import java.util.Map;

/**
 * todo: add description
 *
 * @author Serge Pruteanu
 */
public class ValueMap<K, V> extends Constant<Map<K, V>> {

    private Value<K> entryKey;
    private Value<V> entryValue;

    private Integer count;

    public ValueMap(Map<K, V> value, Value<K> entryKey, Value<V> entryValue) {
        this(value, 0, entryKey, entryValue);
    }

    public ValueMap(Map<K, V> value, Integer count, Value<K> entryKey, Value<V> entryValue) {
        this(value, entryKey, entryValue, count);
    }

    public ValueMap(Map<K, V> value, Value<K> entryKey, Value<V> entryValue, Integer count) {
        super(value);
        this.entryKey = entryKey;
        this.entryValue = entryValue;
        this.count = count;
    }

    @Override
    public Map<K, V> next() {
        Integer count = this.count;
        if (count == null) {
            count = new RandomInteger(1).between(5, 20).next();
        }
        final Map<K, V> kvMap = checkCreate(count);
        for (int i = 0; i < count; i++) {
            kvMap.put(entryKey.next(), entryValue.next());
        }
        setValue(kvMap);
        return kvMap;
    }

    @SuppressWarnings("unchecked")
    Map<K, V> checkCreate(int count) {
        Map<K, V> valueMap = get();
        if (valueMap.size() > 0) {
            valueMap = (Map<K, V>) Util.createInstance(valueMap.getClass(), new Object[]{count});
        }
        return valueMap;
    }

    public void setCount(int count) {
        this.count = count;
    }
}
