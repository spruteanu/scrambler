package org.prismus.scrambler.property;

/**
 * @author Serge Pruteanu
 */
public class IncrementalInteger extends Generic<Integer> {
    private static final int DEFAULT_STEP = 1;
    private int step;

    public IncrementalInteger() {
        this(null, null, DEFAULT_STEP);
    }

    public IncrementalInteger(String name) {
        this(name, null, DEFAULT_STEP);
    }

    public IncrementalInteger(String name, Integer value) {
        this(name, value, DEFAULT_STEP);
    }

    public IncrementalInteger(String name, Integer value, Integer step) {
        super(name, value);
        this.step = step != null ? step : DEFAULT_STEP;
    }

    public void setStep(int step) {
        this.step = step;
    }

    @Override
    public Integer value() {
        Integer value = super.value();
        value = value != null ? value + step : step;
        setValue(value);
        return value;
    }
}
