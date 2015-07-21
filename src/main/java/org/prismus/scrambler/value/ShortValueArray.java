package org.prismus.scrambler.value;

/**
 * @author Serge Pruteanu
 */
class ShortValueArray extends Constant<short[]> {
    private Integer count;
    private ShortArray instance;
    private boolean useInitialArray;

    public ShortValueArray() {
    }

    public ShortValueArray(short[] array, ShortArray value) {
        this(array, null, value);
    }

    public ShortValueArray(short[] array, Integer count, Object value) {
        this(array, count, (ShortArray)value);
    }

    public ShortValueArray(short[] array, Integer count, ShortArray value1) {
        super(array);
        this.count = count;
        this.instance = value1;
        useInitialArray = array != null;
    }

    @Override
    public short[] next() {
        Integer count = this.count;
        if (count == null) {
            count = new RandomInteger(1).between(1, 20).next();
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
