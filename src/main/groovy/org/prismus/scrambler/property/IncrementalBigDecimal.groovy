package org.prismus.scrambler.property

import groovy.transform.CompileStatic

/**
 * @author Serge Pruteanu
 */
@CompileStatic
class IncrementalBigDecimal extends Generic<BigDecimal> {
    private BigDecimal step
    private static final BigDecimal DEFAULT_STEP = BigDecimal.valueOf(1L)

    IncrementalBigDecimal() {
        this(null, null, DEFAULT_STEP)
    }

    IncrementalBigDecimal(String name) {
        this(name, null, DEFAULT_STEP)
    }

    IncrementalBigDecimal(String name, BigDecimal value) {
        this(name, value, DEFAULT_STEP)
    }

    IncrementalBigDecimal(String name, BigDecimal value, BigDecimal step) {
        super(name, value)
        this.step = step != null ? step : DEFAULT_STEP
    }

    void setStep(BigDecimal step) {
        this.step = step
    }

    @Override
    BigDecimal value() {
        BigDecimal value = super.value()
        value = value != null ? value.add(step) : step
        setValue(value)
        return value
    }
}
