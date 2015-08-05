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

import org.prismus.scrambler.beanutils.ConstructorUtils;

import java.util.*;

/**
 * @author Serge Pruteanu
 */
public abstract class Util {

    private static final String NOT_DEFINED_MSG = "not defined";
    private static final String FAILED_CREATE_INSTANCE0_MSG = "Failed to create instance of type: %s, arguments: %s";
    private static final String FAILED_CREATE_INSTANCE_MSG = "Failed to create instance of type: %s, arguments: %s, types: %s";

    private static final Set<Character> PREFIXED_CHAR_SET = new HashSet<Character>(Arrays.asList('+', '(', ')', '^', '$', '.', '{', '}', '[', ']', '|', '\\'));

    @SuppressWarnings({"unchecked"})
    public static Object createInstance(Class clazzType, Object[] arguments) {
        try {
            return ConstructorUtils.invokeConstructor(clazzType, arguments);
        } catch (Exception e) {
            throw new RuntimeException(String.format(FAILED_CREATE_INSTANCE0_MSG,
                    clazzType, checkNotDefinedMessage(arguments)
            ), e);
        }
    }

    @SuppressWarnings({"unchecked"})
    public static Object createInstance(Class clazzType,
                                        Object[] arguments,
                                        Class[] classes) {
        try {
            return ConstructorUtils.invokeConstructor(clazzType, arguments, classes);
        } catch (Exception e) {
            throw new RuntimeException(String.format(FAILED_CREATE_INSTANCE_MSG,
                    clazzType, checkNotDefinedMessage(arguments), checkNotDefinedMessage(classes)
            ), e);
        }
    }

    private static Object checkNotDefinedMessage(Object[] arguments) {
        return arguments != null ? Arrays.asList(arguments) : NOT_DEFINED_MSG;
    }

    public static void checkNullValue(Object value) {
        if (value == null) {
            throw new IllegalArgumentException("Value can't be null");
        }
    }

    public static void checkEmpty(String value) {
        if (value == null || value.length() == 0) {
            throw new IllegalArgumentException("Value can't be null or empty");
        }
    }

    public static void checkPositiveCount(Integer count) {
        if (count != null && count < 0) {
            throw new IllegalArgumentException(String.format("Count should be a positive number: %s", count));
        }
    }

    public static String replaceWildcards(String wildcardPattern) {
        final StringBuilder builder = new StringBuilder();
        builder.append('^');
        boolean replaced = false;
        final int length = wildcardPattern.length();
        for (int i = 0; i < length; ++i) {
            final char ch = wildcardPattern.charAt(i);
            if (ch == '*') {
                builder.append(".*");
                replaced = true;
            } else if (ch == '?') {
                builder.append(".");
                replaced = true;
            } else if (PREFIXED_CHAR_SET.contains(ch)) {
                builder.append('\\').append(ch);
            } else {
                builder.append(ch);
            }
        }
        builder.append('$');
        return replaced ? builder.toString() : wildcardPattern;
    }

}
