package org.prismus.scrambler.property;

import java.util.Random;

/**
 * @author Serge Pruteanu
 */
public class RandomDouble extends RandomRange<Double> {
    public RandomDouble() {
        this(null, null);
    }

    public RandomDouble(String name) {
        this(name, null);
    }

    public RandomDouble(String name, Double value) {
        super(name, value);
        usingDefaults(0.0, Double.MAX_VALUE);
    }

    public RandomDouble(String name, Double minimum, Double maximum) {
        super(name, minimum, maximum);
        usingDefaults(0.0, Double.MAX_VALUE);
    }

    @Override
    public Double value() {
        checkBoundaries();
        Double value = super.value();
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
