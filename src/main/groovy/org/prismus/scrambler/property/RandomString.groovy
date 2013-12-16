package org.prismus.scrambler.property

import groovy.transform.CompileStatic
import org.apache.commons.lang.RandomStringUtils

/**
 * @author Serge Pruteanu
 */
@CompileStatic
class RandomString extends Generic<String> {
    private Integer count
    private boolean includeLetters
    private boolean includeNumbers

    RandomString() {
        this(null, null)
    }

    RandomString(String name) {
        this(name, null)
    }

    RandomString(String name, String value) {
        super(name, value)
    }

    RandomString(String name, String value, Integer count) {
        this(name, value, count, false, false)
    }

    RandomString(String name, String value, Integer count, boolean includeLetters) {
        this(name, value, count, includeLetters, false)
    }

    RandomString(String name, String value, Integer count, boolean includeLetters, boolean includeNumbers) {
        super(name, value)
        this.count = count
    }

    void setCount(Integer count) {
        this.count = count
    }

    void setIncludeLetters(boolean includeLetters) {
        this.includeLetters = includeLetters
    }

    void setIncludeNumbers(boolean includeNumbers) {
        this.includeNumbers = includeNumbers
    }

    @Override
    String value() {
        String value = super.value()
        if (value == null) {
            value = getName()
        }
        if (value == null) {
            value = "RandomString"
        }
        final int count = checkCount(value)
        value = RandomStringUtils.random(count, 0, value.length(), includeLetters, includeNumbers, value.toCharArray())
        return value
    }

    Integer checkCount(String value) {
        Integer count = this.count
        if (count == null) {
            count = value.length()
        }
        return count
    }
}
