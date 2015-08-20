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

    private final List<Expectations> expectationsList = new ArrayList<Expectations>();

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

    public TestContext verify() {
        // todo: implement me
        return null;
    }

    public MethodSuite of(String method, Class... args) throws NoSuchMethodException {
        final MethodSuite methodSuite = new MethodSuite(lookupMethod(inspectedType, method, args), args);
//        methodSuites.add(methodSuite);
        return methodSuite;
    }

    public MethodSuite of(String method, Value... args) throws NoSuchMethodException {
        final MethodSuite methodSuite = new MethodSuite(method, args);
//        methodSuites.add(methodSuite);
        return methodSuite;
    }

    public MethodSuite of(String method, Object... args) throws NoSuchMethodException {
        final MethodSuite methodSuite = new MethodSuite(method, args);
//        methodSuites.add(methodSuite);
        return methodSuite;
    }

    public BoxTestSuite expectField(String field, Expectations expectations) throws NoSuchFieldException {
        expectationsList.add(expectations.forContext(new FieldContext(inspected, inspectedType.getDeclaredField(field))));
        return this;
    }

    public BoxTestSuite expectField(String message, String field, Expectations expectations) throws NoSuchFieldException {
        expectationsList.add(expectations.forContext(new FieldContext(inspected, inspectedType.getDeclaredField(field), message)));
        return this;
    }

    void verifyContext(List<Expectations> expectationsList) {
        boolean passed = true;
        for (Expectations expectations : expectationsList) {
            final TestContext testContext = expectations.verify();
//            if (context != testContext) {
//                testContext.setPassed(result);
//            }
            passed &= testContext.passed();
        }
//        context.setPassed(passed);
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
        private MethodTestContext context;
        private final List<Expectations> expectationsList = new ArrayList<Expectations>();

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
            context = new MethodTestContext();
        }

        public MethodSuite(String method, List<Object> args) {
            this.methodName = method;
            this.args = args;
            context = new MethodTestContext();
        }

        void setContext(MethodTestContext context) {
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
            expectationsList.add(new Expectations().typePredicate(expected.getClass()).forContext(context));
            return this;
        }

        public MethodSuite thrown(Expectations expectations) {
            expectationsList.add(expectations.forContext(context));
            return this;
        }

        public MethodSuite expectField(String field, Expectations expectations) throws NoSuchFieldException {
            expectationsList.add(expectations.forContext(new FieldContext(inspected, inspectedType.getDeclaredField(field))));
            return this;
        }

        public MethodSuite expectReturn(Expectations expectations) {
            expectationsList.add(expectations.forContext(context));
            return this;
        }

        public BoxTestSuite finished() {
            return BoxTestSuite.this;
        }

        TestContext execute() {
            Method executedMethod = this.method;
            Object[] methodArgs = lookupMethodArguments();
            long start = 0;
            try {
                if (executedMethod == null) {
                    executedMethod = lookupMethod(methodArgs);
                }

                context.withArguments(methodArgs);
                start = System.currentTimeMillis();

                final Object result = executedMethod.invoke(inspected, methodArgs);
                context.reportResults(System.currentTimeMillis() - start, result);
            } catch (NoSuchMethodException e) {
                throw new RuntimeException(String.format("Not found method: %s; arguments: %s; for instance: %s", methodName, Arrays.asList(methodArgs), inspected), e);
            } catch (IllegalAccessException e) {
                throw new RuntimeException(String.format("Failed execute method: %s for instance: %s with arguments: (%s)",
                        executedMethod, inspected, methodArgs == null ? "Void": Arrays.asList(methodArgs)), e);
            } catch (InvocationTargetException e) {
                context.reportResults(System.currentTimeMillis() - start, e.getTargetException());
            }

            verifyContext(expectationsList);
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
