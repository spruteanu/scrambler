package org.prismus.scrambler.value;

import java.util.Random;

/**
 * @author Serge Pruteanu
 */
class RandomDouble extends AbstractRandomRange<Double> implements DoubleArray {

    private final Random random;

    public RandomDouble() {
        this(null, null, null);
    }

    public RandomDouble(Double value) {
        this(value, null, null);
    }

    public RandomDouble(Double minimum, Double maximum) {
        this(null, minimum, maximum);
    }

    public RandomDouble(Double value, Double minimum, Double maximum) {
        super(value, minimum, maximum);
        usingDefaults(0.0, Double.MAX_VALUE);
        random = new Random();
    }

    double nextValue() {
        final double result;
        if (minimum != null && maximum != null) {
            double interval = Math.abs(maximum - minimum);
            result = minimum + interval * random.nextDouble();
        } else {
            result = value != null ? random.nextDouble() * value : random.nextDouble() * Math.abs(random.nextInt());
        }
        return result;
    }

    @Override
    public Double get() {
        return value == null ? nextValue() : value;
    }

    @Override
    public Double next() {
        Double result = nextValue();
        setValue(result);
        return result;
    }

    public void next(double[] values) {
        double next = nextValue();
        for (int i = 0; i < values.length; i++) {
            next = nextValue();
            values[i] = next;
        }
        setValue(next);
    }

}
