package org.prismus.scrambler.property;

/**
 * @author Serge Pruteanu
 */
public abstract class RandomRange<T> extends Generic<T> {
    protected T defaultMinimum;
    protected T defaultMaximum;
    protected T minimum;
    protected T maximum;

    public RandomRange() {
        super();
    }

    public RandomRange(String name) {
        super(name);
    }

    public RandomRange(String name, T value) {
        super(name, value);
    }

    public RandomRange(String name, T minimum, T maximum) {
        super(name);
        this.minimum = minimum;
        this.maximum = maximum;
    }

    public RandomRange(String name, T value, T minimum, T maximum) {
        super(name, value);
        this.minimum = minimum;
        this.maximum = maximum;
    }

    public void setDefaultMinimum(T defaultMinimum) {
        this.defaultMinimum = defaultMinimum;
    }

    public void setDefaultMaximum(T defaultMaximum) {
        this.defaultMaximum = defaultMaximum;
    }

    public void setMinimum(T minimum) {
        this.minimum = minimum;
    }

    public void setMaximum(T maximum) {
        this.maximum = maximum;
    }

    public RandomRange<T> usingDefaults(T minimum, T maximum) {
        this.defaultMinimum = minimum;
        this.defaultMaximum = maximum;
        return this;
    }

    public RandomRange<T> between(T minimum, T maximum) {
        this.minimum = minimum;
        this.maximum = maximum;
        return this;
    }

    public RandomRange<T> withDefaultMinimum(T defaultMinimum) {
        this.defaultMinimum = defaultMinimum;
        return this;
    }

    public RandomRange<T> withDefaultMaximum(T defaultMaximum) {
        this.defaultMaximum = defaultMaximum;
        return this;
    }

    public RandomRange<T> minimumBound(T minimum) {
        this.minimum = minimum;
        return this;
    }

    public RandomRange<T> maximumBound(T maximum) {
        this.maximum = maximum;
        return this;
    }

    protected void checkBoundaries() {
        if (minimum != null && maximum == null) {
            maximum = defaultMaximum;
        }
        if (minimum == null && maximum != null) {
            minimum = defaultMinimum;
        }
    }
}
