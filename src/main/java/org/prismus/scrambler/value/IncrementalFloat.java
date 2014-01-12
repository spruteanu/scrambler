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
}
