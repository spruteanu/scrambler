package org.prismus.scrambler.property;

/**
 * @author Serge Pruteanu
 */
public class RandomFloat extends RandomRange<Float> {
    public RandomFloat() {
        this(null, null);
    }

    public RandomFloat(String name) {
        this(name, null);
    }

    public RandomFloat(String name, Float value) {
        super(name, value);
        usingDefaults(0F, Float.MAX_VALUE);
    }

    public RandomFloat(String name, Float minimum, Float maximum) {
        super(name, minimum, maximum);
        usingDefaults(0F, Float.MAX_VALUE);
    }

    @Override
    public Float value() {
        final Float value = super.value();
        return new RandomDouble(getName(), value != null ? value.doubleValue() : null)
                .usingDefaults(defaultMinimum.doubleValue(), defaultMaximum.doubleValue())
                .between(
                        minimum != null ? minimum.doubleValue() : null,
                        maximum != null ? maximum.doubleValue() : null
                ).value().floatValue();
    }
}
