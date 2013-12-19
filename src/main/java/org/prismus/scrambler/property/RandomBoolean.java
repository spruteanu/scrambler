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
    public Boolean value() {
        return new Random().nextBoolean();
    }
}
