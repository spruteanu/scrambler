package org.prismus.scrambler.test;

import java.util.Arrays;

/**
 * todo: add description
 *
 * @author Serge Pruteanu
 */
public class MethodTestContext extends TestContext {
    private String methodName;
    private Object[] args;
    private long executionTime;

    MethodTestContext() {
    }

    public MethodTestContext(String methodName, Object[] args) {
        this.args = args;
    }

    public MethodTestContext(String methodName, Object inspected, Object[] args) {
        super(inspected);
        this.args = args;
    }

    public MethodTestContext(Object inspected, String message, Object[] args) {
        super(inspected, message);
        this.args = args;
    }

    public MethodTestContext withArguments(Object[] args) {
        this.args = args;
        return this;
    }

    public long getExecutionTime() {
        return executionTime;
    }

    MethodTestContext reportResults(long executionTime, Object inspected) {
        this.executionTime = executionTime;
        setInspected(inspected);
        return this;
    }

    MethodTestContext forMethod(String methodName) {
        this.methodName = methodName;
        return this;
    }

    public String getMethodName() {
        return methodName;
    }

    public Object[] getArguments() {
        return args;
    }

    @Override
    public String toString() {
        return String.format("%s; %s(%s) executed in: %d (ms)", super.toString(), methodName, args != null ? Arrays.asList(args) : "", executionTime);
    }

}
