package org.prismus.scrambler.value;

import org.prismus.scrambler.Value;

import java.util.Collection;

/**
 * Value instance that allows to create collection of values using provided value instance strategy (@code instance)
 *
 * @author Serge Pruteanu
 */
public class CollectionValue<V, T extends Collection<V>> extends Constant<T> {
    private Integer count;
    private Value<V> instance;
    private Class<T> clazzType;

    public CollectionValue(T collection, Value<V> value) {
        this(collection, value, null);
    }

    public CollectionValue(T value, Value<V> value1, Integer count) {
        super(value);
        this.instance = value1;
        this.count = count;
    }

    public CollectionValue(Class<T> clazzType, Value<V> value1, Integer count) {
        super(null);
        this.clazzType = clazzType;
        this.instance = value1;
        this.count = count;
    }

    public CollectionValue<V, T> count(Integer count) {
        this.count = count;
        return this;
    }

    public void setCount(Integer count) {
        this.count = count;
    }

    public Value<V> getInstance() {
        return instance;
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
        Class<T> clazzType = this.clazzType;
        if (clazzType == null && collection != null) {
            clazzType = (Class<T>) collection.getClass();
        }
        if (clazzType == null) {
            throw new RuntimeException(String.format("Value map type is undefined, either clazzType or collection instance: %s should be provided", collection));
        }

        collection = (T) Util.createInstance(clazzType, new Object[]{});
        return collection;
    }

}
