package org.prismus.scrambler.value;

/**
 * @author Serge Pruteanu
 */
public class IncrementalLong extends Constant<Long> implements LongArray {
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

    public void next(long[] values) {
        long start = value == null ? 0 : value;
        for (int i = 0; i < values.length; i++) {
            final long next = start + step;
            values[i] = next;
            start = next;
        }
        setValue(start);
    }

}
