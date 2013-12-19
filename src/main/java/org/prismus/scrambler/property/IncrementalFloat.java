package org.prismus.scrambler.property;

/**
 * @author Serge Pruteanu
 */
public class IncrementalFloat extends Constant<Float> {
    private float step;
    private static final float DEFAULT_STEP = 1.0F;

    public IncrementalFloat() {
        this(null, null, DEFAULT_STEP);
    }

    public IncrementalFloat(String name) {
        this(name, null, DEFAULT_STEP);
    }

    public IncrementalFloat(String name, Float value) {
        this(name, value, DEFAULT_STEP);
    }

    public IncrementalFloat(String name, Float value, Float step) {
        super(name, value);
        this.step = step != null ? step : DEFAULT_STEP;
    }

    @Override
    public Float value() {
        Float value = super.value();
        value = value != null ? value + step : step;
        setValue(value);
        return value;
    }
}
