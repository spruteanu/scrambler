package org.prismus.scrambler.test;

import org.prismus.scrambler.Value;
import org.prismus.scrambler.value.Constant;
import org.prismus.scrambler.value.ValueDefinition;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;

/**
 * todo: add description
 *
 * @author Serge Pruteanu
 */
public class BoxTestSuite {
    private Value<Object> inspected;
    private Class inspectedType;

    private final Map<Expectations, ExecutionContext> fieldExpectationMap = new LinkedHashMap<Expectations, ExecutionContext>();
    private final List<MethodSuite> methodSuites = new ArrayList<MethodSuite>();

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
        methodSuites.add(new MethodSuite(lookupMethod(inspectedType, method, args), args));
        return this;
    }

    public BoxTestSuite of(String method, Value... args) throws NoSuchMethodException {
        methodSuites.add(new MethodSuite(method, args));
        return this;
    }

    public BoxTestSuite of(String method, Object... args) throws NoSuchMethodException {
        methodSuites.add(new MethodSuite(method, args));
        return this;
    }

    public BoxTestSuite expectField(String field, Expectations expectations) throws NoSuchFieldException {
        fieldExpectationMap.put(expectations, new FieldContext(inspected, inspectedType.getDeclaredField(field)));
        return this;
    }

    public BoxTestSuite expectField(String message, String field, Expectations expectations) throws NoSuchFieldException {
        fieldExpectationMap.put(expectations, new FieldContext(inspected, inspectedType.getDeclaredField(field), message));
        return this;
    }

    void verifyContext(ExecutionContext context, Map<Expectations, ExecutionContext> expectationMap) {
        boolean passed = true;
        for (Map.Entry<Expectations, ExecutionContext> entry : expectationMap.entrySet()) {
            final Expectations expectations = entry.getKey();
            final ExecutionContext executionContext = entry.getValue();
            final boolean result = expectations.verify(executionContext);
            if (context != executionContext) {
                executionContext.setPassed(result);
            }
            passed &= result;
        }
        context.setPassed(passed);
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
        private final String methodName;
        private Method method;
        private final List<Object> args;

        private final ValueDefinition valueDefinition = new ValueDefinition();
        private ExecutionContext context;
        private final Map<Expectations, ExecutionContext> expectationMap = new LinkedHashMap<Expectations, ExecutionContext>();

        public MethodSuite(Method method, Object... args) {
            this(method, args != null ? Arrays.asList(args) : null);
        }

        public MethodSuite(String method, Object... args) {
            this(method, args != null ? Arrays.asList(args) : null);
        }

        public MethodSuite(Method method, List<Object> args) {
            this.method = method;
            this.methodName = method.getName();
            this.args = args;
            context = new ExecutionContext();
        }

        public MethodSuite(String method, List<Object> args) {
            this.methodName = method;
            this.args = args;
            context = new ExecutionContext();
        }

        void setContext(ExecutionContext context) {
            this.context = context;
        }

        public MethodSuite scanDefinitions(String... definitions) {
            if (definitions != null) {
                valueDefinition.scanDefinitions(Arrays.asList(definitions));
            } else {
                valueDefinition.scanLibraryDefinitions(null);
            }
            return this;
        }

        public MethodSuite usingDefinitions(String... definitions) {
            valueDefinition.usingDefinitions(definitions);
            return this;
        }

        public MethodSuite thrown(Exception expected) {
            expectationMap.put(new Expectations().typePredicate(expected.getClass()), context);
            return this;
        }

        public MethodSuite thrown(Expectations expectations) {
            expectationMap.put(expectations, context);
            return this;
        }

        public MethodSuite expectField(String field, Expectations expectations) throws NoSuchFieldException {
            expectationMap.put(expectations, new FieldContext(inspected, inspectedType.getDeclaredField(field)));
            return this;
        }

        public MethodSuite expectReturn(Expectations expectations) {
            expectationMap.put(expectations, context);
            return this;
        }

        public BoxTestSuite finished() {
            return BoxTestSuite.this;
        }

        ExecutionContext execute() {
            Method executedMethod = this.method;
            Object[] methodArgs = lookupMethodArguments();
            try {
                if (executedMethod == null) {
                    executedMethod = lookupMethod(methodArgs);
                }
                final Object result = executedMethod.invoke(inspected, methodArgs);
                context.setInspected(result);
            } catch (NoSuchMethodException e) {
                throw new RuntimeException(String.format("Not found method: %s for instance: %s", methodName, inspected), e);
            } catch (IllegalAccessException e) {
                throw new RuntimeException(String.format("Failed execute method: %s for instance: %s with arguments: (%s)",
                        executedMethod, inspected, methodArgs == null ? "Void": Arrays.asList(methodArgs)), e);
            } catch (InvocationTargetException e) {
                context.setInspected(e.getTargetException());
            }

            verifyContext(context, expectationMap);
            return context;
        }


        @SuppressWarnings("unchecked")
        Method lookupMethod(Object[] methodArgs) throws NoSuchMethodException {
            Class[] methodArgTypes = null;
            if (methodArgs != null) {
                methodArgTypes = new Class[methodArgs.length];
                for (int i = 0; i < methodArgs.length; i++) {
                    methodArgTypes[i] = methodArgs[i].getClass();
                }
            }
            return inspectedType.getDeclaredMethod(methodName, methodArgTypes);
        }

        Object[] lookupMethodArguments() {
            Object[] methodArgs = null;
            if (args != null) {
                methodArgs = new Object[args.size()];
                for (int i = 0; i < methodArgs.length; i++) {
                    Object arg = args.get(i);
                    if (arg instanceof Class) {
//            valueDefinition.build(); // todo Serge: ensure that definitions are built before lookup
                        arg = valueDefinition.lookupValue(null, (Class)arg);
                    }
                    if (arg instanceof Value) {
                        arg = ((Value) arg).next();
                    }
                    methodArgs[i] =  arg;
                }
            }
            return methodArgs;
        }

    }

}
