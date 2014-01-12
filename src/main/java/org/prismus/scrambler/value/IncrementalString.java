package org.prismus.scrambler.value;

/**
 * @author Serge Pruteanu
 */
public class IncrementalString extends Constant<String> {
    private static final String DEFAULT_PATTERN = "%s%010d";
    private static final int DEFAULT_STEP = 1;

    private Integer index;
    private String pattern;

    public IncrementalString() {
        this(null, DEFAULT_PATTERN, DEFAULT_STEP);
    }

    public IncrementalString(String value) {
        this(value, DEFAULT_PATTERN, DEFAULT_STEP);
    }

    public IncrementalString(String value, String pattern) {
        this(value, pattern, DEFAULT_STEP);
    }

    public IncrementalString(String value, Integer index) {
        this(value, DEFAULT_PATTERN, index);
    }

    public IncrementalString(String value, String pattern, Integer index) {
        super(value);
        this.pattern = pattern;
        this.index = index != null ? index : DEFAULT_STEP;
    }

    public void setIndex(Integer index) {
        this.index = index;
    }

    public void setPattern(String pattern) {
        this.pattern = pattern;
    }

    @Override
    public String next() {
        String value = super.next();
        if (value == null) {
            value = "RandomString";
        }
        return String.format(checkPattern(), value, nextIndex());
    }

    public int getIndex() {
        return index;
    }

    protected Integer nextIndex() {
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
