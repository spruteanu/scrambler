package org.prismus.scrambler.test;

import org.prismus.scrambler.DataPredicate;
import org.prismus.scrambler.DataPredicates;

import java.util.*;
import java.util.regex.Pattern;

/**
 * todo: add description
 *
 * @author Serge Pruteanu
 */
public class Expect {
    // todo Serge: investigate groovy code visitor capabilities, based on example: class SqlWhereVisitor extends CodeVisitorSupport
    private final List<DataPredicate> predicates;
    private TestContext expectationContext;
    private String message;

    Expect() {
        this(new ArrayList<DataPredicate>(), null);
    }

    Expect(String message) {
        this(new ArrayList<DataPredicate>(), message);
    }

    Expect(List<DataPredicate> predicates, String message) {
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

    ResultContext verify() {
        final ResultContext resultContext = new ResultContext();
        boolean result = true;
        final Object inspected = expectationContext.getInspected();
        for (final DataPredicate predicate : predicates) {
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
        predicates.add(new ContextDelegatingPredicate(DataPredicates.isTypeOf(clazzType)));
        return this;
    }

    public Expect isTypeOf(String clazzType) throws ClassNotFoundException {
        predicates.add(new ContextDelegatingPredicate(DataPredicates.isTypeOf(clazzType)));
        return this;
    }

    public Expect matches(String propertyWildcard, Class clazzType) {
        predicates.add(new ContextDelegatingPredicate(DataPredicates.matches(propertyWildcard, clazzType)));
        return this;
    }

    public Expect matches(Pattern pattern, Class clazzType) {
        predicates.add(new ContextDelegatingPredicate(DataPredicates.matches(pattern, clazzType)));
        return this;
    }

    public Expect matches(Pattern pattern) {
        predicates.add(new ContextDelegatingPredicate(DataPredicates.matchProperty(pattern)));
        return this;
    }

    public Expect matches(String propertyWildcard) {
        predicates.add(new ContextDelegatingPredicate(DataPredicates.matchProperty(propertyWildcard)));
        return this;
    }

    public Expect matchesTypes(Pattern pattern) {
        predicates.add(new ContextDelegatingPredicate(DataPredicates.matchesTypes(pattern)));
        return this;
    }

    public Expect isNull() {
        predicates.add(new ContextDelegatingPredicate(DataPredicates.isNull()));
        return this;
    }

    public Expect isNotNull() {
        predicates.add(new ContextDelegatingPredicate(DataPredicates.isNotNull()));
        return this;
    }

    public Expect equalsTo(Object object) {
        predicates.add(new ContextDelegatingPredicate(DataPredicates.equalsTo(object)));
        return this;
    }

    public Expect isSame(Object object) {
        predicates.add(new ContextDelegatingPredicate(DataPredicates.isSame(object)));
        return this;
    }

    public Expect isAny(Set<Object> values) {
        predicates.add(new ContextDelegatingPredicate(DataPredicates.isAny(values)));
        return this;
    }

    public Expect isAny(Object... values) {
        return isAny(new LinkedHashSet<Object>(Arrays.asList(values)));
    }

    public Expect isAny(Collection<Object> values) {
        return isAny(new LinkedHashSet<Object>(values));
    }

    public <N extends Comparable> Expect between(final N min, final N max) {
        predicates.add(new ContextDelegatingPredicate(DataPredicates.between(min, max)));
        return this;
    }

    public Expect executedIn(final long expectedTime) {
        predicates.add(new DataPredicate() {
            @Override
            public boolean apply(String property, Object data) {
                final MethodContext executionContext = (MethodContext) data;
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

    private static class ContextDelegatingPredicate implements DataPredicate {
        private final DataPredicate predicate;

        public ContextDelegatingPredicate(DataPredicate predicate) {
            this.predicate = predicate;
        }

        @Override
        public boolean apply(String property, Object data) {
            final TestContext testContext = (TestContext) data;
            return predicate.apply(property, testContext.getInspected());
        }

        @Override
        public String toString() {
            return predicate.toString();
        }
    }
}
