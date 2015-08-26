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

package org.prismus.scrambler;

import org.prismus.scrambler.value.*;

import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.regex.Pattern;

/**
 * Value predicates facade class, exposes {@link ValuePredicate} types implementations
 *
 * @author Serge Pruteanu
 */
public class ValuePredicates {

    public static TypePredicate isTypeOf(Class clazzType) {
        return new TypePredicate(clazzType);
    }

    public static TypePredicate isTypeOf(String clazzType) throws ClassNotFoundException {
        return new TypePredicate(Class.forName(clazzType));
    }

    public static TypePropertyPredicate matches(String propertyWildcard, Class clazzType) {
        return new TypePropertyPredicate(propertyWildcard, clazzType);
    }

    public static TypePropertyPredicate matches(Pattern pattern, Class clazzType) {
        return new TypePropertyPredicate(pattern, clazzType);
    }

    public static PropertyPredicate matchProperty(Pattern pattern) {
        return new PropertyPredicate(pattern);
    }

    public static ValuePredicate matchProperty(String propertyWildcard) {
        Util.checkEmpty(propertyWildcard);
        final String predicatePattern = Util.replaceWildcards(propertyWildcard);
        final ValuePredicate propertyPredicate;
        if (propertyWildcard.equals(predicatePattern)) {
            propertyPredicate = new SimplePropertyPredicate(propertyWildcard);
        } else {
            propertyPredicate = new PropertyPredicate(propertyWildcard);
        }
        return propertyPredicate;
    }

    public static ValuePredicate matchesTypes(Pattern pattern) {
        return new TypeFilterPredicate(pattern);
    }

    public static ValuePredicate isNull() {
        return new ValuePredicate() {
            @Override
            public boolean apply(String property, Object value) {
                return null == value;
            }

            @Override
            public String toString() {
                return "Value is NULL";
            }
        };
    }

    public static ValuePredicate isNotNull() {
        return new ValuePredicate() {
            @Override
            public boolean apply(String property, Object value) {
                return null != value;
            }

            @Override
            public String toString() {
                return "Value is NOT NULL";
            }
        };
    }

    public static ValuePredicate equalsTo(final Object object) {
        return new ValuePredicate() {
            @Override
            public boolean apply(String property, Object value) {
                return object.equals(value);
            }

            @Override
            public String toString() {
                return String.format("Value equals to: '%s'", object);
            }
        };
    }

    public static ValuePredicate isSame(final Object object) {
        return new ValuePredicate() {
            @Override
            public boolean apply(String property, Object value) {
                return object == value;
            }

            @Override
            public String toString() {
                return String.format("Value is same to: '%s'", object);
            }
        };
    }

    public static ValuePredicate isAny(final Set<Object> values) {
        return new ValuePredicate() {
            @Override
            public boolean apply(String property, Object value) {
                return values.contains(value);
            }

            @Override
            public String toString() {
                return String.format("Value is any of: '%s'", values);
            }
        };
    }

    public static ValuePredicate isAny(final Object... values) {
        return isAny(new LinkedHashSet<Object>(Arrays.asList(values)));
    }

    public static ValuePredicate isAny(final Collection<Object> values) {
        return isAny(new LinkedHashSet<Object>(values));
    }

    public static <N extends Comparable> ValuePredicate between(final N min, final N max) {
        return new ValuePredicate() {
            @SuppressWarnings("unchecked")
            @Override
            public boolean apply(String property, Object value) {
                Comparable comparedValue = (Comparable) value;
                return (min == null || min.compareTo(comparedValue) >= 0) && (max == null || max.compareTo(comparedValue) >= 0);
            }

            @Override
            public String toString() {
                return String.format("Value is between: '%s and %s'", min, max);
            }
        };
    }

}