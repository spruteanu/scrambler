package org.prismus.scrambler.value;

/**
 * @author Serge Pruteanu
 */
public abstract class AbstractRandomRange<T> extends Constant<T> {
    protected T defaultMinimum;
    protected T defaultMaximum;
    protected T minimum;
    protected T maximum;

    public AbstractRandomRange() {
        super();
    }

    public AbstractRandomRange(T value) {
        super(value);
    }

    public AbstractRandomRange(T minimum, T maximum) {
        super();
        this.minimum = minimum;
        this.maximum = maximum;
    }

    public AbstractRandomRange(T value, T minimum, T maximum) {
        super(value);
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

    public AbstractRandomRange<T> usingDefaults(T minimum, T maximum) {
        this.defaultMinimum = minimum;
        this.defaultMaximum = maximum;
        return this;
    }

    public AbstractRandomRange<T> between(T minimum, T maximum) {
        this.minimum = minimum;
        this.maximum = maximum;
        return this;
    }

    public AbstractRandomRange<T> withDefaultMinimum(T defaultMinimum) {
        this.defaultMinimum = defaultMinimum;
        return this;
    }

    public AbstractRandomRange<T> withDefaultMaximum(T defaultMaximum) {
        this.defaultMaximum = defaultMaximum;
        return this;
    }

    public AbstractRandomRange<T> minimumBound(T minimum) {
        this.minimum = minimum;
        return this;
    }

    public AbstractRandomRange<T> maximumBound(T maximum) {
        this.maximum = maximum;
        return this;
    }

    protected void checkBoundaries() {
        if (minimum == null && maximum == null) {
            minimum = defaultMinimum;
            maximum = defaultMaximum;
        } else if (minimum != null && maximum == null) {
            maximum = defaultMaximum;
        } else if (minimum == null) {
            minimum = defaultMinimum;
        }
    }
}
