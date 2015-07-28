package org.prismus.scrambler;

/**
 * Interface that matches either {@code property} and/or {@code value}.
 * The predicate is mostly used as a Map.key for value definitions
 *
 * @author Serge Pruteanu
 */
public interface ValuePredicate {

    boolean apply(String property, Object value);

}
