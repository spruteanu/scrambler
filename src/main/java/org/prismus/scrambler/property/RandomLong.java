package org.prismus.scrambler.property;

import java.util.Random;

/**
 * @author Serge Pruteanu
 */
public class RandomLong extends RandomRange<Long> {
    public RandomLong() {
        this(null, null);
    }

    public RandomLong(String name) {
        this(name, null);
    }

    public RandomLong(String name, Long value) {
        super(name, value);
        usingDefaults(0L, Long.MAX_VALUE);
    }

    public RandomLong(String name, Long minimum, Long maximum) {
        super(name, minimum, maximum);
        usingDefaults(0L, Long.MAX_VALUE);
    }

    @Override
    public Long value() {
        checkBoundaries();
        final Long value;
        final Random random = new Random();
        if (minimum != null && maximum != null) {
            final long interval = Math.abs(maximum - minimum);
            value = minimum + Math.abs(random.nextLong()) % interval;
        } else {
            value = random.nextLong();
        }
        return value;
    }
}
