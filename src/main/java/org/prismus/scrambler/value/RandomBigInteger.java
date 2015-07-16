package org.prismus.scrambler.value;

import org.prismus.scrambler.Value;

import java.math.BigInteger;

/**
 * @author Serge Pruteanu
 */
class RandomBigInteger extends AbstractRandomRange<BigInteger> {
    private final Value<Long> instance;

    public RandomBigInteger() {
        this(null, null, null);
    }

    public RandomBigInteger(BigInteger value) {
        this(value, null, null);
    }

    public RandomBigInteger(BigInteger minimum, BigInteger maximum) {
        this(null, minimum, maximum);
    }

    public RandomBigInteger(BigInteger value, BigInteger minimum, BigInteger maximum) {
        super(value, minimum, maximum);
        usingDefaults(BigInteger.valueOf(0), BigInteger.valueOf(Long.MAX_VALUE));
        instance = new RandomLong(value != null ? value.longValue() : null)
                .usingDefaults(defaultMinimum.longValue(), defaultMaximum.longValue())
                .between(
                        minimum != null ? minimum.longValue() : null,
                        maximum != null ? maximum.longValue() : null
                );
    }

    @Override
    public BigInteger next() {
        final BigInteger newValue = BigInteger.valueOf(instance.next());
        setValue(newValue);
        return newValue;
    }

}
