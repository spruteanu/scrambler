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

import org.prismus.scrambler.data.*;

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
            public boolean matches(String property, Object data) {
                return null == data;
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
            public boolean matches(String property, Object data) {
                return null != data;
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
            public boolean matches(String property, Object data) {
                return object.equals(data);
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
            public boolean matches(String property, Object data) {
                return object == data;
            }

            @Override
            public String toString() {
                return String.format("Data is same to: '%s'", object);
            }
        };
    }

    public static DataPredicate isAny(final Set<Object> dataSet) {
        return new DataPredicate() {
            @Override
            public boolean matches(String property, Object data) {
                return dataSet.contains(data);
            }

            @Override
            public String toString() {
                return String.format("Data is any of: '%s'", dataSet);
            }
        };
    }

    public static DataPredicate isAny(final Object... data) {
        return isAny(new LinkedHashSet<Object>(Arrays.asList(data)));
    }

    public static DataPredicate isAny(final Collection<Object> data) {
        return isAny(new LinkedHashSet<Object>(data));
    }

    public static <N extends Comparable> DataPredicate between(final N min, final N max) {
        return new DataPredicate() {
            @SuppressWarnings("unchecked")
            @Override
            public boolean matches(String property, Object data) {
                Comparable comparedData = (Comparable) data;
                return (min == null || min.compareTo(comparedData) >= 0) && (max == null || max.compareTo(comparedData) >= 0);
            }

            @Override
            public String toString() {
                return String.format("Data is between: '%s and %s'", min, max);
            }
        };
    }

}
