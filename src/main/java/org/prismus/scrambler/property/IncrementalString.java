package org.prismus.scrambler.property;

/**
 * @author Serge Pruteanu
 */
public class IncrementalString extends Constant<String> {
    private static final String DEFAULT_PATTERN = "%s%010d";
    private static final int DEFAULT_STEP = 1;

    private int index;
    private String pattern;

    public IncrementalString() {
        this(null, null, DEFAULT_PATTERN, DEFAULT_STEP);
    }

    public IncrementalString(String name) {
        this(name, null, DEFAULT_PATTERN, DEFAULT_STEP);
    }

    public IncrementalString(String name, String value) {
        this(name, value, DEFAULT_PATTERN, DEFAULT_STEP);
    }

    public IncrementalString(String name, String value, String pattern) {
        this(name, value, pattern, DEFAULT_STEP);
    }

    public IncrementalString(String name, String value, Integer index) {
        this(name, value, DEFAULT_PATTERN, index);
    }

    public IncrementalString(String name, String value, String pattern, Integer index) {
        super(name, value);
        this.pattern = pattern;
        this.index = index != null ? index : DEFAULT_STEP;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public void setPattern(String pattern) {
        this.pattern = pattern;
    }

    @Override
    public String value() {
        String value = super.value();
        if (value == null) {
            value = getName();
        }
        if (value == null) {
            value = "RandomString";
        }
        return String.format(checkPattern(), value, nextIndex());
    }

    public int getIndex() {
        return index;
    }

    protected int nextIndex() {
        return index++;
    }

    String checkPattern() {
        String pattern = this.pattern;
        if (pattern == null) {
            pattern = DEFAULT_PATTERN;
        }
        return pattern;
    }
}
