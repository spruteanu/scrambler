package org.prismus.scrambler.property;

import java.util.Random;

/**
 * @author Serge Pruteanu
 */
public class RandomBoolean extends Constant<Boolean> {
    public RandomBoolean() {
        super();
    }

    public RandomBoolean(Boolean value) {
        super(value);
    }

    @Override
    public Boolean next() {
        final boolean newValue = new Random().nextBoolean();
        setValue(newValue);
        return newValue;
    }
}
