package org.prismus.scrambler.value;

/**
 * @author Serge Pruteanu
 */
public class RandomString extends Constant<String> {
    private final java.util.Random random;
    private Integer count;
    private Boolean includeLetters;
    private Boolean includeNumbers;

    public RandomString() {
        this(null);
    }

    public RandomString(String value) {
        super(value);
        random = new java.util.Random();
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
        random = new java.util.Random();
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
        value = random(count, 0, value.length(), letters, numbers, value.toCharArray());
        return value;
    }

    Integer checkCount(String value) {
        Integer count = this.count;
        if (count == null) {
            count = value.length();
        }
        return count;
    }

    /**
     * THIS IS A COPY from org.apache.commons.lang.RandomStringUtils.random(int count, int start, int end, boolean letters, boolean numbers, char[] chars, Random random)
     * <p/>
     * -----------------------------------------------------------------------------------------------------------------
     * <p/>
     * <p>Creates a random string based on a variety of options, using
     * supplied source of randomness.</p>
     * <p/>
     * <p>If start and end are both {@code 0}, start and end are set
     * to {@code ' '} and {@code 'z'}, the ASCII printable
     * characters, will be used, unless letters and numbers are both
     * {@code false}, in which case, start and end are set to
     * {@code 0} and {@code Integer.MAX_VALUE}.
     * <p/>
     * <p>If set is not {@code null}, characters between start and
     * end are chosen.</p>
     * <p/>
     * <p>This method accepts a user-supplied {@link Random}
     * instance to use as a source of randomness. By seeding a single
     * {@link Random} instance with a fixed seed and using it for each call,
     * the same random sequence of strings can be generated repeatedly
     * and predictably.</p>
     *
     * @param count   the length of random string to create
     * @param start   the position in set of chars to start at
     * @param end     the position in set of chars to end before
     * @param letters only allow letters?
     * @param numbers only allow numbers?
     * @param chars   the set of chars to choose randoms from, must not be empty.
     *                If {@code null}, then it will use the set of all chars.
     * @return the random string
     * @throws ArrayIndexOutOfBoundsException if there are not
     *                                        {@code (end - start) + 1} characters in the set array.
     * @throws IllegalArgumentException       if {@code count} &lt; 0 or the provided chars array is empty.
     * @since 2.0
     */
    String random(int count, int start, int end, final boolean letters, final boolean numbers, final char[] chars) {
        if (count == 0) {
            return "";
        } else if (count < 0) {
            throw new IllegalArgumentException("Requested random string length " + count + " is less than 0.");
        }
        if (chars != null && chars.length == 0) {
            throw new IllegalArgumentException("The chars array must not be empty");
        }
        if (start == 0 && end == 0) {
            if (chars != null) {
                end = chars.length;
            } else {
                if (!letters && !numbers) {
                    end = Integer.MAX_VALUE;
                } else {
                    end = 'z' + 1;
                    start = ' ';
                }
            }
        } else {
            if (end <= start) {
                throw new IllegalArgumentException("Parameter end (" + end + ") must be greater than start (" + start + ")");
            }
        }
        final char[] buffer = new char[count];
        final int gap = end - start;
        while (count-- != 0) {
            char ch;
            if (chars == null) {
                ch = (char) (random.nextInt(gap) + start);
            } else {
                ch = chars[random.nextInt(gap) + start];
            }
            if (letters && Character.isLetter(ch)
                    || numbers && Character.isDigit(ch)
                    || !letters && !numbers) {
                if (ch >= 56320 && ch <= 57343) {
                    if (count == 0) {
                        count++;
                    } else {
                        // low surrogate, insert high surrogate after putting it in
                        buffer[count] = ch;
                        count--;
                        buffer[count] = (char) (55296 + random.nextInt(128));
                    }
                } else if (ch >= 55296 && ch <= 56191) {
                    if (count == 0) {
                        count++;
                    } else {
                        // high surrogate, insert low surrogate before putting it in
                        buffer[count] = (char) (56320 + random.nextInt(128));
                        count--;
                        buffer[count] = ch;
                    }
                } else if (ch >= 56192 && ch <= 56319) {
                    // private high surrogate, no effing clue, so skip it
                    count++;
                } else {
                    buffer[count] = ch;
                }
            } else {
                count++;
            }
        }
        return new String(buffer);
    }

}
