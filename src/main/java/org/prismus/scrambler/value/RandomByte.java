package org.prismus.scrambler.value;

import java.util.Random;

/**
 * @author Serge Pruteanu
 */
class RandomByte extends AbstractRandomRange<Byte> implements ByteArray {
    private final java.util.Random random;

    public RandomByte() {
        this(null, null, null);
    }

    public RandomByte(Byte value) {
        this(value, null, null);
    }

    public RandomByte(Byte minimum, Byte maximum) {
        this(null, minimum, maximum);
    }

    public RandomByte(Byte value, Byte minimum, Byte maximum) {
        super(value, minimum, maximum);
        usingDefaults(Integer.valueOf(0).byteValue(), Byte.MAX_VALUE);
        random = new Random();
    }

    @Override
    protected Byte min(Byte val1, Byte val2) {
        return val1 != null ? val2 != null ? val1.compareTo(val2) > 0 ? val2 : val1 : null : null;
    }

    @Override
    protected Byte max(Byte val1, Byte val2) {
        return val1 != null ? val2 != null ? val1.compareTo(val2) > 0 ? val1 : val2 : val1 : val2;
    }

    byte nextValue() {
        final byte result;
        if (minimum != null && maximum != null) {
            result = Integer.valueOf(minimum + random.nextInt(Math.abs(maximum - minimum))).byteValue();
        } else {
            result = Integer.valueOf(value != null ? random.nextInt(Math.abs(value) + 1) : random.nextInt()).byteValue();
        }
        return result;
    }

    @Override
    public Byte get() {
        return value == null ? nextValue() : value;
    }

    @Override
    public Byte next() {
        final Byte newValue = nextValue();
        setValue(newValue);
        return newValue;
    }

    public void next(byte[] values) {
        byte next = nextValue();
        for (int i = 0; i < values.length; i++) {
            next = nextValue();
            values[i] = next;
        }
        setValue(next);
    }

}
