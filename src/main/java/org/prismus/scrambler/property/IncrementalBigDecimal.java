package org.prismus.scrambler.property;

import java.math.BigDecimal;

/**
 * @author Serge Pruteanu
 */
public class IncrementalBigDecimal extends Generic<BigDecimal> {
    private BigDecimal step;
    private static final BigDecimal DEFAULT_STEP = BigDecimal.valueOf(1L);

    public IncrementalBigDecimal() {
        this(null, null, DEFAULT_STEP);
    }

    public IncrementalBigDecimal(String name) {
        this(name, null, DEFAULT_STEP);
    }

    public IncrementalBigDecimal(String name, BigDecimal value) {
        this(name, value, DEFAULT_STEP);
    }

    public IncrementalBigDecimal(String name, BigDecimal value, BigDecimal step) {
        super(name, value);
        this.step = step != null ? step : DEFAULT_STEP;
    }

    public void setStep(BigDecimal step) {
        this.step = step;
    }

    @Override
    public BigDecimal value() {
        BigDecimal value = super.value();
        value = value != null ? value.add(step) : step;
        setValue(value);
        return value;
    }
}
