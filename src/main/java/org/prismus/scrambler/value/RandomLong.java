package org.prismus.scrambler.value;

import java.util.Random;

/**
 * @author Serge Pruteanu
 */
class RandomLong extends AbstractRandomRange<Long> implements LongArray {

    private final Random random;

    public RandomLong() {
        this(null, null, null);
    }

    public RandomLong(Long value) {
        this(value, null, null);
    }

    public RandomLong(Long minimum, Long maximum) {
        this(null, minimum, maximum);
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
    public Long get() {
        return value == null ? nextValue() : value;
    }

    @Override
    public Long next() {
        final long result = nextValue();
        setValue(result);
        return result;
    }

    public void next(long[] values) {
        long next = 0;
        for (int i = 0; i < values.length; i++) {
            next = nextValue();
            values[i] = next;
        }
        setValue(next);
    }

}
