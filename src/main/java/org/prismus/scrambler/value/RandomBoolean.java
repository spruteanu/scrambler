package org.prismus.scrambler.value;

import java.util.Random;

/**
 * @author Serge Pruteanu
 */
class RandomBoolean extends Constant<Boolean> {

    private final Random random;

    public RandomBoolean() {
        super();
        random = new Random();
    }

    public RandomBoolean(Boolean value) {
        super(value);
        random = new Random();
    }

    boolean nextValue() {
        return random.nextBoolean();
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
