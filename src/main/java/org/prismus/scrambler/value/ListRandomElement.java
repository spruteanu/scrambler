package org.prismus.scrambler.value;

import java.util.List;
import java.util.Random;

/**
 * @author Serge Pruteanu
 */
public class ListRandomElement<T> extends Constant<T> {
    private final Random random;
    private List<T> values;

    public ListRandomElement(List<T> values) {
        super();
        this.values = values;
        setValue(values.get(0));
        random = new Random();
    }

    public void setValues(List<T> values) {
        this.values = values;
    }

    public T next() {
        final T value = values.get(random.nextInt(values.size()));
        setValue(value);
        return value;
    }

}
