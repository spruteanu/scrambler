package org.prismus.scrambler.value;

import java.math.BigInteger;

/**
 * @author Serge Pruteanu
 */
class IncrementalBigInteger extends Constant<BigInteger> {
    private BigInteger step;
    private static final BigInteger DEFAULT_STEP = BigInteger.valueOf(1L);

    public IncrementalBigInteger() {
        this(null, DEFAULT_STEP);
    }

    public IncrementalBigInteger(BigInteger value) {
        this(value, DEFAULT_STEP);
    }

    public IncrementalBigInteger(BigInteger value, BigInteger step) {
        super(value);
        this.step = step != null ? step : DEFAULT_STEP;
    }

    public void setStep(BigInteger step) {
        this.step = step;
    }

    @Override
    public BigInteger next() {
        BigInteger value = super.next();
        value = value != null ? value.add(step) : step;
        setValue(value);
        return value;
    }
}
