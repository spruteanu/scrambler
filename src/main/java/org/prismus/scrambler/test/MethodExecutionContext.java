package org.prismus.scrambler.test;

/**
 * todo: add description
 *
 * @author Serge Pruteanu
 */
public class MethodExecutionContext extends ExecutionContext {
    private Object[] args;

    public MethodExecutionContext() {
    }

    public MethodExecutionContext(Object[] args) {
        this.args = args;
    }

    public MethodExecutionContext(Object inspected, Object[] args) {
        super(inspected);
        this.args = args;
    }

    public MethodExecutionContext(Object inspected, String message, Object[] args) {
        super(inspected, message);
        this.args = args;
    }

    public Object[] getArguments() {
        return args;
    }

}
