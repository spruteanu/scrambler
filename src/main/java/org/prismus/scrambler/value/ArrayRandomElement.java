package org.prismus.scrambler.value;

import java.util.Random;

/**
 * @author Serge Pruteanu
 */
public class ArrayRandomElement<T> extends Constant<T> {
    private final Random random;
    private T[] values;

    public ArrayRandomElement(T[] values) {
        super();
        this.values = values;
        setValue(values[0]);
        random = new Random();
    }

    public void setValues(T[] values) {
        this.values = values;
    }

    public T next() {
        final T value = values[random.nextInt(values.length)];
        setValue(value);
        return value;
    }

    public T[] getValues() {
        return values;
    }

}
