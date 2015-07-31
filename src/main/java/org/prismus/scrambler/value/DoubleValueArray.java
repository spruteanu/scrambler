package org.prismus.scrambler.value;

/**
 * @author Serge Pruteanu
 */
class DoubleValueArray extends Constant<double[]> {
    private Integer count;
    private DoubleArray instance;
    private boolean useInitialArray;

    public DoubleValueArray() {
    }

    public DoubleValueArray(double[] array, DoubleArray value) {
        this(array, null, value);
    }

    public DoubleValueArray(double[] array, Integer count, Object value) {
        this(array, count, (DoubleArray)value);
    }

    public DoubleValueArray(double[] array, Integer count, DoubleArray value1) {
        super(array);
        this.count = count != null ? count : array != null ? array.length : null;
        this.instance = value1;
        useInitialArray = array != null && array.length == this.count;
    }

    @Override
    public double[] next() {
        Integer count = this.count;
        if (count == null) {
            count = new RandomInteger(1).between(1, 20).next();
        }

        double[] value = useInitialArray ? this.value : new double[count];
        instance.next(value);

        setValue(value);
        useInitialArray = false;
        return value;
    }

    public void setCount(Integer count) {
        this.count = count;
    }

}
