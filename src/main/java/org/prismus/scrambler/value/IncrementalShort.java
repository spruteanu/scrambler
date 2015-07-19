package org.prismus.scrambler.value;

/**
 * @author Serge Pruteanu
 */
class IncrementalShort extends Constant<Short> implements ShortArray {
    private static final short DEFAULT_STEP = Integer.valueOf(1).shortValue();
    private short step;

    public IncrementalShort() {
        this(null, DEFAULT_STEP);
    }

    public IncrementalShort(Short value) {
        this(value, DEFAULT_STEP);
    }

    public IncrementalShort(Short value, Short step) {
        super(value);
        this.step = step != null ? step : DEFAULT_STEP;
    }

    public void setStep(short step) {
        this.step = step;
    }

    short next(short value) {
        return Integer.valueOf(value + step).shortValue();
    }

    @Override
    public Short next() {
        Short value = get();
        value = value != null ? next(value) : 0;
        setValue(value);
        return value;
    }

    public void next(short[] values) {
        short start = value == null ? 0 : value;
        for (int i = 0; i < values.length; i++) {
            values[i] = start;
            start = next(start);
        }
        setValue(start);
    }

}
