package org.prismus.scrambler.value;

/**
 * @author Serge Pruteanu
 */
class BooleanValueArray extends Constant<boolean[]> {
    private Integer count;
    private BooleanArray instance;
    private boolean useInitialArray;

    public BooleanValueArray() {
    }

    public BooleanValueArray(boolean[] array, BooleanArray value) {
        this(array, null, value);
    }

    public BooleanValueArray(boolean[] array, Integer count, Object value) {
        this(array, count, (BooleanArray)value);
    }

    public BooleanValueArray(boolean[] array, Integer count, BooleanArray value1) {
        super(array);
        this.count = count != null ? count : array != null ? array.length : null;
        this.instance = value1;
        useInitialArray = array != null && array.length == this.count;
    }

    @Override
    public boolean[] next() {
        Integer count = this.count;
        if (count == null) {
            count = new RandomInteger(1).between(1, 20).next();
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
