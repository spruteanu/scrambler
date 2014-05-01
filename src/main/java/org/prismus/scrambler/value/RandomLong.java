package org.prismus.scrambler.value;

import java.util.Random;

/**
 * @author Serge Pruteanu
 */
public class RandomLong extends AbstractRandomRange<Long> {

    private final Random random;

    public RandomLong() {
        this(null);
    }

    public RandomLong(Long value) {
        super(value);
        usingDefaults(0L, Long.MAX_VALUE);
        random = new Random();
    }

    public RandomLong(Long minimum, Long maximum) {
        super(minimum, maximum);
        usingDefaults(0L, Long.MAX_VALUE);
        random = new Random();
    }

    public RandomLong(Long value, Long minimum, Long maximum) {
        super(value, minimum, maximum);
        usingDefaults(0L, Long.MAX_VALUE);
        random = new Random();
    }

    @Override
    public Long next() {
        checkBoundaries();
        final Long value;
        if (minimum != null && maximum != null) {
            final long interval = Math.abs(maximum - minimum);
            value = minimum + Math.abs(random.nextLong()) % interval;
        } else {
            value = random.nextLong();
        }
        setValue(value);
        return value;
    }
}
