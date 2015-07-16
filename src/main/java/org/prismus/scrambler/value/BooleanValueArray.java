package org.prismus.scrambler.value;

/**
 * todo: add description
 *
 * @author Serge Pruteanu
 */
class BooleanValueArray extends Constant<boolean[]> {
    private Integer count;
    private BooleanArray instance;
    private Boolean randomCount;
    private boolean useInitialArray;

    public BooleanValueArray() {
    }

    public BooleanValueArray(boolean[] array, BooleanArray value) {
        this(array, null, value);
    }

    public BooleanValueArray(boolean[] array, Integer count, BooleanArray value) {
        this(array, count, value, null);
    }

    public BooleanValueArray(boolean[] array, Integer count, Object value) {
        this(array, count, (BooleanArray)value, null);
    }

    public BooleanValueArray(boolean[] array, Integer count, BooleanArray value1, Boolean randomCount) {
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
    public boolean[] next() {
        Util.validateArguments(instance);
        int count = this.count != null ? this.count : 0;
        if (count == 0) {
            count = 20;
        }
        if (randomCount != null && randomCount) {
            count = new RandomInteger(count).between(1, count).next();
        }

        boolean[] value = useInitialArray ? this.value : new boolean[count];
        instance.next(value);

        setValue(value);
        useInitialArray = false;
        return value;
    }

    public void setCount(Integer count) {
        this.count = count;
    }

}
