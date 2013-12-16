package org.prismus.scrambler.property

import groovy.transform.CompileStatic

/**
 * @author Serge Pruteanu
 */
@CompileStatic
class IncrementalDouble extends Generic<Double> {
    private static final double DEFAULT_STEP = 1.0
    private double step

    IncrementalDouble() {
        this(null, null, DEFAULT_STEP)
    }

    IncrementalDouble(String name) {
        this(name, null, DEFAULT_STEP)
    }

    IncrementalDouble(String name, Double value) {
        this(name, value, DEFAULT_STEP)
    }

    IncrementalDouble(String name, Double value, Double step) {
        super(name, value)
        this.step = step != null ? step : DEFAULT_STEP
    }

    void setStep(double step) {
        this.step = step
    }

    @Override
    Double value() {
        Double value = super.value()
        value = value != null ? value + step : step
        setValue(value)
        return value
    }
}
