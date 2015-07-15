package org.prismus.scrambler.value;

import java.util.Random;

/**
 * @author Serge Pruteanu
 */
class RandomInteger extends AbstractRandomRange<Integer> implements IntArray {

    private final Random random;

    public RandomInteger() {
        this(null);
    }

    public RandomInteger(Integer value) {
        super(value);
        usingDefaults(0, Integer.MAX_VALUE);
        random = new Random();
    }

    public RandomInteger(Integer minimum, Integer maximum) {
        this(0, minimum, maximum);
    }

    public RandomInteger(Integer value, Integer minimum, Integer maximum) {
        super(value, minimum, maximum);
        usingDefaults(0, Integer.MAX_VALUE);
        random = new Random();
    }

    int nextValue(int previousValue) {
        final int result;
        if (minimum != null && maximum != null) {
            result = minimum + random.nextInt(Math.abs(maximum - minimum));
        } else {
            result = random.nextInt(Math.abs(previousValue) + 1);
        }
        return result;
    }

    @Override
    public Integer next() {
        checkBoundaries();
        final Integer result = nextValue(value == null ? random.nextInt() : value);
        setValue(result);
        return result;
    }

    public void next(int[] values) {
        checkBoundaries();
        int start = nextValue(value == null ? random.nextInt() : value);
        for (int i = 0; i < values.length; i++) {
            final int next = nextValue(start);
            values[i] = next;
            start = next;
        }
        setValue(start);
    }

}
