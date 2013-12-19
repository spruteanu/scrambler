package org.prismus.scrambler.property;

/**
 * @author Serge Pruteanu
 */
public class RandomByte extends AbstractRandomRange<Byte> {
    public RandomByte() {
        this(null, null);
    }

    public RandomByte(String name) {
        this(name, null);
    }

    public RandomByte(String name, Byte value) {
        super(name, value);
        usingDefaults(Integer.valueOf(0).byteValue(), Byte.MAX_VALUE);
    }

    public RandomByte(String name, Byte minimum, Byte maximum) {
        super(name, minimum, maximum);
        usingDefaults(Integer.valueOf(0).byteValue(), Byte.MAX_VALUE);
    }

    @Override
    public Byte value() {
        final Byte value = super.value();
        return new RandomInteger(getName(), value != null ? value.intValue() : null)
                .usingDefaults(defaultMinimum.intValue(), defaultMaximum.intValue())
                .between(
                        minimum != null ? minimum.intValue() : null,
                        maximum != null ? maximum.intValue() : null
                ).value().byteValue();
    }
}
