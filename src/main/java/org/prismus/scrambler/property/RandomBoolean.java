package org.prismus.scrambler.property;

import java.util.Random;

/**
 * @author Serge Pruteanu
 */
public class RandomBoolean extends Generic<Boolean> {
    public RandomBoolean() {
        super();
    }

    public RandomBoolean(String name) {
        this(name, null);
    }

    public RandomBoolean(String name, Boolean value) {
        super(name, value);
    }

    @Override
    public Boolean value() {
        return new Random().nextBoolean();
    }
}
