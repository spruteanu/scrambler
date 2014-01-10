package org.prismus.scrambler.property;

import org.prismus.scrambler.Value;

import java.util.Collection;

/**
 * @author Serge Pruteanu
 */
public class ValueCollection<V, T extends Collection<V>> extends Constant<T> {
    private int count;
    private Value<V> value;

    @SuppressWarnings({"unchecked"})
    public ValueCollection(T collection, Value<V> value) {
        this(collection, 0, value);
    }

    public ValueCollection(T collection, int count, Value<V> value) {
        super(collection);
        this.count = count;
        this.value = value;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public void setValue(Value<V> value) {
        this.value = value;
    }

    @Override
    public T next() {
        final T value = super.next();
        validateArguments(value, this.value);
        int count = this.count;
        if (count == 0) {
            count = new RandomInteger(count).between(0, 100).next();
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
