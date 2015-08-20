package org.prismus.scrambler.test;

/**
 * todo: add description
 *
 * @author Serge Pruteanu
 */
public class TestContext {
    protected Object inspected;
    private boolean passed;
    protected String message;

    public TestContext() {
    }

    public TestContext(Object inspected) {
        this.inspected = inspected;
    }

    public TestContext(Object inspected, String message) {
        this.inspected = inspected;
        this.message = message;
    }

    Object getInspected() {
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
