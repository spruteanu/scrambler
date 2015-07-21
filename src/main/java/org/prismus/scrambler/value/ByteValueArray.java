package org.prismus.scrambler.value;

/**
 * @author Serge Pruteanu
 */
class ByteValueArray extends Constant<byte[]> {
    private Integer count;
    private ByteArray instance;
    private boolean useInitialArray;

    public ByteValueArray() {
    }

    public ByteValueArray(byte[] array, ByteArray value) {
        this(array, null, value);
    }

    public ByteValueArray(byte[] array, Integer count, Object value) {
        this(array, count, (ByteArray)value);
    }

    public ByteValueArray(byte[] array, Integer count, ByteArray value1) {
        super(array);
        this.count = count;
        this.instance = value1;
        useInitialArray = array != null;
    }

    @Override
    public byte[] next() {
        Integer count = this.count;
        if (count == null) {
            count = new RandomInteger(1).between(1, 20).next();
        }

        byte[] value = useInitialArray ? this.value : new byte[count];
        instance.next(value);

        setValue(value);
        useInitialArray = false;
        return value;
    }

    public void setCount(Integer count) {
        this.count = count;
    }

}
