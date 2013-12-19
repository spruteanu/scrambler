package org.prismus.scrambler.property;

/**
 * @author Serge Pruteanu
 */
public class IncrementalLong extends Constant<Long> {
    private static final long DEFAULT_STEP = 1L;
    private long step;

    public IncrementalLong() {
        this(null, null, DEFAULT_STEP);
    }

    public IncrementalLong(String name) {
        this(name, null, DEFAULT_STEP);
    }

    public IncrementalLong(String name, Long value) {
        this(name, value, DEFAULT_STEP);
    }

    public IncrementalLong(String name, Long value, Long step) {
        super(name, value);
        this.step = step != null ? step : DEFAULT_STEP;
    }

    public void setStep(long step) {
        this.step = step;
    }

    @Override
    public Long value() {
        Long value = super.value();
        value = value != null ? value + step : step;
        setValue(value);
        return value;
    }
}
