package org.prismus.scrambler.value;

/**
 * todo: add description
 *
 * @author Serge Pruteanu
 */
class ByteValueArray extends Constant<byte[]> {
    private Integer count;
    private ByteArray instance;
    private Boolean randomCount;
    private boolean useInitialArray;

    public ByteValueArray() {
    }

    public ByteValueArray(byte[] array, ByteArray value) {
        this(array, null, value);
    }

    public ByteValueArray(Class<Integer> valueType, ByteArray value) {
        this(valueType, null, value);
    }

    public ByteValueArray(byte[] array, Integer count, ByteArray value) {
        this(array, count, value, null);
        useInitialArray = array != null;
    }

    public ByteValueArray(Class<Integer> valueType, Integer count, ByteArray value) {
        this(valueType, count, value, null);
    }

    public ByteValueArray(byte[] array, Integer count, ByteArray value1, Boolean randomCount) {
        super(array);
        this.count = count;
        this.instance = value1;
        this.randomCount = randomCount;
    }

    public ByteValueArray(Class<Integer> valueType, Integer count, ByteArray value1, Boolean randomCount) {
        super(null);
        this.count = count;
        this.instance = value1;
        this.randomCount = randomCount;
    }

    @Override
    public byte[] next() {
        Util.validateArguments(instance);
        int count = this.count != null ? this.count : 0;
        if (count == 0) {
            count = 20;
        }
        if (randomCount != null && randomCount) {
            count = new RandomInteger(count).between(0, count).next();
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
