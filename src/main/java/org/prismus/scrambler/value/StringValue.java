package org.prismus.scrambler.value;

/**
 * todo: add description
 *
 * @author Serge Pruteanu
 */
public class StringValue {
    public static IncrementalString increment(String self) {
        return new IncrementalString(self);
    }

    public static IncrementalString increment(String self, String pattern) {
        return new IncrementalString(self, pattern);
    }

    public static IncrementalString increment(String self, Integer index) {
        return new IncrementalString(self, index);
    }

    public static IncrementalString increment(String self, String pattern, Integer index) {
        return new IncrementalString(self, pattern, index);
    }

    public static ArrayValue<String> incrementArray(String self, Integer count) {
        return new ArrayValue<String>(String.class, count, new IncrementalString(self));
    }

    public static ArrayValue<String> incrementArray(String self, String pattern, Integer count) {
        return new ArrayValue<String>(String.class, count, new IncrementalString(self, pattern));
    }

    public static ArrayValue<String> incrementArray(String value, Integer index, Integer count) {
        return new ArrayValue<String>(String.class, count, new IncrementalString(value, index));
    }

    public static ArrayValue<String> incrementArray(String value, String pattern, Integer index, Integer count) {
        return new ArrayValue<String>(String.class, count, new IncrementalString(value, pattern, index));
    }

    public static RandomString random(String value) {
        return new RandomString(value);
    }

    public static RandomString random(String value, Integer count) {
        return new RandomString(value, count);
    }

    public static ArrayValue<String> randomArray(String value, Integer arrayCount) {
        return new ArrayValue<String>(String.class, arrayCount, random(value));
    }

    public static ArrayValue<String> randomArray(String value, Integer count, Integer arrayCount) {
        return new ArrayValue<String>(String.class, arrayCount, random(value, count));
    }

}
