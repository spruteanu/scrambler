package org.prismus.scrambler.property;

import org.apache.commons.lang.RandomStringUtils;

/**
 * @author Serge Pruteanu
 */
public class RandomString extends Constant<String> {
    private Integer count;
    private boolean includeLetters;
    private boolean includeNumbers;

    public RandomString() {
        this(null);
    }

    public RandomString(String value) {
        super(value);
    }

    public RandomString(String value, Integer count) {
        this(value, count, false, false);
    }

    public RandomString(String value, Integer count, boolean includeLetters) {
        this(value, count, includeLetters, false);
    }

    public RandomString(String value, Integer count, boolean includeLetters, boolean includeNumbers) {
        super(value);
        this.count = count;
    }

    public void setCount(Integer count) {
        this.count = count;
    }

    public void setIncludeLetters(boolean includeLetters) {
        this.includeLetters = includeLetters;
    }

    public void setIncludeNumbers(boolean includeNumbers) {
        this.includeNumbers = includeNumbers;
    }

    @Override
    public String next() {
        String value = super.next();
        if (value == null) {
            value = "RandomString";
        }
        final int count = checkCount(value);
        value = RandomStringUtils.random(count, 0, value.length(), includeLetters, includeNumbers, value.toCharArray());
        return value;
    }

    Integer checkCount(String value) {
        Integer count = this.count;
        if (count == null) {
            count = value.length();
        }
        return count;
    }
}
