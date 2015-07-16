package org.prismus.scrambler.value;

import org.prismus.scrambler.Value;

import java.math.BigDecimal;

/**
 * @author Serge Pruteanu
 */
class RandomBigDecimal extends AbstractRandomRange<BigDecimal> {
    private final Value<Double> instance;

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
        usingDefaults(BigDecimal.valueOf(0), BigDecimal.valueOf(Double.MAX_VALUE));
        instance = new RandomDouble(value != null ? value.doubleValue() : null)
                .usingDefaults(defaultMinimum.doubleValue(), defaultMaximum.doubleValue())
                .between(
                        minimum != null ? minimum.doubleValue() : null,
                        maximum != null ? maximum.doubleValue() : null
                );
    }

    @Override
    public BigDecimal next() {
        final BigDecimal newValue = BigDecimal.valueOf(instance.next());
        setValue(newValue);
        return newValue;
    }
}
