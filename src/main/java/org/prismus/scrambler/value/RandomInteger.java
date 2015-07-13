package org.prismus.scrambler.value;

import java.util.Random;

/**
 * @author Serge Pruteanu
 */
public class RandomInteger extends AbstractRandomRange<Integer> {

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

    int nextValue(int value) {
        final int result;
        if (minimum != null && maximum != null) {
            result = minimum + random.nextInt(Math.abs(maximum - minimum));
        } else {
            result = random.nextInt(Math.abs(value) + 1);
        }
        return result;
    }

    int nextValue() {
        return nextValue(value == null ? random.nextInt() : value);
    }

    @Override
    public Integer next() {
        checkBoundaries();
        final Integer result = nextValue();
        setValue(result);
        return result;
    }

    public int[] next(int count) {
        checkBoundaries();
        final int[] values = new int[count];
        int start = nextValue();
        for (int i = 0; i < values.length; i++) {
            final int next = nextValue(start);
            values[i] = next;
            start = next;
        }
        setValue(start);
        return values;
    }

}
