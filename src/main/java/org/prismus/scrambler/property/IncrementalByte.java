package org.prismus.scrambler.property;

/**
 * @author Serge Pruteanu
 */
public class IncrementalByte extends Constant<Byte> {
    private static final byte DEFAULT_STEP = Integer.valueOf(1).byteValue();
    private byte step;

    public IncrementalByte() {
        this(null, DEFAULT_STEP);
    }

    public IncrementalByte(Byte value) {
        this(value, DEFAULT_STEP);
    }

    public IncrementalByte(Byte value, Byte step) {
        super(value);
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
