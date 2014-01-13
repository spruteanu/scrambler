package org.prismus.scrambler.value;

import java.math.BigInteger;

/**
 * @author Serge Pruteanu
 */
public class RandomBigInteger extends AbstractRandomRange<BigInteger> {
    public RandomBigInteger() {
        this(null);
    }

    public RandomBigInteger(BigInteger value) {
        super(value);
        usingDefaults(BigInteger.valueOf(0), BigInteger.valueOf(Long.MAX_VALUE));
    }

    public RandomBigInteger(BigInteger minimum, BigInteger maximum) {
        super(minimum, maximum);
        usingDefaults(BigInteger.valueOf(0), BigInteger.valueOf(Long.MAX_VALUE));
    }

    public RandomBigInteger(BigInteger value, BigInteger minimum, BigInteger maximum) {
        super(value, minimum, maximum);
        usingDefaults(BigInteger.valueOf(0), BigInteger.valueOf(Long.MAX_VALUE));
    }

    @Override
    public BigInteger next() {
        final BigInteger value = super.next();
        final BigInteger newValue = BigInteger.valueOf(
                new RandomLong(value != null ? value.longValue() : null)
                        .usingDefaults(defaultMinimum.longValue(), defaultMaximum.longValue())
                        .between(
                                minimum != null ? minimum.longValue() : null,
                                maximum != null ? maximum.longValue() : null
                        ).next()
        );
        setValue(newValue);
        return newValue;
    }
}
