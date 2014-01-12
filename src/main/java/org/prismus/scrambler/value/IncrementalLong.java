package org.prismus.scrambler.value;

/**
 * @author Serge Pruteanu
 */
public class IncrementalLong extends Constant<Long> {
    private static final long DEFAULT_STEP = 1L;
    private long step;

    public IncrementalLong() {
        this(null, DEFAULT_STEP);
    }

    public IncrementalLong(Long value) {
        this(value, DEFAULT_STEP);
    }

    public IncrementalLong(Long value, Long step) {
        super(value);
        this.step = step != null ? step : DEFAULT_STEP;
    }

    public void setStep(long step) {
        this.step = step;
    }

    @Override
    public Long next() {
        Long value = super.next();
        value = value != null ? value + step : step;
        setValue(value);
        return value;
    }
}
