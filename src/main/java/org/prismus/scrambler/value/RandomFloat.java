package org.prismus.scrambler.value;

/**
 * @author Serge Pruteanu
 */
public class RandomFloat extends AbstractRandomRange<Float> {
    public RandomFloat() {
        this(null);
    }

    public RandomFloat(Float value) {
        super(value);
        usingDefaults(0F, Float.MAX_VALUE);
    }

    public RandomFloat(Float minimum, Float maximum) {
        super(minimum, maximum);
        usingDefaults(0F, Float.MAX_VALUE);
    }

    public RandomFloat(Float value, Float minimum, Float maximum) {
        super(value, minimum, maximum);
        usingDefaults(0F, Float.MAX_VALUE);
    }

    @Override
    public Float next() {
        final Float value = super.next();
        final float newValue = new RandomDouble(value != null ? value.doubleValue() : null)
                .usingDefaults(defaultMinimum.doubleValue(), defaultMaximum.doubleValue())
                .between(
                        minimum != null ? minimum.doubleValue() : null,
                        maximum != null ? maximum.doubleValue() : null
                ).next().floatValue();
        setValue(newValue);
        return newValue;
    }
}
