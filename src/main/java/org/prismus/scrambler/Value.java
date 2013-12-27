package org.prismus.scrambler;

import java.io.Serializable;

/**
 * todo: add description
 *
 * @author Serge Pruteanu
 */
public interface Value<T> extends Serializable, Cloneable {
    T next();
}
