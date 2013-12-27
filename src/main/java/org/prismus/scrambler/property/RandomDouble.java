package org.prismus.scrambler.property;

import java.util.Random;

/**
 * @author Serge Pruteanu
 */
public class RandomDouble extends AbstractRandomRange<Double> {
    public RandomDouble() {
        this(null);
    }

    public RandomDouble(Double value) {
        super(value);
        usingDefaults(0.0, Double.MAX_VALUE);
    }

    public RandomDouble(Double minimum, Double maximum) {
        super(minimum, maximum);
        usingDefaults(0.0, Double.MAX_VALUE);
    }

    public RandomDouble(Double value, Double minimum, Double maximum) {
        super(value, minimum, maximum);
        usingDefaults(0.0, Double.MAX_VALUE);
    }

    @Override
    public Double next() {
        checkBoundaries();
        Double value = super.next();
        final Random random = new Random();
        if (minimum != null && maximum != null) {
            double interval = Math.abs(maximum - minimum);
            value = minimum + interval * random.nextDouble();
        } else {
            value = value != null ? random.nextDouble() * value : random.nextDouble();
        }
        return value;
    }
}
