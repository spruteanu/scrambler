package org.prismus.scrambler.property;

import java.util.Random;

/**
 * @author Serge Pruteanu
 */
public class RandomLong extends AbstractRandomRange<Long> {
    public RandomLong() {
        this(null);
    }

    public RandomLong(Long value) {
        super(value);
        usingDefaults(0L, Long.MAX_VALUE);
    }

    public RandomLong(Long minimum, Long maximum) {
        super(minimum, maximum);
        usingDefaults(0L, Long.MAX_VALUE);
    }

    public RandomLong(Long value, Long minimum, Long maximum) {
        super(value, minimum, maximum);
        usingDefaults(0L, Long.MAX_VALUE);
    }

    @Override
    public Long next() {
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
