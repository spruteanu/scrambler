package org.prismus.scrambler.test;

/**
 * todo: add description
 *
 * @author Serge Pruteanu
 */
public class MethodTestContext extends TestContext {
    private Object[] args;
    private long executionTime;

    MethodTestContext() {
    }

    public MethodTestContext(Object[] args) {
        this.args = args;
    }

    public MethodTestContext(Object inspected, Object[] args) {
        super(inspected);
        this.args = args;
    }

    public MethodTestContext(Object inspected, String message, Object[] args) {
        super(inspected, message);
        this.args = args;
    }

    public Object[] getArguments() {
        return args;
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

}
