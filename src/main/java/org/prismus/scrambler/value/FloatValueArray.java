package org.prismus.scrambler.value;

/**
 * todo: add description
 *
 * @author Serge Pruteanu
 */
class FloatValueArray extends Constant<float[]> {
    private Integer count;
    private FloatArray instance;
    private Boolean randomCount;
    private boolean useInitialArray;

    public FloatValueArray() {
    }

    public FloatValueArray(float[] array, FloatArray value) {
        this(array, null, value);
    }

    public FloatValueArray(Class<Integer> valueType, FloatArray value) {
        this(valueType, null, value);
    }

    public FloatValueArray(float[] array, Integer count, FloatArray value) {
        this(array, count, value, null);
        useInitialArray = array != null;
    }

    public FloatValueArray(Class<Integer> valueType, Integer count, FloatArray value) {
        this(valueType, count, value, null);
    }

    public FloatValueArray(float[] array, Integer count, FloatArray value1, Boolean randomCount) {
        super(array);
        this.count = count;
        this.instance = value1;
        this.randomCount = randomCount;
    }

    public FloatValueArray(Class<Integer> valueType, Integer count, FloatArray value1, Boolean randomCount) {
        super(null);
        this.count = count;
        this.instance = value1;
        this.randomCount = randomCount;
    }

    @Override
    public float[] next() {
        Util.validateArguments(instance);
        int count = this.count != null ? this.count : 0;
        if (count == 0) {
            count = 20;
        }
        if (randomCount != null && randomCount) {
            count = new RandomInteger(count).between(0, count).next();
        }

        float[] value = useInitialArray ? this.value : new float[count];
        instance.next(value);

        setValue(value);
        useInitialArray = false;
        return value;
    }

    public void setCount(Integer count) {
        this.count = count;
    }

}
