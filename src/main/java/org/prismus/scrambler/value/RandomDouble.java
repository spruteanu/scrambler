package org.prismus.scrambler.value;

import java.util.Random;

/**
 * @author Serge Pruteanu
 */
public class RandomDouble extends AbstractRandomRange<Double> {

    private final Random random;

    public RandomDouble() {
        this(null);
    }

    public RandomDouble(Double value) {
        super(value);
        usingDefaults(0.0, Double.MAX_VALUE);
        random = new Random();
    }

    public RandomDouble(Double minimum, Double maximum) {
        super(minimum, maximum);
        usingDefaults(0.0, Double.MAX_VALUE);
        random = new Random();
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
    public Double next() {
        checkBoundaries();
        Double result = nextValue();
        setValue(result);
        return result;
    }

    public double[] next(int count) {
        checkBoundaries();
        double next = nextValue();
        final double[] values = new double[count];
        for (int i = 0; i < values.length; i++) {
            next = nextValue();
            values[i] = next;
        }
        setValue(next);
        return values;
    }

}
