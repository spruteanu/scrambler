package org.prismus.scrambler.test;

import org.prismus.scrambler.Value;
import org.prismus.scrambler.ValuePredicate;
import org.prismus.scrambler.ValuePredicates;
import org.prismus.scrambler.value.Constant;
import org.prismus.scrambler.value.ValueDefinition;

import java.lang.reflect.Method;
import java.util.*;
import java.util.regex.Pattern;

/**
 * todo: add description
 *
 * @author Serge Pruteanu
 */
public class BoxTestSuite {
    private Value<Object> inspected;
    private Class inspectedType;

    private final Map<Expectations, ExecutionContext> expectationsMap = new LinkedHashMap<Expectations, ExecutionContext>();

    public BoxTestSuite() {
    }

    public BoxTestSuite(Object inspected) {
        inspect(inspected);
    }

    public BoxTestSuite inspect(Object inspected) {
        return inspect(new Constant<Object>(inspected));
    }

    public BoxTestSuite inspect(Value<Object> inspected) {
        this.inspected = inspected;
        inspectedType = inspected.get().getClass();
        return this;
    }

    public ExecutionContext verify() {
        // todo: implement me
        return null;
    }

    public BoxTestSuite of(String method, Class... args) throws NoSuchMethodException {
        // todo: implement me
        lookupMethod(inspectedType, method, args);
        return this;
    }

    public BoxTestSuite of(String method, Value... args) throws NoSuchMethodException {
        // todo: implement me
//        lookupMethod(inspectedType, method, args);
        return this;
    }

    public BoxTestSuite of(String method, Object... args) throws NoSuchMethodException {
        // todo: implement me
//        lookupMethod(inspectedType, method, args);
        return this;
    }

    public BoxTestSuite expectField(String field, ValuePredicate valuePredicate) {
        return this;
    }

    public BoxTestSuite expectField(String message, String field, ValuePredicate valuePredicate) {
        return this;
    }

    public static BoxTestSuite of(Object inspected) {
        return new BoxTestSuite(inspected);
    }

    static Method lookupMethod(Class<?> clazzType, String methodName, Class... args) throws NoSuchMethodException {
        final Method method;
        if (args == null) {
            final LinkedHashSet<Method> methods = new LinkedHashSet<Method>();
            for (final Method m : clazzType.getMethods()) {
                if (m.getName().equalsIgnoreCase(methodName)) {
                    methods.add(m);
                }
            }
            final int size = methods.size();
            if (size > 1) {
                throw new IllegalArgumentException(String.format("More than one methods: %s found for class: %s", methodName, clazzType));
            } else {
                if (size == 0) {
                    throw new NoSuchMethodException(String.format("Not found method: %s for class: %s", methodName, clazzType));
                }
            }
            method = methods.iterator().next();
        } else {
            method = clazzType.getMethod(methodName, args);
        }
        return method;
    }

    public class MethodSuite {
        protected Method method;
        protected List<Value> args;

        private final ExecutionContext context = new ExecutionContext(inspected);
        protected final ValueDefinition valueDefinition = new ValueDefinition();

        public MethodSuite scanDefinitions(String... definitions) {
            return this;
        }

        public MethodSuite usingDefinitions(String definition, String... definitions) {
            return this;
        }

        public MethodSuite thrown(Exception expected) {
            return this;
        }

        public MethodSuite thrown(ValuePredicate expected) {
            return this;
        }

        public MethodSuite thrown(String message, Exception expected) {
            return this;
        }

        public MethodSuite thrown(String message, ValuePredicate expected) {
            return this;
        }

        public MethodSuite expectField(String field, ValuePredicate valuePredicate) {
            return this;
        }

        public MethodSuite expectField(String message, String field, ValuePredicate valuePredicate) {
            return this;
        }

        public BoxTestSuite finished() {
            return BoxTestSuite.this;
        }

        public MethodSuite expectReturn(ValuePredicate valuePredicate) {
            return this;
        }

        public MethodSuite expectReturn(String message, ValuePredicate valuePredicate) {
            return this;
        }

        ExecutionContext execute() {
            return null;
        }

    }

    class Expectations {
        private final Map<ValuePredicate, String> predicateMessageMap;

        public Expectations() {
            this(new LinkedHashMap<ValuePredicate, String>());
        }

        public Expectations(Map<ValuePredicate, String> predicateMessageMap) {
            this.predicateMessageMap = predicateMessageMap;
        }

