package org.prismus.scrambler.property

import groovy.transform.CompileStatic

/**
 * @author Serge Pruteanu
 */
@CompileStatic
class IncrementalByte extends Generic<Byte> {
    private static final byte DEFAULT_STEP = Integer.valueOf(1).byteValue()
    private byte step

    IncrementalByte() {
        this(null, null, DEFAULT_STEP)
    }

    IncrementalByte(String name) {
        this(name, null, DEFAULT_STEP)
    }

    IncrementalByte(String name, Byte value) {
        this(name, value, DEFAULT_STEP)
    }

    IncrementalByte(String name, Byte value, Byte step) {
        super(name, value)
        this.step = step != null ? step : DEFAULT_STEP
    }

    void setStep(byte step) {
        this.step = step
    }

    @Override
    Byte value() {
        Byte value = super.value()
        value = value != null ? Integer.valueOf(value + step).byteValue() : step
        setValue(value)
        return value
    }
}
