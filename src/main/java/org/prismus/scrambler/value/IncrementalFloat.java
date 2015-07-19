package org.prismus.scrambler.value;

/**
 * @author Serge Pruteanu
 */
class IncrementalFloat extends Constant<Float> implements FloatArray{
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
        Float value = get();
        value = value != null ? value + step : 0.0f;
        setValue(value);
        return value;
    }

    public void next(float[] values) {
        float start = value != null ? value : 0.0f;
        for (int i = 0; i < values.length; i++) {
            values[i] = start;
            start = start + step;
        }
        setValue(start);
    }

}
