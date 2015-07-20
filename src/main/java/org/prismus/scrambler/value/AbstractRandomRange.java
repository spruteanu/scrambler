package org.prismus.scrambler.value;

/**
 * @author Serge Pruteanu
 */
abstract class AbstractRandomRange<T> extends Constant<T> {
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
        this(null, minimum, maximum);
    }

    public AbstractRandomRange(T value, T minimum, T maximum) {
        super(value);
        between(minimum, maximum);
    }

    public AbstractRandomRange<T> usingDefaults(T minimum, T maximum) {
        this.defaultMinimum = min(minimum, maximum);
        this.defaultMaximum = max(minimum, maximum);
        return this;
    }

    public AbstractRandomRange<T> between(T minimum, T maximum) {
        this.minimum = min(minimum, maximum);
        this.maximum = max(minimum, maximum);
        return this;
    }

    protected abstract T min(T val1, T val2);

    protected abstract T max(T val1, T val2);

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
