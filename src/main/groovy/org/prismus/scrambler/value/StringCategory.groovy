package org.prismus.scrambler.value

import groovy.transform.CompileStatic

/**
 * todo: add description
 *
 * @author Serge Pruteanu
 */
@CompileStatic
class StringCategory {

    static IncrementalString increment(String self, String pattern = null, Integer index = null) {
        return new IncrementalString(self, pattern, index)
    }

    static ArrayValue<String> incrementArray(String self, Integer count) {
        return new ArrayValue<String>(String.class, count, new IncrementalString(self))
    }

    static ArrayValue<String> incrementArray(String self, String pattern, Integer count) {
        return new ArrayValue<String>(String.class, count, new IncrementalString(self, pattern))
    }

    static ArrayValue<String> incrementArray(String value, Integer index, Integer count) {
        return new ArrayValue<String>(String.class, count, new IncrementalString(value, index))
    }

    static ArrayValue<String> incrementArray(String value, String pattern, Integer index, Integer count) {
        return new ArrayValue<String>(String.class, count, new IncrementalString(value, pattern, index))
    }

    static RandomString random(String value, Integer count = null) {
        return new RandomString(value, count)
    }

    static ArrayValue<String> randomArray(String value, Integer arrayCount = null, Integer count = null) {
        return new ArrayValue<String>(String.class, arrayCount, random(value, count))
    }

}
