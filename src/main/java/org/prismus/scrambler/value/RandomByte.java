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
