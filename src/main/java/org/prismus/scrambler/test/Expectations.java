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
    private TestContext context;

    public Expectations() {
        this(new ArrayList<ValuePredicate>());
    }

    Expectations(List<ValuePredicate> predicates) {
        this.predicates = predicates;
    }

    TestContext verify() {
        boolean result = true;
        for (final ValuePredicate predicate : predicates) {
            result &= predicate.apply(null, context.getInspected());
        }
        if (context == null) {
            context = new TestContext();
        }
        context.setPassed(result);
        return context;
    }

    Expectations forContext(TestContext context) {
        this.context = context;
        return this;
    }

    public Expectations typePredicate(Class clazzType) {
        predicates.add(new ContextDelegatingPredicate(ValuePredicates.typePredicate(clazzType)));
        return this;
    }

    public Expectations typePredicate(String clazzType) throws ClassNotFoundException {
        predicates.add(new ContextDelegatingPredicate(ValuePredicates.typePredicate(clazzType)));
        return this;
    }

    public Expectations predicateOf(String propertyWildcard, Class clazzType) {
        predicates.add(new ContextDelegatingPredicate(ValuePredicates.predicateOf(propertyWildcard, clazzType)));
        return this;
    }

    public Expectations predicateOf(Pattern pattern, Class clazzType) {
        predicates.add(new ContextDelegatingPredicate(ValuePredicates.predicateOf(pattern, clazzType)));
        return this;
    }

    public Expectations predicateOf(Pattern pattern) {
        predicates.add(new ContextDelegatingPredicate(ValuePredicates.predicateOf(pattern)));
        return this;
    }

    public Expectations predicateOf(String propertyWildcard) {
        predicates.add(new ContextDelegatingPredicate(ValuePredicates.predicateOf(propertyWildcard)));
        return this;
    }

    public Expectations typeFilterOf(Pattern pattern) {
        predicates.add(new ContextDelegatingPredicate(ValuePredicates.typeFilterOf(pattern)));
        return this;
    }

    public Expectations isNull() {
        predicates.add(new ContextDelegatingPredicate(ValuePredicates.isNull()));
        return this;
    }

    public Expectations isNotNull() {
        predicates.add(new ContextDelegatingPredicate(ValuePredicates.isNotNull()));
        return this;
    }

    public Expectations equalsTo(Object object) {
        predicates.add(new ContextDelegatingPredicate(ValuePredicates.equalsTo(object)));
        return this;
    }

    public Expectations isSame(Object object) {
        predicates.add(new ContextDelegatingPredicate(ValuePredicates.isSame(object)));
        return this;
    }

    public Expectations any(Set<Object> values) {
        predicates.add(new ContextDelegatingPredicate(ValuePredicates.any(values)));
        return this;
    }

    public Expectations any(Object... values) {
        return any(new LinkedHashSet<Object>(Arrays.asList(values)));
    }

    public Expectations any(Collection<Object> values) {
        return any(new LinkedHashSet<Object>(values));
    }

    public <N extends Comparable> Expectations between(final N min, final N max) {
        predicates.add(new ContextDelegatingPredicate(ValuePredicates.between(min, max)));
        return this;
    }

    public Expectations inTime(final long expectedTime) {
        predicates.add(new ValuePredicate() {
            @Override
            public boolean apply(String property, Object value) {
                final MethodTestContext executionContext = (MethodTestContext) value;
                final long inspected = executionContext.getExecutionTime();
                return expectedTime <= inspected;
            }
        });
        return this;
    }

    private static class ContextDelegatingPredicate implements ValuePredicate {
        private final ValuePredicate predicate;

        public ContextDelegatingPredicate(ValuePredicate predicate) {
            this.predicate = predicate;
        }

        @Override
        public boolean apply(String property, Object value) {
            final TestContext testContext = (TestContext) value;
            return predicate.apply(property, testContext.getInspected());
        }
    }
}
