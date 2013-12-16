package org.prismus.scrambler.property

import groovy.transform.CompileStatic

/**
 * @author Serge Pruteanu
 */
@CompileStatic
abstract class RandomRange<T> extends Generic<T> {
    protected T defaultMinimum
    protected T defaultMaximum
    protected T minimum
    protected T maximum

    RandomRange() {
        super()
    }

    RandomRange(String name) {
        super(name)
    }

    RandomRange(String name, T value) {
        super(name, value)
    }

    RandomRange(String name, T minimum, T maximum) {
        super(name)
        this.minimum = minimum
        this.maximum = maximum
    }

    RandomRange(String name, T value, T minimum, T maximum) {
        super(name, value)
        this.minimum = minimum
        this.maximum = maximum
    }

    void setDefaultMinimum(T defaultMinimum) {
        this.defaultMinimum = defaultMinimum
    }

    void setDefaultMaximum(T defaultMaximum) {
        this.defaultMaximum = defaultMaximum
    }

    void setMinimum(T minimum) {
        this.minimum = minimum
    }

    void setMaximum(T maximum) {
        this.maximum = maximum
    }

    RandomRange<T> usingDefaults(T minimum, T maximum) {
        this.defaultMinimum = minimum
        this.defaultMaximum = maximum
        return this
    }

    RandomRange<T> between(T minimum, T maximum) {
        this.minimum = minimum
        this.maximum = maximum
        return this
    }

    RandomRange<T> withDefaultMinimum(T defaultMinimum) {
        this.defaultMinimum = defaultMinimum
        return this
    }

    RandomRange<T> withDefaultMaximum(T defaultMaximum) {
        this.defaultMaximum = defaultMaximum
        return this
    }

    RandomRange<T> minimumBound(T minimum) {
        this.minimum = minimum
        return this
    }

    RandomRange<T> maximumBound(T maximum) {
        this.maximum = maximum
        return this
    }

    protected void checkBoundaries() {
        if (minimum != null && maximum == null) {
            maximum = defaultMaximum
        }
        if (minimum == null && maximum != null) {
            minimum = defaultMinimum
        }
    }
}
