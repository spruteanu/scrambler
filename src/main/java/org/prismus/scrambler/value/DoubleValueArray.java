package org.prismus.scrambler.value;

/**
 * todo: add description
 *
 * @author Serge Pruteanu
 */
class DoubleValueArray extends Constant<double[]> {
    private Integer count;
    private DoubleArray instance;
    private Boolean randomCount;
    private boolean useInitialArray;

    public DoubleValueArray() {
    }

    public DoubleValueArray(double[] array, DoubleArray value) {
        this(array, null, value);
    }

    public DoubleValueArray(Class<Integer> valueType, DoubleArray value) {
        this(valueType, null, value);
    }

    public DoubleValueArray(double[] array, Integer count, DoubleArray value) {
        this(array, count, value, null);
        useInitialArray = array != null;
    }

    public DoubleValueArray(Class<Integer> valueType, Integer count, DoubleArray value) {
        this(valueType, count, value, null);
    }

    public DoubleValueArray(double[] array, Integer count, DoubleArray value1, Boolean randomCount) {
        super(array);
        this.count = count;
        this.instance = value1;
        this.randomCount = randomCount;
    }

    public DoubleValueArray(Class<Integer> valueType, Integer count, DoubleArray value1, Boolean randomCount) {
        super(null);
        this.count = count;
        this.instance = value1;
        this.randomCount = randomCount;
    }

    @Override
    public double[] next() {
        Util.validateArguments(instance);
        int count = this.count != null ? this.count : 0;
        if (count == 0) {
            count = 20;
        }
        if (randomCount != null && randomCount) {
            count = new RandomInteger(count).between(0, count).next();
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
