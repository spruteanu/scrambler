package org.prismus.scrambler.value;

/**
 * todo: add description
 *
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
        this.count = count;
        this.instance = value1;
        useInitialArray = array != null;
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
