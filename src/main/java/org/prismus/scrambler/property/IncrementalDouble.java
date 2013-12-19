package org.prismus.scrambler.property;

/**
 * @author Serge Pruteanu
 */
public class IncrementalDouble extends Constant<Double> {
    private static final double DEFAULT_STEP = 1.0;
    private double step;

    public IncrementalDouble() {
        this(null, null, DEFAULT_STEP);
    }

    public IncrementalDouble(String name) {
        this(name, null, DEFAULT_STEP);
    }

    public IncrementalDouble(String name, Double value) {
        this(name, value, DEFAULT_STEP);
    }

    public IncrementalDouble(String name, Double value, Double step) {
        super(name, value);
        this.step = step != null ? step : DEFAULT_STEP;
    }

    public void setStep(double step) {
        this.step = step;
    }

    @Override
    public Double value() {
        Double value = super.value();
        value = value != null ? value + step : step;
        setValue(value);
        return value;
    }
}
