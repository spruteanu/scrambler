package org.prismus.scrambler.value;

import org.apache.commons.lang.RandomStringUtils;

/**
 * @author Serge Pruteanu
 */
public class RandomString extends Constant<String> {
    private Integer count;
    private Boolean includeLetters;
    private Boolean includeNumbers;

    public RandomString() {
        this(null);
    }

    public RandomString(String value) {
        super(value);
    }

    public RandomString(String value, Integer count) {
        this(value, count, false, false);
    }

    public RandomString(String value, Integer count, Boolean includeLetters) {
        this(value, count, includeLetters, false);
    }

    public RandomString(String value, Integer count, Boolean includeLetters, Boolean includeNumbers) {
        super(value);
        this.count = count;
        this.includeLetters = includeLetters;
        this.includeNumbers = includeNumbers;
    }

    public void setCount(Integer count) {
        this.count = count;
    }

    public void setIncludeLetters(Boolean includeLetters) {
        this.includeLetters = includeLetters;
    }

    public void setIncludeNumbers(Boolean includeNumbers) {
        this.includeNumbers = includeNumbers;
    }

    @Override
    public String next() {
        String value = super.next();
        if (value == null) {
            value = "RandomString";
        }
        final int count = checkCount(value);
        final boolean letters = includeLetters == null ? false : includeLetters;
        final boolean numbers = includeNumbers == null ? false : includeNumbers;
        value = RandomStringUtils.random(count, 0, value.length(), letters, numbers, value.toCharArray());
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
