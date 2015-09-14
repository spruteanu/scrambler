/*
 * Data Scrambler, Data Generation API
 * Copyright (c) 2015, Sergiu Prutean. All rights reserved.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3.0 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library.
 */

package org.prismus.scrambler.value;

/**
 * @author Serge Pruteanu
 */
public class RandomString extends Constant<String> {
    private String template;
    private final java.util.Random random;
    private Integer count;
    private Integer maxCount;

    public RandomString() {
        this(null, null);
    }

    public RandomString(String value) {
        this(value, null);
    }

    public RandomString(String value, Integer count) {
        super(value);
        this.template = value;
        this.count = count;
        random = new java.util.Random();
    }

    public RandomString maxCount(Integer maxCount) {
        this.maxCount = maxCount;
        return this;
    }

    public void setCount(Integer count) {
        this.count = count;
    }

    @Override
    public String get() {
        return doNext();
    }

    @Override
    protected String doNext() {
        String value = template;
        if (value == null) {
            value = this.value;
        }
        if (value == null) {
            value = "RandomString";
        }
        final int count = checkCount(value);
        value = random(count, 0, value.length(), true, true, value.toCharArray());
        return value;
    }

    Integer checkCount(String value) {
        Integer count = this.count;
        if (count == null && maxCount != null) {
            count = random.nextInt(maxCount) + 1;
        }
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
     * <p>This method accepts a user-supplied {@link java.util.Random}
     * instance to use as a source of randomness. By seeding a single
     * {@link java.util.Random} instance with a fixed seed and using it for each call,
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
