package org.prismus.scrambler.property

import groovy.transform.CompileStatic

/**
 * @author Serge Pruteanu
 */
@CompileStatic
class RandomDouble extends RandomRange<Double> {
    RandomDouble() {
        this(null, null)
    }

    RandomDouble(String name) {
        this(name, null)
    }

    RandomDouble(String name, Double value) {
        super(name, value)
        usingDefaults(0.0, Double.MAX_VALUE)
    }

    RandomDouble(String name, Double minimum, Double maximum) {
        super(name, minimum, maximum)
        usingDefaults(0.0, Double.MAX_VALUE)
    }

    @Override
    Double value() {
        checkBoundaries()
        Double value = super.value()
        final java.util.Random random = new java.util.Random()
        if (minimum != null && maximum != null) {
            double interval = Math.abs(maximum - minimum)
            value = minimum + interval * random.nextDouble()
        } else {
            value = value != null ? random.nextDouble() * value : random.nextDouble()
        }
        return value
    }
}
