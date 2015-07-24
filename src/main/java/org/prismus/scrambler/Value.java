package org.prismus.scrambler;

import java.io.Serializable;

/**
 * An interface used to generate an object.
 *
 * @author Serge Pruteanu
 */
public interface Value<T> extends Serializable, Cloneable {

    /**
     * Generates an object.
     *
     * @return an instance of object
     */
    T next();

    /**
     * Gets current instance value
     *
     * @return current value instance
     */
    T get();

    Object clone() throws CloneNotSupportedException;

}
