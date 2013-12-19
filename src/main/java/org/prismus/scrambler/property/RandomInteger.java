package org.prismus.scrambler.property;

import java.util.Random;

/**
 * @author Serge Pruteanu
 */
public class RandomInteger extends AbstractRandomRange<Integer> {
    public RandomInteger() {
        this(null);
    }

    public RandomInteger(Integer value) {
        super(value);
        usingDefaults(0, Integer.MAX_VALUE);
    }

    public RandomInteger(Integer minimum, Integer maximum) {
        super(minimum, maximum);
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
