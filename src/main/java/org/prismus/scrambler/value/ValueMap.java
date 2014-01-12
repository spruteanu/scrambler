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
    private Boolean randomCount;

    public ValueMap(Map<K, V> value, Value<K> entryKey, Value<V> entryValue) {
        this(value, 0, entryKey, entryValue);
    }

    public ValueMap(Map<K, V> value, Integer count, Value<K> entryKey, Value<V> entryValue) {
        this(value, entryKey, entryValue, count, null);
    }

    public ValueMap(Map<K, V> value,
                    Value<K> entryKey, Value<V> entryValue,
                    Integer count, Boolean randomCount) {
        super(value);
        this.entryKey = entryKey;
        this.entryValue = entryValue;
        this.count = count;
        this.randomCount = randomCount;
    }

    public void setRandomCount(Boolean randomCount) {
        this.randomCount = randomCount;
    }

    @Override
    public Map<K, V> next() {
        int count = this.count != null ? this.count : 0;
        if (count == 0) {
            count = 20;
        }
        if (randomCount != null && randomCount) {
            count = new RandomInteger(count).between(5, count).next();
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
        Map<K, V> valueMap = getValue();
        if (valueMap.size() > 0) {
            valueMap = (Map<K, V>) Util.createInstance(valueMap.getClass(), new Object[]{count});
        }
        return valueMap;
    }

    public Value<K> getEntryKey() {
        return entryKey;
    }

    public void setEntryKey(Value<K> entryKey) {
        this.entryKey = entryKey;
    }

    public Value<V> getEntryValue() {
        return entryValue;
    }

    public void setEntryValue(Value<V> entryValue) {
        this.entryValue = entryValue;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }
}
