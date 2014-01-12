package org.prismus.scrambler.property;

import org.prismus.scrambler.Value;

import java.util.Collection;

/**
 * @author Serge Pruteanu
 */
public class ValueCollection<V, T extends Collection<V>> extends Constant<T> {
    private Integer count;
    private Value<V> value;
    private boolean randomCount;

    @SuppressWarnings({"unchecked"})
    public ValueCollection(T collection, Value<V> value) {
        this(collection, null, value);
    }

    public ValueCollection(T collection, Integer count, Value<V> value) {
        super(collection);
        this.count = count;
        this.value = value;
    }

    public void setCount(Integer count) {
        this.count = count;
    }

    public void setValue(Value<V> value) {
        this.value = value;
    }

    public void setRandomCount(boolean randomCount) {
        this.randomCount = randomCount;
    }

    @Override
    public T next() {
        final T value = super.next();
        validateArguments(value, this.value);
        int count = this.count != null ? this.count : 0;
        if (count == 0) {
            count = 20;
        }
        if (randomCount) {
            count = new RandomInteger(count).between(5, count).next();
        }
        checkCreate(count);
        for (int i = 0; i < count; i++) {
            value.add(this.value.next());
        }
        setValue(value);
        return value;
    }

    @SuppressWarnings("unchecked")
    Collection<V> checkCreate(int count) {
        Collection<V> collection = getValue();
        if (collection.size() > 0) {
            collection = (Collection<V>) Util.createInstance(collection.getClass(), new Object[]{count});
        }
        return collection;
    }

    static <V> void validateArguments(Collection<V> value, Value<V> property) {
        if (value == null || property == null) {
            throw new IllegalArgumentException("Collection/property instances should not be null");
        }
    }

}
