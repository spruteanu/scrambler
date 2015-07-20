package org.prismus.scrambler.value;

import java.util.Random;

/**
 * @author Serge Pruteanu
 */
class RandomBoolean extends Constant<Boolean> implements BooleanArray {

    private final Random random;

    public RandomBoolean() {
        this(null);
    }

    public RandomBoolean(Boolean value) {
        super(value);
        random = new Random();
    }

    boolean nextValue() {
        return random.nextBoolean();
    }

    @Override
    public Boolean get() {
        return value == null ? nextValue() : value;
    }

    @Override
    public Boolean next() {
        final boolean newValue = nextValue();
        setValue(newValue);
        return newValue;
    }

    public void next(boolean[] values) {
        for (int i = 0; i < values.length; i++) {
            values[i] = nextValue();
        }
    }

}
