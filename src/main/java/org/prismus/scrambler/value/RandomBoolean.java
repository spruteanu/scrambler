package org.prismus.scrambler.value;

import java.util.Random;

/**
 * @author Serge Pruteanu
 */
public class RandomBoolean extends Constant<Boolean> {

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

    public boolean[] next(int count) {
        final boolean[] values = new boolean[count];
        for (int i = 0; i < values.length; i++) {
            values[i] = nextValue();
        }
        return values;
    }

}
