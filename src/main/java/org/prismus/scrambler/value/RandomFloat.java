package org.prismus.scrambler.value;

import java.util.Random;

/**
 * @author Serge Pruteanu
 */
class RandomFloat extends AbstractRandomRange<Float> implements FloatArray {
    private final Random random;

    public RandomFloat() {
        this(null, null, null);
    }

    public RandomFloat(Float value) {
        this(value, null, null);
    }

    public RandomFloat(Float minimum, Float maximum) {
        this(null, minimum, maximum);
    }

    public RandomFloat(Float value, Float minimum, Float maximum) {
        super(value, minimum, maximum);
        usingDefaults(0F, Float.MAX_VALUE);
        random = new Random();
    }

    float nextValue() {
        final float result;
        if (minimum != null && maximum != null) {
            float interval = Math.abs(maximum - minimum);
            result = minimum + interval * random.nextFloat();
        } else {
            result = value != null ? random.nextFloat() * value : random.nextFloat() * Math.abs(random.nextInt());
        }
        return result;
    }

    @Override
    public Float next() {
        final Float result = nextValue();
        setValue(result);
        return result;
    }

    public void next(float[] values) {
        float next = nextValue();
        for (int i = 0; i < values.length; i++) {
            next = nextValue();
            values[i] = next;
        }
        setValue(next);
    }

}
