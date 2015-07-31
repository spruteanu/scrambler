package org.prismus.scrambler.value;

/**
 * @author Serge Pruteanu
 */
class FloatValueArray extends Constant<float[]> {
    private Integer count;
    private FloatArray instance;
    private boolean useInitialArray;

    public FloatValueArray() {
    }

    public FloatValueArray(float[] array, FloatArray value) {
        this(array, null, value);
    }

    public FloatValueArray(float[] array, Integer count, Object value) {
        this(array, count, (FloatArray) value);
    }

    public FloatValueArray(float[] array, Integer count, FloatArray value1) {
        super(array);
        this.count = count != null ? count : array != null ? array.length : null;
        this.instance = value1;
        useInitialArray = array != null && array.length == this.count;
    }

    @Override
    public float[] next() {
        Integer count = this.count;
        if (count == null) {
            count = new RandomInteger(1).between(1, 20).next();
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
