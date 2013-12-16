package org.prismus.scrambler.property;

import java.util.List;
import java.util.Random;

/**
 * @author Serge Pruteanu
 */
public class RandomListElement<T> extends Generic<T> {
    private List<T> values;

    public RandomListElement() {
        super();
    }

    public RandomListElement(String name, List<T> values) {
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
