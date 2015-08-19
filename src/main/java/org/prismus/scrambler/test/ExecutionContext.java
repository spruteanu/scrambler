package org.prismus.scrambler.test;

/**
 * todo: add description
 *
 * @author Serge Pruteanu
 */
public class ExecutionContext {
    protected Object inspected;
    private boolean passed;
    protected String message;

    public ExecutionContext() {
    }

    public ExecutionContext(Object inspected) {
        this.inspected = inspected;
    }

    public ExecutionContext(Object inspected, String message) {
        this.inspected = inspected;
        this.message = message;
    }

    public Object getInspected() {
        return inspected;
    }

    public String getMessage() {
        return message;
    }

    public boolean passed() {
        return passed;
    }

    void setInspected(Object inspected) {
        this.inspected = inspected;
    }

    void setPassed(boolean passed) {
        this.passed = passed;
    }

    void setMessage(String message) {
        this.message = message;
    }

}
