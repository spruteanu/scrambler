package org.prismus.scrambler.property;

import java.util.List;
import java.util.Random;

/**
 * @author Serge Pruteanu
 */
public class RandomElement<T> extends Constant<T> {
    private List<T> values;

    public RandomElement() {
        super();
    }

    public RandomElement(String name, List<T> values) {
        super(name);
        this.values = values;
    }

    public void setValues(List<T> values) {
        this.values = values;
    }

    public T value() {
        return values.get(Math.abs(new Random().nextInt(values.size())));
    }
}
