package org.prismus.scrambler.property

import groovy.transform.CompileStatic

/**
 * @author Serge Pruteanu
 */
@CompileStatic
class IncrementalLong extends Generic<Long> {
    private static final long DEFAULT_STEP = 1L
    private long step

    IncrementalLong() {
        this(null, null, DEFAULT_STEP)
    }

    IncrementalLong(String name) {
        this(name, null, DEFAULT_STEP)
    }

    IncrementalLong(String name, Long value) {
        this(name, value, DEFAULT_STEP)
    }

    IncrementalLong(String name, Long value, Long step) {
        super(name, value)
        this.step = step != null ? step : DEFAULT_STEP
    }

    void setStep(long step) {
        this.step = step
    }

    @Override
    Long value() {
        Long value = super.value()
        value = value != null ? value + step : step
        setValue(value)
        return value
    }
}
