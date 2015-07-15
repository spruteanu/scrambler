package org.prismus.scrambler.value;

/**
 * @author Serge Pruteanu
 */
class IncrementalInteger extends Constant<Integer> implements IntArray {
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

    int nextValue(int start) {
        return start + step;
    }

    @Override
    public Integer next() {
        Integer value = super.next();
        value = value != null ? nextValue(value) : 0;
        setValue(value);
        return value;
    }

    public void next(int[] values) {
        int start = value == null ? 0 : value;
        for (int i = 0; i < values.length; i++) {
            final int next = nextValue(start);
            values[i] = next;
            start = next;
        }
        setValue(start);
    }

}
