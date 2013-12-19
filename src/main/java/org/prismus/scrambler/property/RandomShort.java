package org.prismus.scrambler.property;

/**
 * @author Serge Pruteanu
 */
public class RandomShort extends AbstractRandomRange<Short> {
    public RandomShort() {
        this(null, null);
    }

    public RandomShort(String name) {
        this(name, null);
    }

    public RandomShort(String name, Short value) {
        super(name, value);
        usingDefaults(Integer.valueOf(0).shortValue(), Short.MAX_VALUE);
    }

    public RandomShort(String name, Short minimum, Short maximum) {
        super(name, minimum, maximum);
        usingDefaults(Integer.valueOf(0).shortValue(), Short.MAX_VALUE);
    }

    @Override
    public Short value() {
        final Short value = super.value();
        return new RandomInteger(getName(), value != null ? value.intValue() : null)
                .usingDefaults(defaultMinimum.intValue(), defaultMaximum.intValue())
                .between(
                        minimum != null ? minimum.intValue() : null,
                        maximum != null ? maximum.intValue() : null
                ).value().shortValue();
    }
}
