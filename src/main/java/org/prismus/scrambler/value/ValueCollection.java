package org.prismus.scrambler.value;

import org.prismus.scrambler.Value;

import java.util.Collection;

/**
 * @author Serge Pruteanu
 */
public class ValueCollection<V, T extends Collection<V>> extends Constant<T> {
    private Integer count;
    private Value<V> instance;

    @SuppressWarnings({"unchecked"})
    public ValueCollection(T collection, Value<V> value) {
        this(collection, null, value);
    }

    public ValueCollection(T value, Integer count, Value<V> value1) {
        super(value);
        this.count = count;
        this.instance = value1;
    }

    public void setCount(Integer count) {
        this.count = count;
    }

    public Value<V> getInstance() {
        return instance;
    }

    public void setInstance(Value<V> instance) {
        this.instance = instance;
    }

    @Override
    public T next() {
        Integer count = this.count;
        if (count == null) {
            count = new RandomInteger(1).between(1, 20).next();
        }
        final T value = checkCreate(count);
        for (int i = 0; i < count; i++) {
            value.add(instance.next());
        }
        setValue(value);
        return value;
    }

    @SuppressWarnings("unchecked")
    T checkCreate(int count) {
        T collection = get();
        if (collection.size() > 0) {
            collection = (T) Util.createInstance(collection.getClass(), new Object[]{count});
        }
        return collection;
    }

}
