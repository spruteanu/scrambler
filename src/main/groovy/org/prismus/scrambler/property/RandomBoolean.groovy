package org.prismus.scrambler.property

import groovy.transform.CompileStatic

/**
 * @author Serge Pruteanu
 */
@CompileStatic
class RandomBoolean extends Generic<Boolean> {
    RandomBoolean() {
        super()
    }

    RandomBoolean(String name) {
        this(name, null)
    }

    RandomBoolean(String name, Boolean value) {
        super(name, value)
    }

    @Override
    Boolean value() {
        return new java.util.Random().nextBoolean()
    }
}
