package org.prismus.scrambler.value;

import org.prismus.scrambler.Value;

import java.lang.reflect.Array;

/**
 * @author Serge Pruteanu
 */
public class ValueArray<T> extends Constant<T[]> {
    private Integer count;
    private Value<T> value;
    private Boolean randomCount;

    @SuppressWarnings({"unchecked"})
    public ValueArray(T[] array, Value<T> value) {
        this(array, null, value);
    }

    public ValueArray(T[] array, Integer count, Value<T> value) {
        this(array, count, value, null);
    }

    public ValueArray(T[] array, Integer count, Value<T> value1, Boolean randomCount) {
        super(array);
        this.count = count;
        this.value = value1;
        this.randomCount = randomCount;
    }

    public void setCount(Integer count) {
        this.count = count;
    }

    public void setValue(Value<T> value) {
        this.value = value;
    }

    public void setRandomCount(Boolean randomCount) {
        this.randomCount = randomCount;
    }

    @Override
    public T[] next() {
        final T[] value = super.next();
        final Value<T> valueInstance = this.value;
        validateArguments(value, valueInstance);
        int count = this.count != null ? this.count : 0;
        if (count == 0) {
            count = 20;
        }
        if (randomCount != null && randomCount) {
            count = new RandomInteger(count).between(0, count).next();
        }
        checkCreate(count);
        for (int i = 0; i < count; i++) {
            value[i] = valueInstance.next();
        }
        setValue(value);
        return value;
    }

    @SuppressWarnings("unchecked")
    T[] checkCreate(int count) {
        T[] array = getValue();
        if (array.length != count || (array.length > 0 && array[0] != null)) {
            array = (T[]) Array.newInstance(array.getClass().getComponentType(), count);
        }
        return array;
    }

    static <T> void validateArguments(T[] array, Value<T> property) {
        if (array == null || property == null) {
            throw new IllegalArgumentException("Collection/property instances should not be null");
        }
    }

}
