package org.prismus.scrambler.value;

/**
 * @author Serge Pruteanu
 */
public class IncrementalShort extends Constant<Short> {
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
        Short value = super.next();
        value = value != null ? next(value) : 0;
        setValue(value);
        return value;
    }

    public short[] next(int count) {
        final short[] values = new short[count];
        short start = value == null ? 0 : value;
        for (int i = 0; i < values.length; i++) {
            final short next = next(start);
            values[i] = next;
            start = next;
        }
        setValue(start);
        return values;
    }

    public short getStep() {
        return step;
    }

}
