package org.prismus.scrambler.property;

import java.math.BigDecimal;

/**
 * @author Serge Pruteanu
 */
public class RandomBigDecimal extends AbstractRandomRange<BigDecimal> {
    public RandomBigDecimal() {
        this(null);
    }

    public RandomBigDecimal(BigDecimal value) {
        super(value);
        usingDefaults(BigDecimal.valueOf(0), BigDecimal.valueOf(Double.MAX_VALUE));
    }

    public RandomBigDecimal(BigDecimal minimum, BigDecimal maximum) {
        super(minimum, maximum);
        usingDefaults(BigDecimal.valueOf(0), BigDecimal.valueOf(Double.MAX_VALUE));
    }

    public RandomBigDecimal(BigDecimal value, BigDecimal minimum, BigDecimal maximum) {
        super(value,minimum, maximum);
        usingDefaults(BigDecimal.valueOf(0), BigDecimal.valueOf(Double.MAX_VALUE));
    }

    @Override
    public BigDecimal next() {
        final BigDecimal value = super.next();
        final BigDecimal newValue = BigDecimal.valueOf(
                new RandomDouble(value != null ? value.doubleValue() : null)
                        .usingDefaults(defaultMinimum.doubleValue(), defaultMaximum.doubleValue())
                        .between(
                                minimum != null ? minimum.doubleValue() : null,
                                maximum != null ? maximum.doubleValue() : null
                        ).next()
        );
        setValue(newValue);
        return newValue;
    }
}
