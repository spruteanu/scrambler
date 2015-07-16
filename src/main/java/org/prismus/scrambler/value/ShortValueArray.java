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
        this(array, null, value, null);
    }

    public ShortValueArray(short[] array, Integer count, ShortArray value) {
        this(array, count, value, null);
    }

    public ShortValueArray(short[] array, Integer count, Object value) {
        this(array, count, (ShortArray)value, null);
    }

    public ShortValueArray(short[] array, Integer count, ShortArray value1, Boolean randomCount) {
        super(array);
        this.count = count;
        this.instance = value1;
        this.randomCount = randomCount;
        if (array == null && count == null) {
            this.randomCount = Boolean.TRUE;
        }
        useInitialArray = array != null;
    }

    @Override
    public short[] next() {
        Util.validateArguments(instance);
        int count = this.count != null ? this.count : 0;
        if (count == 0) {
            count = 20;
        }
        if (randomCount != null && randomCount) {
            count = new RandomInteger(count).between(1, count).next();
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
