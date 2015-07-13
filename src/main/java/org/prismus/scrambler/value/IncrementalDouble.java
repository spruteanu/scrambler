package org.prismus.scrambler.value;

/**
 * @author Serge Pruteanu
 */
public class IncrementalDouble extends Constant<Double> {
    private static final double DEFAULT_STEP = 1.0;
    private double step;

    public IncrementalDouble() {
        this(null, DEFAULT_STEP);
    }

    public IncrementalDouble(Double value) {
        this(value, DEFAULT_STEP);
    }

    public IncrementalDouble(Double value, Double step) {
        super(value);
        this.step = step != null ? step : DEFAULT_STEP;
    }

    public void setStep(double step) {
        this.step = step;
    }

    @Override
    public Double next() {
        Double value = super.next();
        value = value != null ? value + step : step;
        setValue(value);
        return value;
    }

    public double[] next(int count) {
        final double[] values = new double[count];
        double start = 0.0f;
        for (int i = 0; i < values.length; i++) {
            double next = start + step;
            values[i] = next;
            start = next;
        }
        setValue(start);
        return values;
    }

    public double getStep() {
        return step;
    }

}
