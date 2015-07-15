package org.prismus.scrambler.value;

import java.math.BigDecimal;

/**
 * @author Serge Pruteanu
 */
class IncrementalBigDecimal extends Constant<BigDecimal> {
    private BigDecimal step;
    private static final BigDecimal DEFAULT_STEP = BigDecimal.valueOf(1L);

    public IncrementalBigDecimal() {
        this(null, DEFAULT_STEP);
    }

    public IncrementalBigDecimal(BigDecimal value) {
        this(value, DEFAULT_STEP);
    }

    public IncrementalBigDecimal(BigDecimal value, BigDecimal step) {
        super(value);
        this.step = step != null ? step : DEFAULT_STEP;
    }

    public void setStep(BigDecimal step) {
        this.step = step;
    }

    @Override
    public BigDecimal next() {
        BigDecimal value = super.next();
        value = value != null ? value.add(step) : step;
        setValue(value);
        return value;
    }
}
