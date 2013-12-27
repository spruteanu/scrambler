package org.prismus.scrambler.property;

import org.prismus.scrambler.Value;

import java.util.Collection;

/**
 * @author Serge Pruteanu
 */
public class ValueCollection<V, T extends Collection<V>> extends Constant<T> {
    private int count;
    private Value<V> value;
    private boolean randomCount;

    @SuppressWarnings({"unchecked"})
    public ValueCollection(T collection, Value<V> value) {
        this(collection, 0, value);
    }

    public ValueCollection(T collection, int count, Value<V> value) {
        super(collection);
        this.count = count;
        this.value = value;
        randomCount = count == 0;
    }

    public void setCount(int count) {
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
        int count = this.count;
        if (randomCount) {
            if (count == 0) {
                count = 10;
            }
            count = new RandomInteger(count).between(0, count).next();
        }
        for (int i = 0; i < count; i++) {
            value.add(this.value.next());
        }
        return value;
    }

    static <V> void validateArguments(Collection<V> value, Value<V> property) {
        if (value == null || property == null) {
            throw new IllegalArgumentException("Collection/property instances should not be null");
        }
    }

}
