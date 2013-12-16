package org.prismus.scrambler.property

import groovy.transform.CompileStatic

/**
 * @author Serge Pruteanu
 */
@CompileStatic
class IncrementalShort extends Generic<Short> {
    private static final short DEFAULT_STEP = Integer.valueOf(1).shortValue()
    private short step

    IncrementalShort() {
        this(null, null, DEFAULT_STEP)
    }

    IncrementalShort(String name) {
        this(name, null, DEFAULT_STEP)
    }

    IncrementalShort(String name, Short value) {
        this(name, value, DEFAULT_STEP)
    }

    IncrementalShort(String name, Short value, Short step) {
        super(name, value)
        this.step = step != null ? step : DEFAULT_STEP
    }

    void setStep(short step) {
        this.step = step
    }

    @Override
    Short value() {
        Short value = super.value()
        value = value != null ? Integer.valueOf(value + step).shortValue() : step
        setValue(value)
        return value
    }
}
