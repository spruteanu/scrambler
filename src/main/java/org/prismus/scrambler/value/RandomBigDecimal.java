package org.prismus.scrambler.value;

import java.math.BigDecimal;

/**
 * @author Serge Pruteanu
 */
class RandomBigDecimal extends AbstractRandomRange<BigDecimal> {

    public RandomBigDecimal() {
        this(null, null, null);
    }

    public RandomBigDecimal(BigDecimal value) {
        this(value, null, null);
    }

    public RandomBigDecimal(BigDecimal minimum, BigDecimal maximum) {
        this(null, minimum, maximum);
    }

    public RandomBigDecimal(BigDecimal value, BigDecimal minimum, BigDecimal maximum) {
        super(value, minimum, maximum);
        usingDefaults(BigDecimal.ZERO, BigDecimal.valueOf(Double.MAX_VALUE));
    }

    @Override
    protected BigDecimal min(BigDecimal val1, BigDecimal val2) {
        return val1 != null ? val2 != null ? val1.compareTo(val2) > 0 ? val2 : val1 : null : null;
    }

    @Override
    protected BigDecimal max(BigDecimal val1, BigDecimal val2) {
        return val1 != null ? val2 != null ? val1.compareTo(val2) > 0 ? val1 : val2 : val1 : val2;
    }

    BigDecimal nextValue() {
        final BigDecimal result;
        if (minimum != null && maximum != null) {
            result = minimum.add(maximum.subtract(minimum).multiply(new BigDecimal(Math.random())));
        } else {
            result = defaultMaximum.multiply(new BigDecimal(Math.random()));
        }
        return result;
    }

    @Override
    public BigDecimal get() {
        return value == null ? nextValue() : value;
    }

    @Override
    public BigDecimal next() {
        final BigDecimal newValue = nextValue();
        setValue(newValue);
        return newValue;
    }
}
