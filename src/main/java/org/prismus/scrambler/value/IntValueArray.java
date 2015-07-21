package org.prismus.scrambler.value;

/**
 * todo: add description
 *
 * @author Serge Pruteanu
 */
class IntValueArray extends Constant<int[]> {
    private Integer count;
    private IntArray instance;
    private boolean useInitialArray;

    public IntValueArray(int[] array, IntArray value) {
        this(array, null, value);
    }

    public IntValueArray(int[] array, Integer count, Object value1) {
        this(array, count, (IntArray)value1);
    }

    public IntValueArray(int[] array, Integer count, IntArray value1) {
        super(array);
        this.count = count;
        this.instance = value1;
        useInitialArray = array != null;
    }

    @Override
    public int[] next() {
        Integer count = this.count;
        if (count == null) {
            count = new RandomInteger(1).between(1, 20).next();
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
