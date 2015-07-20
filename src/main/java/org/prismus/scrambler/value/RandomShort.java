package org.prismus.scrambler.value;

import java.util.Random;

/**
 * @author Serge Pruteanu
 */
class RandomShort extends AbstractRandomRange<Short> implements ShortArray {

    private final java.util.Random random;

    public RandomShort() {
        this(null, null, null);
    }

    public RandomShort(Short value) {
        this(value, null, null);
    }

    public RandomShort(Short minimum, Short maximum) {
        this(null, minimum, maximum);
    }

    public RandomShort(Short value, Short minimum, Short maximum) {
        super(value, minimum, maximum);
        usingDefaults(Integer.valueOf(0).shortValue(), Short.MAX_VALUE);
        random = new Random();
    }

    @Override
    protected Short min(Short val1, Short val2) {
        return val1 != null ? val2 != null ? val1.compareTo(val2) > 0 ? val2 : val1 : null : null;
    }

    @Override
    protected Short max(Short val1, Short val2) {
        return val1 != null ? val2 != null ? val1.compareTo(val2) > 0 ? val1 : val2 : val1 : val2;
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
    public Short get() {
        return value == null ? nextValue() : value;
    }

    @Override
    public Short next() {
        final Short newValue = nextValue();
        setValue(newValue);
        return newValue;
    }

    public void next(short[] values) {
        short next = nextValue();
        for (int i = 0; i < values.length; i++) {
            next = nextValue();
            values[i] = next;
        }
        setValue(next);
    }

}
