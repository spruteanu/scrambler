package org.prismus.scrambler.property

import groovy.transform.CompileStatic

/**
 * @author Serge Pruteanu
 */
@CompileStatic
class RandomBigDecimal extends RandomRange<BigDecimal> {
    RandomBigDecimal(String name) {
        this(name, null)
    }

    RandomBigDecimal(String name, BigDecimal value) {
        super(name, value)
        usingDefaults(BigDecimal.valueOf(0), BigDecimal.valueOf(Double.MAX_VALUE))
    }

    RandomBigDecimal(String name, BigDecimal minimum, BigDecimal maximum) {
        super(name, minimum, maximum)
        usingDefaults(BigDecimal.valueOf(0), BigDecimal.valueOf(Double.MAX_VALUE))
    }

    @Override
    BigDecimal value() {
        final BigDecimal value = super.value()
        return BigDecimal.valueOf(
                new RandomDouble(getName(), value != null ? value.doubleValue() : null)
                        .usingDefaults(defaultMinimum.doubleValue(), defaultMaximum.doubleValue())
                        .between(
                        minimum != null ? minimum.doubleValue() : null,
                        maximum != null ? maximum.doubleValue() : null
                ).value()
        )
    }
}
