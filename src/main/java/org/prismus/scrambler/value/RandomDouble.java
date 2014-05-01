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

    @Override
    public Double next() {
        checkBoundaries();
        Double value = super.next();
        if (minimum != null && maximum != null) {
            double interval = Math.abs(maximum - minimum);
            value = minimum + interval * random.nextDouble();
        } else {
            value = value != null ? random.nextDouble() * value : random.nextDouble();
        }
        setValue(value);
        return value;
    }
}
