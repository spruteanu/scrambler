package org.prismus.scrambler.value;

/**
 * @author Serge Pruteanu
 */
class IncrementalByte extends Constant<Byte> implements ByteArray {
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

    byte next(byte value) {
        return Integer.valueOf(value + step).byteValue();
    }

    @Override
    public Byte next() {
        Byte value = get();
        value = value != null ? next(value) : 0;
        setValue(value);
        return value;
    }

    public void next(byte[] values) {
        byte start = value == null ? 0 : value;
        for (int i = 0; i < values.length; i++) {
            values[i] = start;
            start = next(start);
        }
        setValue(start);
    }

}
