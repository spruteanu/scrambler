package org.prismus.scrambler.property;

/**
 * @author Serge Pruteanu
 */
public class RandomByte extends AbstractRandomRange<Byte> {
    public RandomByte() {
        this(null);
    }

    public RandomByte(Byte value) {
        super(value);
        usingDefaults(Integer.valueOf(0).byteValue(), Byte.MAX_VALUE);
    }

    public RandomByte(Byte minimum, Byte maximum) {
        super(minimum, maximum);
        usingDefaults(Integer.valueOf(0).byteValue(), Byte.MAX_VALUE);
    }

    public RandomByte(Byte value, Byte minimum, Byte maximum) {
        super(value, minimum, maximum);
        usingDefaults(Integer.valueOf(0).byteValue(), Byte.MAX_VALUE);
    }

    @Override
    public Byte next() {
        final Byte value = super.next();
        return new RandomInteger(value != null ? value.intValue() : null)
                .usingDefaults(defaultMinimum.intValue(), defaultMaximum.intValue())
                .between(
                        minimum != null ? minimum.intValue() : null,
                        maximum != null ? maximum.intValue() : null
                ).next().byteValue();
    }
}
