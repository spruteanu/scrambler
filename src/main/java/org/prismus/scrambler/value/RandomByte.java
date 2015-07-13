package org.prismus.scrambler.value;

import java.util.Random;

/**
 * @author Serge Pruteanu
 */
public class RandomByte extends AbstractRandomRange<Byte> {
    private final java.util.Random random;

    public RandomByte() {
        this(null);
    }

    public RandomByte(Byte value) {
        super(value);
        usingDefaults(Integer.valueOf(0).byteValue(), Byte.MAX_VALUE);
        random = new Random();
    }

    public RandomByte(Byte minimum, Byte maximum) {
        super(minimum, maximum);
        usingDefaults(Integer.valueOf(0).byteValue(), Byte.MAX_VALUE);
        random = new Random();
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

    public byte[] next(int count) {
        final byte[] values = new byte[count];
        byte next = nextValue();
        for (int i = 0; i < values.length; i++) {
            next = nextValue();
            values[i] = next;
        }
        setValue(next);
        return values;
    }

}
