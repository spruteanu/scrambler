package org.prismus.scrambler.test;

import org.prismus.scrambler.ValuePredicate;

import java.util.List;

/**
 * todo: add description
 *
 * @author Serge Pruteanu
 */
public class ContainerPredicates implements ValuePredicate {
    private final List<ValuePredicate> predicates;

    public ContainerPredicates(List<ValuePredicate> predicates) {
        this.predicates = predicates;
    }

    @Override
    public boolean apply(String property, Object value) {
        boolean result = true;
        for (ValuePredicate predicate : predicates) {
            result &= predicate.apply(property, value);
        }
        return result;
    }

}
