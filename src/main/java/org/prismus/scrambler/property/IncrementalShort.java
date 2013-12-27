package org.prismus.scrambler.property;

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

    @Override
    public Short next() {
        Short value = super.next();
        value = value != null ? Integer.valueOf(value + step).shortValue() : step;
        setValue(value);
        return value;
    }

}
