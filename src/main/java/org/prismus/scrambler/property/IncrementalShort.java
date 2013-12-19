package org.prismus.scrambler.property;

/**
 * @author Serge Pruteanu
 */
public class IncrementalShort extends Constant<Short> {
    private static final short DEFAULT_STEP = Integer.valueOf(1).shortValue();
    private short step;

    public IncrementalShort() {
        this(null, null, DEFAULT_STEP);
    }

    public IncrementalShort(String name) {
        this(name, null, DEFAULT_STEP);
    }

    public IncrementalShort(String name, Short value) {
        this(name, value, DEFAULT_STEP);
    }

    public IncrementalShort(String name, Short value, Short step) {
        super(name, value);
        this.step = step != null ? step : DEFAULT_STEP;
    }

    public void setStep(short step) {
        this.step = step;
    }

    @Override
    public Short value() {
        Short value = super.value();
        value = value != null ? Integer.valueOf(value + step).shortValue() : step;
        setValue(value);
        return value;
    }
}
