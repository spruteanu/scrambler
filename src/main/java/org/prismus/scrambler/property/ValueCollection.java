package org.prismus.scrambler.property;

import org.prismus.scrambler.Property;

import java.util.Collection;

/**
 * @author Serge Pruteanu
 */
public class ValueCollection<V, T extends Collection<V>> extends Constant<T> {
    private int count;
    private Property<V> property;
    private boolean randomCount;

    @SuppressWarnings({"unchecked"})
    public ValueCollection(String name, T collection, Property<V> property) {
        this(name, collection, 0, property);
    }

    public ValueCollection(String name, T collection, int count, Property<V> property) {
        super(name, collection);
        this.count = count;
        this.property = property;
        randomCount = count == 0;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public void setProperty(Property<V> property) {
        this.property = property;
    }

    public void setRandomCount(boolean randomCount) {
        this.randomCount = randomCount;
    }

    @Override
    public T value() {
        final T value = super.value();
        validateArguments(value, property);
        int count = this.count;
        if (randomCount) {
            if (count == 0) {
                count = 10;
            }
            count = new RandomInteger("count", count).between(0, count).value();
        }
        for (int i = 0; i < count; i++) {
            value.add(property.value());
        }
        return value;
    }

    static <V> void validateArguments(Collection<V> value, Property<V> property) {
        if (value == null || property == null) {
            throw new IllegalArgumentException("Collection/property instances should not be null");
        }
    }
}