        public boolean verify(ExecutionContext context) {
            boolean result = true;
            for (Map.Entry<ValuePredicate, String> entry : predicateMessageMap.entrySet()) {
                final ValuePredicate predicate = entry.getKey();
                Object value = context;
                final boolean passed = predicate.apply(null, value);
                // todo Serge: implement context verification
                result &= passed;
            }
            context.setPassed(result);
            return result;
        }

        public Expectations typePredicate(String message, Class clazzType) {
            predicateMessageMap.put(ValuePredicates.typePredicate(clazzType), message);
            return this;
        }

        public Expectations typePredicate(Class clazzType) {
            return typePredicate(null, clazzType);
        }

        public Expectations typePredicate(String message, String clazzType) throws ClassNotFoundException {
            predicateMessageMap.put(ValuePredicates.typePredicate(clazzType), message);
            return this;
        }

        public Expectations typePredicate(String clazzType) throws ClassNotFoundException {
            return typePredicate(null, clazzType);
        }

        public Expectations predicateOf(String message, String propertyWildcard, Class clazzType) {
            predicateMessageMap.put(ValuePredicates.predicateOf(propertyWildcard, clazzType), message);
            return this;
        }

        public Expectations predicateOf(String propertyWildcard, Class clazzType) {
            return predicateOf(null, propertyWildcard, clazzType);
        }

        public Expectations predicateOf(String message, Pattern pattern, Class clazzType) {
            predicateMessageMap.put(ValuePredicates.predicateOf(pattern, clazzType), message);
            return this;
        }

        public Expectations predicateOf(Pattern pattern, Class clazzType) {
            return predicateOf(null, pattern, clazzType);
        }

        public Expectations predicateOf(String message, Pattern pattern) {
            predicateMessageMap.put(ValuePredicates.predicateOf(pattern), message);
            return this;
        }

        public Expectations predicateOf(Pattern pattern) {
            return predicateOf(null, pattern);
        }

        public Expectations predicateOf(String message, String propertyWildcard) {
            predicateMessageMap.put(ValuePredicates.predicateOf(propertyWildcard), message);
            return this;
        }

        public Expectations predicateOf(String propertyWildcard) {
            return predicateOf(null, propertyWildcard);
        }

        public Expectations typeFilterOf(String message, Pattern pattern) {
            predicateMessageMap.put(ValuePredicates.typeFilterOf(pattern), message);
            return this;
        }

        public Expectations typeFilterOf(Pattern pattern) {
            return typeFilterOf(null, pattern);
        }

        public Expectations isNull(String message) {
            predicateMessageMap.put(ValuePredicates.isNull(), message);
            return this;
        }

        public Expectations isNull() {
            return isNull(null);
        }

        public Expectations isNotNull(String message) {
            predicateMessageMap.put(ValuePredicates.isNotNull(), message);
            return this;
        }

        public Expectations isNotNull() {
            return isNotNull(null);
        }

        public Expectations equalsTo(String message, Object object) {
            predicateMessageMap.put(ValuePredicates.equalsTo(object), message);
            return this;
        }

        public Expectations equalsTo(Object object) {
            return equalsTo(null, object);
        }

        public Expectations isSame(String message, Object object) {
            predicateMessageMap.put(ValuePredicates.isSame(object), message);
            return this;
        }

        public Expectations isSame(Object object) {
            return isSame(null, object);
        }

        public Expectations any(String message, Set<Object> values) {
            predicateMessageMap.put(ValuePredicates.any(values), message);
            return this;
        }

        public Expectations any(Set<Object> values) {
            return any(null, values);
        }

        public Expectations any(String message, Object... values) {
            return any(message, new LinkedHashSet<Object>(Arrays.asList(values)));
        }

        public Expectations any(Object... values) {
            return any(null, new LinkedHashSet<Object>(Arrays.asList(values)));
        }

        public Expectations any(String message, Collection<Object> values) {
            return any(message, new LinkedHashSet<Object>(values));
        }

        public Expectations any(final Collection<Object> values) {
            return any(null, new LinkedHashSet<Object>(values));
        }

        public <N extends Comparable> Expectations between(String message, final N min, final N max) {
            predicateMessageMap.put(ValuePredicates.between(min, max), message);
            return this;
        }

        public <N extends Comparable> Expectations between(final N min, final N max) {
            return between(null, min, max);
        }

        public Expectations inTime(String message, final long expectedTime) {
            predicateMessageMap.put(new ValuePredicate() {
                @Override
                public boolean apply(String property, Object value) {
                    ExecutionContext executionContext = (ExecutionContext) value;
                    final long inspected = (Long) executionContext.getInspected();
                    return expectedTime <= inspected;
                }
            }, message);
            return this;
        }

        public Expectations inTime(long expectedTime) {
            return inTime(null, expectedTime);
        }

    }
}
