package org.prismus.scrambler.value;

/**
 * todo: add description
 *
 * @author Serge Pruteanu
 */
class LongValueArray extends Constant<long[]> {
    private Integer count;
    private LongArray instance;
    private boolean useInitialArray;

    public LongValueArray() {
    }

    public LongValueArray(long[] array, LongArray value) {
        this(array, null, value);
    }

    public LongValueArray(long[] array, Integer count, Object value) {
        this(array, count, (LongArray)value);
    }

    public LongValueArray(long[] array, Integer count, LongArray value1) {
        super(array);
        this.count = count;
        this.instance = value1;
        useInitialArray = array != null;
    }

    @Override
    public long[] next() {
        Integer count = this.count;
        if (count == null) {
            count = new RandomInteger(1).between(1, 20).next();
        }

        long[] value = useInitialArray ? this.value : new long[count];
        instance.next(value);

        setValue(value);
        useInitialArray = false;
        return value;
    }

    public void setCount(Integer count) {
        this.count = count;
    }

}
