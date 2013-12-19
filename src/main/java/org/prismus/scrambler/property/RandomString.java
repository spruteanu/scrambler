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
        this(null, null);
    }

    public RandomString(String name) {
        this(name, null);
    }

    public RandomString(String name, String value) {
        super(name, value);
    }

    public RandomString(String name, String value, Integer count) {
        this(name, value, count, false, false);
    }

    public RandomString(String name, String value, Integer count, boolean includeLetters) {
        this(name, value, count, includeLetters, false);
    }

    public RandomString(String name, String value, Integer count, boolean includeLetters, boolean includeNumbers) {
        super(name, value);
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
    public String value() {
        String value = super.value();
        if (value == null) {
            value = getName();
        }
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
