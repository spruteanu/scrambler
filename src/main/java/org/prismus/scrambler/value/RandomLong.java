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

    long nextValue() {
        if (minimum != null && maximum != null) {
            final long interval = Math.abs(maximum - minimum);
            value = minimum + Math.abs(random.nextLong()) % interval;
        } else {
            value = random.nextLong();
        }
        return value;
    }

    @Override
    public Long next() {
        checkBoundaries();
        final long result = nextValue();
        setValue(result);
        return result;
    }

    public long[] next(int count) {
        checkBoundaries();
        final long[] values = new long[count];
        long next = 0;
        for (int i = 0; i < values.length; i++) {
            next = nextValue();
            values[i] = next;
        }
        setValue(next);
        return values;
    }

}
