package org.prismus.scrambler.value;

import org.prismus.scrambler.Value;

import java.lang.reflect.Array;

/**
 * todo: add description
 *
 * @author Serge Pruteanu
 */
public class ValueArray<T> extends Constant<T[]> {
    private Integer count;
    private Value<T> instance;
    private Boolean randomCount;
    private Class<T> valueType;

    public ValueArray() {
    }

    public ValueArray(T[] array, Value<T> value) {
        this(array, null, value);
    }

    public ValueArray(Class<T> valueType, Value<T> value) {
        this(valueType, null, value);
    }

    public ValueArray(T[] array, Integer count, Value<T> value) {
        this(array, count, value, null);
    }

    public ValueArray(Class<T> valueType, Integer count, Value<T> value) {
        this(valueType, count, value, null);
    }

    public ValueArray(T[] array, Integer count, Value<T> value1, Boolean randomCount) {
        super(array);
        this.count = count;
        this.instance = value1;
        this.randomCount = randomCount;
    }

    public ValueArray(Class<T> valueType, Integer count, Value<T> value1, Boolean randomCount) {
        super(null);
        this.valueType = valueType;
        this.count = count;
        this.instance = value1;
        this.randomCount = randomCount;
    }

    @SuppressWarnings("unchecked")
    public ValueArray asType(Class<T> valueType) {
        this.valueType = valueType.isArray() ? (Class<T>) valueType.getComponentType() : valueType;
        return this;
    }

    @Override
    public T[] next() {
        T[] value = super.next();
        Util.validateArguments(valueType, value, instance);
        int count = this.count != null ? this.count : 0;
        if (count == 0) {
            count = 20;
        }
        if (randomCount != null && randomCount) {
            count = new RandomInteger(count).between(0, count).next();
        }
        value = checkCreate(value, count);
        for (int i = 0; i < count; i++) {
            value[i] = instance.next();
        }

        setValue(value);
        return value;
    }

    @SuppressWarnings("unchecked")
    public T[] checkCreate(T[] array, int count) {
        Class type = valueType;
        if (type == null) {
            if (array.length != count || (array.length > 0 && array[0] != null)) {
                type = array.getClass();
            } else {
                return array;
            }
        }
        if (type.isArray()) {
            type = type.getComponentType();
        }
        array = (T[]) Array.newInstance(type, count);
        return array;
    }

    public Integer getCount() {
        return count;
    }

    public void setCount(Integer count) {
        this.count = count;
    }

    public Value getInstance() {
        return instance;
    }

    public void setInstance(Value<T> instance) {
        this.instance = instance;
    }

    public Boolean getRandomCount() {
        return randomCount;
    }

    public void setRandomCount(Boolean randomCount) {
        this.randomCount = randomCount;
    }

    public Class getValueType() {
        return valueType;
    }

    public void setValueType(Class<T> valueType) {
        this.valueType = valueType;
    }

}
