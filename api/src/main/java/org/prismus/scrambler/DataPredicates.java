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
 * Data predicates facade class, exposes {@link DataPredicate} types implementations
 *
 * @author Serge Pruteanu
 */
public class DataPredicates {

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

    public static DataPredicate matchProperty(String propertyWildcard) {
        Util.checkEmpty(propertyWildcard);
        final String predicatePattern = Util.replaceWildcards(propertyWildcard);
        final DataPredicate propertyPredicate;
        if (propertyWildcard.equals(predicatePattern)) {
            propertyPredicate = new SimplePropertyPredicate(propertyWildcard);
        } else {
            propertyPredicate = new PropertyPredicate(propertyWildcard);
        }
        return propertyPredicate;
    }

    public static DataPredicate matchesTypes(Pattern pattern) {
        return new TypeFilterPredicate(pattern);
    }

    public static DataPredicate isNull() {
        return new DataPredicate() {
            @Override
            public boolean apply(String property, Object value) {
                return null == value;
            }

            @Override
            public String toString() {
                return "Data is NULL";
            }
        };
    }

    public static DataPredicate isNotNull() {
        return new DataPredicate() {
            @Override
            public boolean apply(String property, Object value) {
                return null != value;
            }

            @Override
            public String toString() {
                return "Data is NOT NULL";
            }
        };
    }

    public static DataPredicate equalsTo(final Object object) {
        return new DataPredicate() {
            @Override
            public boolean apply(String property, Object value) {
                return object.equals(value);
            }

            @Override
            public String toString() {
                return String.format("Data equals to: '%s'", object);
            }
        };
    }

    public static DataPredicate isSame(final Object object) {
        return new DataPredicate() {
            @Override
            public boolean apply(String property, Object value) {
                return object == value;
            }

            @Override
            public String toString() {
                return String.format("Data is same to: '%s'", object);
            }
        };
    }

    public static DataPredicate isAny(final Set<Object> values) {
        return new DataPredicate() {
            @Override
            public boolean apply(String property, Object value) {
                return values.contains(value);
            }

            @Override
            public String toString() {
                return String.format("Data is any of: '%s'", values);
            }
        };
    }

    public static DataPredicate isAny(final Object... values) {
        return isAny(new LinkedHashSet<Object>(Arrays.asList(values)));
    }

    public static DataPredicate isAny(final Collection<Object> values) {
        return isAny(new LinkedHashSet<Object>(values));
    }

    public static <N extends Comparable> DataPredicate between(final N min, final N max) {
        return new DataPredicate() {
            @SuppressWarnings("unchecked")
            @Override
            public boolean apply(String property, Object value) {
                Comparable comparedValue = (Comparable) value;
                return (min == null || min.compareTo(comparedValue) >= 0) && (max == null || max.compareTo(comparedValue) >= 0);
            }

            @Override
            public String toString() {
                return String.format("Data is between: '%s and %s'", min, max);
            }
        };
    }

}
