package org.prismus.scrambler.property;

import java.util.Random;

/**
 * @author Serge Pruteanu
 */
public class RandomInteger extends RandomRange<Integer> {
    public RandomInteger() {
        this(null, null);
    }

    public RandomInteger(String name) {
        this(name, null);
    }

    public RandomInteger(String name, Integer value) {
        super(name, value);
        usingDefaults(0, Integer.MAX_VALUE);
    }

    public RandomInteger(String name, Integer minimum, Integer maximum) {
        super(name, minimum, maximum);
        usingDefaults(0, Integer.MAX_VALUE);
    }

    @Override
    public Integer value() {
        checkBoundaries();
        Integer value = super.value();
        final Random random = new Random();
        if (minimum != null && maximum != null) {
            value = random.nextInt(maximum - minimum) + 1;
        } else {
            value = value != null ? random.nextInt(Math.abs(value) + 1) : random.nextInt();
        }
        return value;
    }
}
