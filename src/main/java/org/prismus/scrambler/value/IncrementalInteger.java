package org.prismus.scrambler.value;

/**
 * @author Serge Pruteanu
 */
public class IncrementalInteger extends Constant<Integer> {
    private static final int DEFAULT_STEP = 1;
    private int step;

    public IncrementalInteger() {
        this(null, DEFAULT_STEP);
    }

    public IncrementalInteger(Integer value) {
        this(value, DEFAULT_STEP);
    }

    public IncrementalInteger(Integer value, Integer step) {
        super(value);
        this.step = step != null ? step : DEFAULT_STEP;
    }

    public void setStep(int step) {
        this.step = step;
    }

    @Override
    public Integer next() {
        Integer value = super.next();
        value = value != null ? value + step : step;
        setValue(value);
        return value;
    }

    public int[] next(int count) {
        final int[] values = new int[count];
        int start = value == null ? 0 : value;
        for (int i = 0; i < values.length; i++) {
            final int next = start + step;
            values[i] = next;
            start = next;
        }
        setValue(start);
        return values;
    }

    public int getStep() {
        return step;
    }

}
