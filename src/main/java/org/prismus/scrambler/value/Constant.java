package org.prismus.scrambler.value;

import org.prismus.scrambler.Value;

/**
 * @author Serge Pruteanu
 */
public class Constant<T> implements Value<T>, Cloneable {
    protected T value;

    public Constant() {
    }

    public Constant(T value) {
        this.value = value;
    }

    public void setValue(T value) {
        this.value = value;
    }

    public Constant<T> usingValue(T value) {
        this.value = value;
        return this;
    }

    public T get() {
        return value;
    }

    public T next() {
        return value;
    }

    @Override
    public Object clone() throws CloneNotSupportedException {
        return super.clone();
    }

}
