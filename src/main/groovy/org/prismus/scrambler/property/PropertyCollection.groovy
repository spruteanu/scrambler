package org.prismus.scrambler.property

import org.prismus.scrambler.Property

import java.util.ArrayList
import java.util.Collection

/**
 * @author Serge Pruteanu
 */
class PropertyCollection<V, T extends Collection<V>> extends Generic<T> {
    private int count
    private Property<V> property
    private boolean randomCount

    PropertyCollection() {
        this(null, null, 0)
    }

    PropertyCollection(String name) {
        this(name, null, 0)
    }

    PropertyCollection(String name, Property<V> property) {
        this(name, property, 0)
    }

    PropertyCollection(String name, Property<V> property, int count) {
        this(name, ((T) new ArrayList<V>()), count, property)
    }

    PropertyCollection(String name, T collection, Property<V> property) {
        this(name, ((T) new ArrayList<V>()), 0, property)
    }

    PropertyCollection(String name, T collection, int count, Property<V> property) {
        super(name, collection)
        this.count = count
        this.property = property
        randomCount = count == 0
    }

    void setCount(int count) {
        this.count = count
    }

    void setProperty(Property<V> property) {
        this.property = property
    }

    void setRandomCount(boolean randomCount) {
        this.randomCount = randomCount
    }

    @Override
    T value() {
        final T value = super.value()
        validateArguments(value, property)
        int count = this.count
        if (randomCount) {
            if (count == 0) {
                count = 10
            }
            count = new RandomInteger("count", count).between(0, count).value()
        }
        for (int i = 0; i < count; i++) {
            value.add(property.value())
        }
        return value
    }

    void validateArguments(Collection value, Property property) {
        if (value == null || property == null) {
            throw new IllegalArgumentException("Collection/property instances should not be null")
        }
    }
}
