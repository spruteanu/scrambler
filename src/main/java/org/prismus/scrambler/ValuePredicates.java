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

import java.util.regex.Pattern;

/**
 * Value predicates facade class, exposes {@link ValuePredicate} types implementations
 *
 * @author Serge Pruteanu
 */
public class ValuePredicates {

    public static TypePredicate predicateOf(Class clazzType) {
        return new TypePredicate(clazzType);
    }

    public static TypePredicate typePredicate(String clazzType) throws ClassNotFoundException {
        return new TypePredicate(Class.forName(clazzType));
    }

    public static TypePropertyPredicate predicateOf(String propertyWildcard, Class clazzType) {
        return new TypePropertyPredicate(propertyWildcard, clazzType);
    }

    public static TypePropertyPredicate predicateOf(Pattern pattern, Class clazzType) {
        return new TypePropertyPredicate(pattern, clazzType);
    }

    public static PropertyPredicate predicateOf(Pattern pattern) {
        return new PropertyPredicate(pattern);
    }

    public static ValuePredicate predicateOf(String propertyWildcard) {
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

}
