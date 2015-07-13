package org.prismus.scrambler.value;

/**
 * @author Serge Pruteanu
 */
public class IncrementalFloat extends Constant<Float> {
    private float step;
    private static final float DEFAULT_STEP = 1.0F;

    public IncrementalFloat() {
        this(null, DEFAULT_STEP);
    }

    public IncrementalFloat(Float value) {
        this(value, DEFAULT_STEP);
    }

    public IncrementalFloat(Float value, Float step) {
        super(value);
        this.step = step != null ? step : DEFAULT_STEP;
    }

    @Override
    public Float next() {
        Float value = super.next();
        value = value != null ? value + step : step;
        setValue(value);
        return value;
    }

    public float[] next(int count) {
        final float[] values = new float[count];
        float start = 0.0f;
        for (int i = 0; i < values.length; i++) {
            float next = start + step;
            values[i] = next;
            start = next;
        }
        setValue(start);
        return values;
    }

    public float getStep() {
        return step;
    }

}
