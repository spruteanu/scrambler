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

    @Override
    public BigDecimal value() {
        final BigDecimal value = super.value();
        return BigDecimal.valueOf(
                new RandomDouble(value != null ? value.doubleValue() : null)
                        .usingDefaults(defaultMinimum.doubleValue(), defaultMaximum.doubleValue())
                        .between(
                                minimum != null ? minimum.doubleValue() : null,
                                maximum != null ? maximum.doubleValue() : null
                        ).value()
        );
    }
}
