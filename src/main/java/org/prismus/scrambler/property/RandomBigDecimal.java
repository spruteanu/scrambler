package org.prismus.scrambler.property;

import java.math.BigDecimal;

/**
 * @author Serge Pruteanu
 */
public class RandomBigDecimal extends AbstractRandomRange<BigDecimal> {
    public RandomBigDecimal(String name) {
        this(name, null);
    }

    public RandomBigDecimal(String name, BigDecimal value) {
        super(name, value);
        usingDefaults(BigDecimal.valueOf(0), BigDecimal.valueOf(Double.MAX_VALUE));
    }

    public RandomBigDecimal(String name, BigDecimal minimum, BigDecimal maximum) {
        super(name, minimum, maximum);
        usingDefaults(BigDecimal.valueOf(0), BigDecimal.valueOf(Double.MAX_VALUE));
    }

    @Override
    public BigDecimal value() {
        final BigDecimal value = super.value();
        return BigDecimal.valueOf(
                new RandomDouble(getName(), value != null ? value.doubleValue() : null)
                        .usingDefaults(defaultMinimum.doubleValue(), defaultMaximum.doubleValue())
                        .between(
                                minimum != null ? minimum.doubleValue() : null,
                                maximum != null ? maximum.doubleValue() : null
                        ).value()
        );
    }
}
