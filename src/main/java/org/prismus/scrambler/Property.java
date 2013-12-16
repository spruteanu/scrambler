package org.prismus.scrambler;

import java.io.Serializable;

/**
 * todo: add description
 *
 * @author Serge Pruteanu
 */
public interface Property<T> extends Serializable, Cloneable {
    String getName();
    T value();
}
