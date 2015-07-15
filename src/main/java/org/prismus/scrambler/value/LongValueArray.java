package org.prismus.scrambler.value;

/**
 * todo: add description
 *
 * @author Serge Pruteanu
 */
class LongValueArray extends Constant<long[]> {
    private Integer count;
    private LongArray instance;
    private Boolean randomCount;
    private boolean useInitialArray;

    public LongValueArray() {
    }

    public LongValueArray(long[] array, LongArray value) {
        this(array, null, value);
    }

    public LongValueArray(Class<Integer> valueType, LongArray value) {
        this(valueType, null, value);
    }

    public LongValueArray(long[] array, Integer count, LongArray value) {
        this(array, count, value, null);
        useInitialArray = array != null;
    }

    public LongValueArray(Class<Integer> valueType, Integer count, LongArray value) {
        this(valueType, count, value, null);
    }

    public LongValueArray(long[] array, Integer count, LongArray value1, Boolean randomCount) {
        super(array);
        this.count = count;
        this.instance = value1;
        this.randomCount = randomCount;
    }

    public LongValueArray(Class<Integer> valueType, Integer count, LongArray value1, Boolean randomCount) {
        super(null);
        this.count = count;
        this.instance = value1;
        this.randomCount = randomCount;
    }

    @Override
    public long[] next() {
        Util.validateArguments(instance);
        int count = this.count != null ? this.count : 0;
        if (count == 0) {
            count = 20;
        }
        if (randomCount != null && randomCount) {
            count = new RandomInteger(count).between(0, count).next();
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
