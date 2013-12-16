package org.prismus.scrambler.property;

import org.prismus.scrambler.Property;

/**
 * @author Serge Pruteanu
 */
public class Generic<T> implements Property<T>, Cloneable {
    private String name;
    protected T value;

    public Generic() {
    }

    public Generic(String name) {
        this(name, null);
    }

    public Generic(String name, T value) {
        this.name = name;
        this.value = value;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setValue(T value) {
        this.value = value;
    }

    public Generic<T> usingValue(T value) {
        this.value = value;
        return this;
    }

    public String getName() {
        return name;
    }

    public T getValue() {
        return value;
    }

    public T value() {
        return value;
    }

    @Override
    public Object clone() throws CloneNotSupportedException {
        return super.clone();
    }

}
