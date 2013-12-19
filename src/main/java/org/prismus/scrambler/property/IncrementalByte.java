package org.prismus.scrambler.property;

/**
 * @author Serge Pruteanu
 */
public class IncrementalByte extends Constant<Byte> {
    private static final byte DEFAULT_STEP = Integer.valueOf(1).byteValue();
    private byte step;

    public IncrementalByte() {
        this(null, null, DEFAULT_STEP);
    }

    public IncrementalByte(String name) {
        this(name, null, DEFAULT_STEP);
    }

    public IncrementalByte(String name, Byte value) {
        this(name, value, DEFAULT_STEP);
    }

    public IncrementalByte(String name, Byte value, Byte step) {
        super(name, value);
        this.step = step != null ? step : DEFAULT_STEP;
    }

    public void setStep(byte step) {
        this.step = step;
    }

    @Override
    public Byte value() {
        Byte value = super.value();
        value = value != null ? Integer.valueOf(value + step).byteValue() : step;
        setValue(value);
        return value;
    }
}
