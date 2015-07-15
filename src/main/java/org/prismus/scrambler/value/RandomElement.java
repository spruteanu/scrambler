package org.prismus.scrambler.value;

import java.util.List;
import java.util.Random;

/**
 * @author Serge Pruteanu
 */
class RandomElement<T> extends Constant<T> {
    private final Random random;
    private List<T> values;

    public RandomElement(List<T> values) {
        super();
        this.values = values;
        setValue(values.get(0));
        random = new Random();
    }

    public void setValues(List<T> values) {
        this.values = values;
    }

    public T next() {
        final T value = values.get(Math.abs(random.nextInt(values.size())));
        setValue(value);
        return value;
    }

}
