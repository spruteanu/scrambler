package org.prismus.scrambler.property

import groovy.transform.CompileStatic

/**
 * @author Serge Pruteanu
 */
@CompileStatic
class IncrementalFloat extends Generic<Float> {
    private float step
    private static final float DEFAULT_STEP = 1.0F

    IncrementalFloat() {
        this(null, null, DEFAULT_STEP)
    }

    IncrementalFloat(String name) {
        this(name, null, DEFAULT_STEP)
    }

    IncrementalFloat(String name, Float value) {
        this(name, value, DEFAULT_STEP)
    }

    IncrementalFloat(String name, Float value, Float step) {
        super(name, value)
        this.step = step != null ? step : DEFAULT_STEP
    }

    @Override
    Float value() {
        Float value = super.value()
        value = value != null ? value + step : step
        setValue(value)
        return value
    }
}
