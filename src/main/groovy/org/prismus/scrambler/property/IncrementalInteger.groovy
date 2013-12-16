package org.prismus.scrambler.property

import groovy.transform.CompileStatic

/**
 * @author Serge Pruteanu
 */
@CompileStatic
class IncrementalInteger extends Generic<Integer> {
    private static final int DEFAULT_STEP = 1
    private int step

    IncrementalInteger() {
        this(null, null, DEFAULT_STEP)
    }

    IncrementalInteger(String name) {
        this(name, null, DEFAULT_STEP)
    }

    IncrementalInteger(String name, Integer value) {
        this(name, value, DEFAULT_STEP)
    }

    IncrementalInteger(String name, Integer value, Integer step) {
        super(name, value)
        this.step = step != null ? step : DEFAULT_STEP
    }

    void setStep(int step) {
        this.step = step
    }

    @Override
    Integer value() {
        Integer value = super.value()
        value = value != null ? value + step : step
        setValue(value)
        return value
    }
}
