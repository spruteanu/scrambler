package org.prismus.scrambler.test;

import org.prismus.scrambler.Value;
import org.prismus.scrambler.value.Constant;
import org.prismus.scrambler.value.ValueDefinition;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;
import java.util.concurrent.Callable;

/**
 * todo: add description
 *
 * @author Serge Pruteanu
 */
public class BoxTestSuite {
    private Value<Object> inspected;
    private Class inspectedType;

    private final List<Callable<TestContext>> executionsList = new ArrayList<Callable<TestContext>>();

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

    public ResultContext verify() {
        final ResultContext resultContext = new ResultContext();
        resultContext.setInspected(inspected.next());
        for (final Callable<TestContext> testContextCallable : executionsList) {
            TestContext testContext;
            try {
                testContext = testContextCallable.call();
            } catch (Exception e) {
                testContext = new TestContext(e, String.format("Failed execute test context: '%s'", testContextCallable)).verified(false);
            }
            resultContext.add(testContext);
        }
        return resultContext;
    }

    public MethodSuite of(String method, Class... args) throws NoSuchMethodException {
        final MethodSuite methodSuite = new MethodSuite(lookupMethod(inspectedType, method, args), args);
        executionsList.add(new MethodExecutionCallable(methodSuite));
        return methodSuite;
    }

    public MethodSuite of(String method, Object... args) throws NoSuchMethodException {
        final MethodSuite methodSuite = new MethodSuite(method, args);
        executionsList.add(new MethodExecutionCallable(methodSuite));
        return methodSuite;
    }

    public MethodSuite of(String method, Value... args) throws NoSuchMethodException {
        final MethodSuite methodSuite = new MethodSuite(method, args);
        executionsList.add(new MethodExecutionCallable(methodSuite));
        return methodSuite;
    }

    public BoxTestSuite expectField(String field, Expect expect) throws NoSuchFieldException {
        executionsList.add(new ExpectationsCallable(expect.forContext(
                new FieldContext(inspected, inspectedType.getDeclaredField(field)))));
        return this;
    }

    public BoxTestSuite expectField(String message, String field, Expect expect) throws NoSuchFieldException {
        executionsList.add(new ExpectationsCallable(expect.forContext(
                new FieldContext(inspected, inspectedType.getDeclaredField(field), message))));
        return this;
    }

    public static BoxTestSuite of(Object inspected) {
        return new BoxTestSuite(inspected);
    }

    static Method lookupMethod(Class<?> clazzType, String methodName, Class... args) throws NoSuchMethodException {
        final Method method;
        if (args == null) {
            final Set<Method> methods = new LinkedHashSet<Method>();
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
        private MethodContext context;
        private final List<Expect> expectList = new ArrayList<Expect>();

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
            context = new MethodContext();
        }

        public MethodSuite(String method, List<Object> args) {
            this.methodName = method;
            this.args = args;
            context = new MethodContext();
        }

        public MethodSuite scanDefinitions(String... definitions) {
            if (definitions != null) {
                valueDefinition.scanDefinitions(Arrays.asList(definitions));
            } else {
                valueDefinition.usingLibraryDefinitions();
            }
            return this;
        }

        public MethodSuite usingDefinitions(String... definitions) {
            valueDefinition.usingDefinitions(definitions);
            return this;
        }

        public MethodSuite thrown(Exception expected) {
            expectList.add(new Expect().isTypeOf(expected.getClass()).forContext(context));
            return this;
        }

        public MethodSuite thrown(Expect expect) {
            expectList.add(expect.forContext(context));
            return this;
        }

        public MethodSuite expectField(String field, Expect expect) throws NoSuchFieldException {
            expectList.add(expect.forContext(new FieldContext(inspected, inspectedType.getDeclaredField(field))));
            return this;
        }

        public MethodSuite expectReturn(Expect expect) {
            expectList.add(expect.forContext(context));
            return this;
        }

        public BoxTestSuite build() {
            return BoxTestSuite.this;
        }

        TestContext execute() throws CloneNotSupportedException {
            Method executedMethod = this.method;
            Object[] methodArgs = lookupMethodArguments();
            long start = 0;
            try {
                if (executedMethod == null) {
                    executedMethod = lookupMethod(methodArgs);
                }

                context.forMethod(executedMethod.getName()).withArguments(methodArgs);
                start = System.currentTimeMillis();

                final Object result = executedMethod.invoke(inspected, methodArgs);
                context.reportResults(System.currentTimeMillis() - start, result);
            } catch (NoSuchMethodException e) {
                throw new RuntimeException(String.format("Not found method: %s; arguments: %s; for instance: %s", methodName, Arrays.asList(methodArgs), inspected), e);
            } catch (IllegalAccessException e) {
                throw new RuntimeException(String.format("Failed execute method: %s for instance: %s with arguments: (%s)",
                        executedMethod, inspected, methodArgs == null ? "" : Arrays.asList(methodArgs)), e);
            } catch (InvocationTargetException e) {
                context.reportResults(System.currentTimeMillis() - start, e.getTargetException());
            }
            return verifyExpectations((MethodContext) context.clone());
        }

        TestContext verifyExpectations(MethodContext context) {
            if (expectList.size() == 0) {
                return context;
            }
            final ResultContext resultContext = new ResultContext().add(context);
            boolean passed = true;
            for (Expect expect : expectList) {
                final TestContext testContext = expect.verify();
                resultContext.add(testContext);
                passed &= testContext.passed();
            }
            context.verified(passed);
            resultContext.verified(passed);
            return resultContext;
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
                        arg = valueDefinition.lookupValue(null, (Class) arg);
                    }
                    if (arg instanceof Value) {
                        arg = ((Value) arg).next();
                    }
                    methodArgs[i] = arg;
                }
            }
            return methodArgs;
        }

        @Override
        public String toString() {
            return String.format("Method: %s(%s)", methodName, args != null ? args.toString() : "");
        }
    }

    private static class ExpectationsCallable implements Callable<TestContext> {
        private final Expect expect;

        private ExpectationsCallable(Expect expect) {
            this.expect = expect;
        }

        @Override
        public TestContext call() throws Exception {
            return expect.verify();
        }

        @Override
        public String toString() {
            return expect.toString();
        }
    }

    private static class MethodExecutionCallable implements Callable<TestContext> {
        private final MethodSuite methodSuite;

        private MethodExecutionCallable(MethodSuite methodSuite) {
            this.methodSuite = methodSuite;
        }

        @Override
        public TestContext call() throws Exception {
            return methodSuite.execute();
        }

        @Override
        public String toString() {
            return methodSuite.toString();
        }
    }

}
