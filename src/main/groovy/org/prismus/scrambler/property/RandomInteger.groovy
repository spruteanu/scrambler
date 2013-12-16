package org.prismus.scrambler.property

import groovy.transform.CompileStatic

import java.util.Random

/**
 * @author Serge Pruteanu
 */
@CompileStatic
class RandomInteger extends RandomRange<Integer> {
    RandomInteger() {
        this(null, null)
    }

    RandomInteger(String name) {
        this(name, null)
    }

    RandomInteger(String name, Integer value) {
        super(name, value)
        usingDefaults(0, Integer.MAX_VALUE)
    }

    RandomInteger(String name, Integer minimum, Integer maximum) {
        super(name, minimum, maximum)
        usingDefaults(0, Integer.MAX_VALUE)
    }

    @Override
    Integer value() {
        checkBoundaries()
        Integer value = super.value()
        final Random random = new Random()
        if (minimum != null && maximum != null) {
            value = random.nextInt(maximum - minimum) + 1
        } else {
            value = value != null ? random.nextInt(Math.abs(value) + 1) : random.nextInt()
        }
        return value
    }
}
