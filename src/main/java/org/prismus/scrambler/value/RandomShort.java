package org.prismus.scrambler.value;

import java.util.Random;

/**
 * @author Serge Pruteanu
 */
public class RandomShort extends AbstractRandomRange<Short> {

    private final java.util.Random random;

    public RandomShort() {
        this(null);
    }

    public RandomShort(Short value) {
        super(value);
        usingDefaults(Integer.valueOf(0).shortValue(), Short.MAX_VALUE);
        random = new Random();
    }

    public RandomShort(Short minimum, Short maximum) {
        super(minimum, maximum);
        usingDefaults(Integer.valueOf(0).shortValue(), Short.MAX_VALUE);
        random = new Random();
    }

    public RandomShort(Short value, Short minimum, Short maximum) {
        super(value, minimum, maximum);
        usingDefaults(Integer.valueOf(0).shortValue(), Short.MAX_VALUE);
        random = new Random();
    }

    short nextValue() {
        final short result;
        if (minimum != null && maximum != null) {
            result = Integer.valueOf(minimum + random.nextInt(Math.abs(maximum - minimum))).shortValue();
        } else {
            result = Integer.valueOf(value != null ? random.nextInt(Math.abs(value) + 1) : random.nextInt()).shortValue();
        }
        return result;
    }

    @Override
    public Short next() {
        final Short newValue = nextValue();
        setValue(newValue);
        return newValue;
    }

    public short[] next(int count) {
        final short[] values = new short[count];
        short next = nextValue();
        for (int i = 0; i < values.length; i++) {
            next = nextValue();
            values[i] = next;
        }
        setValue(next);
        return values;
    }

}
