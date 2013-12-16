package org.prismus.scrambler.property

import groovy.transform.CompileStatic
import org.prismus.scrambler.Property

/**
 * @author Serge Pruteanu
 */
@CompileStatic
class Generic<T> implements Property<T>, Cloneable {
    private String name
    protected T value

    Generic() {
    }

    Generic(String name) {
        this(name, null)
    }

    Generic(String name, T value) {
        this.name = name
        this.value = value
    }

    void setName(String name) {
        this.name = name
    }

    void setValue(T value) {
        this.value = value
    }

    Generic<T> usingValue(T value) {
        this.value = value
        return this
    }

    String getName() {
        return name
    }

    T getValue() {
        return value
    }

    T value() {
        return value
    }

    @Override
    Object clone() throws CloneNotSupportedException {
        return super.clone()
    }

}
