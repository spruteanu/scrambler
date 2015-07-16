package org.prismus.scrambler.value;

import org.prismus.scrambler.Value;

import java.util.Collection;

/**
 * @author Serge Pruteanu
 */
public class ValueCollection<V, T extends Collection<V>> extends Constant<T> {
    private Integer count;
    private Value<V> instance;
    private Boolean randomCount;

    @SuppressWarnings({"unchecked"})
    public ValueCollection(T collection, Value<V> value) {
        this(collection, null, value);
    }

    public ValueCollection(T collection, Integer count, Value<V> value) {
        this(collection, count, value, null);
    }

    public ValueCollection(T value, Integer count, Value<V> value1, Boolean randomCount) {
        super(value);
        this.count = count;
        this.instance = value1;
        this.randomCount = randomCount;
    }

    public void setCount(Integer count) {
        this.count = count;
    }

    public void setRandomCount(Boolean randomCount) {
        this.randomCount = randomCount;
    }

    public Value<V> getInstance() {
        return instance;
    }

    public void setInstance(Value<V> instance) {
        this.instance = instance;
    }

    @Override
    public T next() {
        final T value = super.next();
        final Value<V> valueInstance = instance;
        Util.validateArguments(value, valueInstance);
        int count = this.count != null ? this.count : 0;
        if (count == 0) {
            count = 20;
        }
        if (randomCount != null && randomCount) {
            count = new RandomInteger(count).between(1, count).next();
        }
        checkCreate(count);
        for (int i = 0; i < count; i++) {
            value.add(valueInstance.next());
        }
        setValue(value);
        return value;
    }

    @SuppressWarnings("unchecked")
    Collection<V> checkCreate(int count) {
        Collection<V> collection = get();
        if (collection.size() > 0) {
            collection = (Collection<V>) Util.createInstance(collection.getClass(), new Object[]{count});
        }
        return collection;
    }

}
