package org.prismus.scrambler.property;

/**
 * @author Serge Pruteanu
 */
public class RandomShort extends AbstractRandomRange<Short> {
    public RandomShort() {
        this(null);
    }

    public RandomShort(Short value) {
        super(value);
        usingDefaults(Integer.valueOf(0).shortValue(), Short.MAX_VALUE);
    }

    public RandomShort(Short minimum, Short maximum) {
        super(minimum, maximum);
        usingDefaults(Integer.valueOf(0).shortValue(), Short.MAX_VALUE);
    }

    @Override
    public Short value() {
        final Short value = super.value();
        return new RandomInteger(value != null ? value.intValue() : null)
                .usingDefaults(defaultMinimum.intValue(), defaultMaximum.intValue())
                .between(
                        minimum != null ? minimum.intValue() : null,
                        maximum != null ? maximum.intValue() : null
                ).value().shortValue();
    }
}
