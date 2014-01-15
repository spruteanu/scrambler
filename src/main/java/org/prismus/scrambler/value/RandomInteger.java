package org.prismus.scrambler.value;

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
        this(0, minimum, maximum);
    }

    public RandomInteger(Integer value, Integer minimum, Integer maximum) {
        super(value, minimum, maximum);
        usingDefaults(0, Integer.MAX_VALUE);
    }

    @Override
    public Integer next() {
        checkBoundaries();
        Integer value = super.next();
        final Random random = new Random();
        if (minimum != null && maximum != null) {
            value = minimum + random.nextInt(Math.abs(maximum - minimum));
        } else {
            value = value != null ? random.nextInt(Math.abs(value) + 1) : random.nextInt();
        }
        setValue(value);
        return value;
    }
}
