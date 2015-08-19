package org.prismus.scrambler.test;

import org.prismus.scrambler.ValuePredicate;
import org.prismus.scrambler.ValuePredicates;

import java.util.*;
import java.util.regex.Pattern;

/**
 * todo: add description
 *
 * @author Serge Pruteanu
 */
public class Expectations {
    private final List<ValuePredicate> predicates;

    public Expectations() {
        this(new ArrayList<ValuePredicate>());
    }

    public Expectations(List<ValuePredicate> predicates) {
        this.predicates = predicates;
    }

    public boolean verify(ExecutionContext context) {
        boolean result = true;
        for (final ValuePredicate predicate : predicates) {
            result &= predicate.apply(null, context.getInspected());
        }
        context.setPassed(result);
        return result;
    }

    public Expectations typePredicate(Class clazzType) {
        predicates.add(ValuePredicates.typePredicate(clazzType));
        return this;
    }

    public Expectations typePredicate(String clazzType) throws ClassNotFoundException {
        predicates.add(ValuePredicates.typePredicate(clazzType));
        return this;
    }

    public Expectations predicateOf(String propertyWildcard, Class clazzType) {
        predicates.add(ValuePredicates.predicateOf(propertyWildcard, clazzType));
        return this;
    }

    public Expectations predicateOf(Pattern pattern, Class clazzType) {
        predicates.add(ValuePredicates.predicateOf(pattern, clazzType));
        return this;
    }

    public Expectations predicateOf(Pattern pattern) {
        predicates.add(ValuePredicates.predicateOf(pattern));
        return this;
    }

    public Expectations predicateOf(String propertyWildcard) {
        predicates.add(ValuePredicates.predicateOf(propertyWildcard));
        return this;
    }

    public Expectations typeFilterOf(Pattern pattern) {
        predicates.add(ValuePredicates.typeFilterOf(pattern));
        return this;
    }

    public Expectations isNull() {
        predicates.add(ValuePredicates.isNull());
        return this;
    }

    public Expectations isNotNull() {
        predicates.add(ValuePredicates.isNotNull());
        return this;
    }

    public Expectations equalsTo(Object object) {
        predicates.add(ValuePredicates.equalsTo(object));
        return this;
    }

    public Expectations isSame(Object object) {
        predicates.add(ValuePredicates.isSame(object));
        return this;
    }

    public Expectations any(Set<Object> values) {
        predicates.add(ValuePredicates.any(values));
        return this;
    }

    public Expectations any(Object... values) {
        return any(new LinkedHashSet<Object>(Arrays.asList(values)));
    }

    public Expectations any(Collection<Object> values) {
        return any(new LinkedHashSet<Object>(values));
    }

    public <N extends Comparable> Expectations between(final N min, final N max) {
        predicates.add(ValuePredicates.between(min, max));
        return this;
    }

    public Expectations inTime(final long expectedTime) {
        predicates.add(new ValuePredicate() {
            @Override
            public boolean apply(String property, Object value) {
                ExecutionContext executionContext = (ExecutionContext) value;
                final long inspected = (Long) executionContext.getInspected();
                return expectedTime <= inspected;
            }
        });
        return this;
    }

}
