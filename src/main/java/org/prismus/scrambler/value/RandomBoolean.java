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

    @Override
    public Boolean next() {
        final boolean newValue = random.nextBoolean();
        setValue(newValue);
        return newValue;
    }

}
