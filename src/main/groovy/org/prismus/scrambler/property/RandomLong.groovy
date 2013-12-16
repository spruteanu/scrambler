package org.prismus.scrambler.property

import groovy.transform.CompileStatic

import java.util.Random

/**
 * @author Serge Pruteanu
 */
@CompileStatic
class RandomLong extends RandomRange<Long> {
    RandomLong() {
        this(null, null)
    }

    RandomLong(String name) {
        this(name, null)
    }

    RandomLong(String name, Long value) {
        super(name, value)
        usingDefaults(0L, Long.MAX_VALUE)
    }

    RandomLong(String name, Long minimum, Long maximum) {
        super(name, minimum, maximum)
        usingDefaults(0L, Long.MAX_VALUE)
    }

    @Override
    Long value() {
        checkBoundaries()
        final Long value
        final Random random = new Random()
        if (minimum != null && maximum != null) {
            final long interval = Math.abs(maximum - minimum)
            value = minimum + Math.abs(random.nextLong()) % interval
        } else {
            value = random.nextLong()
        }
        return value
    }
}
