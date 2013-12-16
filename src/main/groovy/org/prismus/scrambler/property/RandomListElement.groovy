package org.prismus.scrambler.property

import groovy.transform.CompileStatic

import java.util.Random

/**
 * @author Serge Pruteanu
 */
@CompileStatic
class RandomListElement<T> extends Generic<T> {
    private List<T> values

    RandomListElement() {
        super()
    }

    RandomListElement(String name, List<T> values) {
        super(name)
        this.values = values
    }

    void setValues(List<T> values) {
        this.values = values
    }

    T value() {
        return values.get(Math.abs(new Random().nextInt(values.size())))
    }
}
