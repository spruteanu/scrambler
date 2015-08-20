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
public class Expect {
    private final List<ValuePredicate> predicates;
    private TestContext expectationContext;
    private String message;

    Expect() {
        this(new ArrayList<ValuePredicate>(), null);
    }

    Expect(String message) {
        this(new ArrayList<ValuePredicate>(), message);
    }

    Expect(List<ValuePredicate> predicates, String message) {
        this.predicates = predicates;
        this.message = message;
    }

    public static Expect that() {
        return new Expect();
    }

    public static Expect that(String message) {
        return new Expect(message);
    }

    @Override
    public String toString() {
        return message;
    }

    TestResultContext verify() {
        final TestResultContext resultContext = new TestResultContext();
        boolean result = true;
        final Object inspected = expectationContext.getInspected();
        for (final ValuePredicate predicate : predicates) {
            final boolean predicateResult = predicate.apply(null, inspected);
            resultContext.add(new TestContext(inspected, predicate.toString()).verified(predicateResult));
            result &= predicateResult;
        }
        resultContext.verified(result);
        return resultContext;
    }

    Expect forContext(TestContext context) {
        this.expectationContext = context;
        return this;
    }

    public Expect isTypeOf(Class clazzType) {
        predicates.add(new ContextDelegatingPredicate(ValuePredicates.isTypeOf(clazzType)));
        return this;
    }

    public Expect isTypeOf(String clazzType) throws ClassNotFoundException {
        predicates.add(new ContextDelegatingPredicate(ValuePredicates.isTypeOf(clazzType)));
        return this;
    }

    public Expect matches(String propertyWildcard, Class clazzType) {
        predicates.add(new ContextDelegatingPredicate(ValuePredicates.matches(propertyWildcard, clazzType)));
        return this;
    }

    public Expect matches(Pattern pattern, Class clazzType) {
        predicates.add(new ContextDelegatingPredicate(ValuePredicates.matches(pattern, clazzType)));
        return this;
    }

    public Expect matches(Pattern pattern) {
        predicates.add(new ContextDelegatingPredicate(ValuePredicates.matchProperty(pattern)));
        return this;
    }

    public Expect matches(String propertyWildcard) {
        predicates.add(new ContextDelegatingPredicate(ValuePredicates.matchProperty(propertyWildcard)));
        return this;
    }

    public Expect matchesTypes(Pattern pattern) {
        predicates.add(new ContextDelegatingPredicate(ValuePredicates.matchesTypes(pattern)));
        return this;
    }

    public Expect isNull() {
        predicates.add(new ContextDelegatingPredicate(ValuePredicates.isNull()));
        return this;
    }

    public Expect isNotNull() {
        predicates.add(new ContextDelegatingPredicate(ValuePredicates.isNotNull()));
        return this;
    }

    public Expect equalsTo(Object object) {
        predicates.add(new ContextDelegatingPredicate(ValuePredicates.equalsTo(object)));
        return this;
    }

    public Expect isSame(Object object) {
        predicates.add(new ContextDelegatingPredicate(ValuePredicates.isSame(object)));
        return this;
    }

    public Expect isAny(Set<Object> values) {
        predicates.add(new ContextDelegatingPredicate(ValuePredicates.isAny(values)));
        return this;
    }

    public Expect isAny(Object... values) {
        return isAny(new LinkedHashSet<Object>(Arrays.asList(values)));
    }

    public Expect isAny(Collection<Object> values) {
        return isAny(new LinkedHashSet<Object>(values));
    }

    public <N extends Comparable> Expect between(final N min, final N max) {
        predicates.add(new ContextDelegatingPredicate(ValuePredicates.between(min, max)));
        return this;
    }

    public Expect executedIn(final long expectedTime) {
        predicates.add(new ValuePredicate() {
            @Override
            public boolean apply(String property, Object value) {
                final MethodTestContext executionContext = (MethodTestContext) value;
                final long inspected = executionContext.getExecutionTime();
                return expectedTime <= inspected;
            }

            @Override
            public String toString() {
                return String.format("Verify method executed in time: '%d'", expectedTime);
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

        @Override
        public String toString() {
            return predicate.toString();
        }
    }
}
