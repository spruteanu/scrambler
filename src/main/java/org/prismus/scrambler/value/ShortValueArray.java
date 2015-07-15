package org.prismus.scrambler.value;

/**
 * todo: add description
 *
 * @author Serge Pruteanu
 */
class ShortValueArray extends Constant<short[]> {
    private Integer count;
    private ShortArray instance;
    private Boolean randomCount;
    private boolean useInitialArray;

    public ShortValueArray() {
    }

    public ShortValueArray(short[] array, ShortArray value) {
        this(array, null, value);
    }

    public ShortValueArray(Class<Integer> valueType, ShortArray value) {
        this(valueType, null, value);
    }

    public ShortValueArray(short[] array, Integer count, ShortArray value) {
        this(array, count, value, null);
        useInitialArray = array != null;
    }

    public ShortValueArray(Class<Integer> valueType, Integer count, ShortArray value) {
        this(valueType, count, value, null);
    }

    public ShortValueArray(short[] array, Integer count, ShortArray value1, Boolean randomCount) {
        super(array);
        this.count = count;
        this.instance = value1;
        this.randomCount = randomCount;
    }

    public ShortValueArray(Class<Integer> valueType, Integer count, ShortArray value1, Boolean randomCount) {
        super(null);
        this.count = count;
        this.instance = value1;
        this.randomCount = randomCount;
    }

    @Override
    public short[] next() {
        Util.validateArguments(instance);
        int count = this.count != null ? this.count : 0;
        if (count == 0) {
            count = 20;
        }
        if (randomCount != null && randomCount) {
            count = new RandomInteger(count).between(0, count).next();
        }

        short[] value = useInitialArray ? this.value : new short[count];
        instance.next(value);

        setValue(value);
        useInitialArray = false;
        return value;
    }

    public void setCount(Integer count) {
        this.count = count;
    }

}
