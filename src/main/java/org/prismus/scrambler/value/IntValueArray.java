package org.prismus.scrambler.value;

/**
 * todo: add description
 *
 * @author Serge Pruteanu
 */
class IntValueArray extends Constant<int[]> {
    private Integer count;
    private IntArray instance;
    private Boolean randomCount;
    private boolean useInitialArray;

    public IntValueArray(int[] array, IntArray value) {
        this(array, null, value, null);
    }

    public IntValueArray(int[] array, Integer count, IntArray value) {
        this(array, count, value, null);
    }

    public IntValueArray(int[] array, Integer count, Object value1) {
        this(array, count, (IntArray)value1, null);
    }

    public IntValueArray(int[] array, Integer count, IntArray value1, Boolean randomCount) {
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
    public int[] next() {
        Util.validateArguments(instance);
        int count = this.count != null ? this.count : 0;
        if (count == 0) {
            count = 20;
        }
        if (randomCount != null && randomCount) {
            count = new RandomInteger(count).between(0, count).next();
        }

        int[] value = useInitialArray ? this.value : new int[count];
        instance.next(value);

        setValue(value);
        useInitialArray = false;
        return value;
    }

    public void setCount(Integer count) {
        this.count = count;
    }

}
