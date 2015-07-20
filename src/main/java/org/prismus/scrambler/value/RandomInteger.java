package org.prismus.scrambler.value;

import java.util.Random;

/**
 * @author Serge Pruteanu
 */
class RandomInteger extends AbstractRandomRange<Integer> implements IntArray {

    private final Random random;

    public RandomInteger() {
        this(null, null, null);
    }

    public RandomInteger(Integer value) {
        this(value, null, null);
    }

    public RandomInteger(Integer minimum, Integer maximum) {
        this(null, minimum, maximum);
    }

    public RandomInteger(Integer value, Integer minimum, Integer maximum) {
        super(value, minimum, maximum);
        usingDefaults(0, Integer.MAX_VALUE);
        random = new Random();
    }

    @Override
    protected Integer min(Integer val1, Integer val2) {
        return val1 != null ? val2 != null ? val1.compareTo(val2) > 0 ? val2 : val1 : null : null;
    }

    @Override
    protected Integer max(Integer val1, Integer val2) {
        return val1 != null ? val2 != null ? val1.compareTo(val2) > 0 ? val1 : val2 : val1 : val2;
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
    public Integer get() {
        return value == null ? nextValue(random.nextInt()) : value;
    }

    @Override
    public Integer next() {
        final Integer result = nextValue(value == null ? random.nextInt() : value);
        setValue(result);
        return result;
    }

    public void next(int[] values) {
        int start = nextValue(value == null ? random.nextInt() : value);
        for (int i = 0; i < values.length; i++) {
            final int next = nextValue(start);
            values[i] = next;
            start = next;
        }
        setValue(start);
    }

}
