package org.prismus.scrambler.value;

import java.util.List;
import java.util.Random;

/**
 * @author Serge Pruteanu
 */
public class RandomElement<T> extends Constant<T> {
    private List<T> values;

    public RandomElement(List<T> values) {
        super();
        this.values = values;
        setValue(values.get(0));
    }

    public void setValues(List<T> values) {
        this.values = values;
    }

    public T next() {
        final T value = values.get(Math.abs(new Random().nextInt(values.size())));
        setValue(value);
        return value;
    }
}
