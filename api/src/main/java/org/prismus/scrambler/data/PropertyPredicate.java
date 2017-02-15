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

package org.prismus.scrambler.data;

import org.prismus.scrambler.DataPredicate;

import java.util.regex.Pattern;

/**
 * Property matcher using regular expression. Used when an Instance definition is built to
 * match property by name. Property predicate can be expressed either by wildcard pattern
 * (that is converted into regular expression pattern) or provided by an explicit {@link Pattern}
 * Supported wildcard characters:
 * <li/>*   -> all
 * <li/>?   -> one character
 * Found regular expression characters are ignored (prefixed)
 *
 * @author Serge Pruteanu
 */
public class PropertyPredicate implements DataPredicate {
    private String wildcardPattern;
    private Pattern pattern;

    public PropertyPredicate() {
    }

    public PropertyPredicate(String wildcardPattern) {
        setPattern(wildcardPattern);
    }

    public PropertyPredicate(Pattern pattern) {
        withPattern(pattern);
    }

    @Override
    public boolean matches(String property, Object data) {
        return (pattern != null && property != null && pattern.matcher(property).matches()) || wildcardPattern.equalsIgnoreCase(property);
    }

    public void setPattern(Pattern pattern) {
        this.pattern = pattern;
        if (pattern != null) {
            wildcardPattern = pattern.pattern();
        }
    }

    public PropertyPredicate withPattern(Pattern pattern) {
        setPattern(pattern);
        return this;
    }

    public void setPattern(String wildcardPattern) {
        this.wildcardPattern = wildcardPattern;
        final String replacedWildcards = Util.replaceWildcards(wildcardPattern);
        if (replacedWildcards.charAt(0) == '^') {
            this.pattern = Pattern.compile(replacedWildcards, Pattern.CASE_INSENSITIVE);
        }
    }

    public Pattern getPattern() {
        return pattern;
    }

    @Override
    public String toString() {
        return String.format("Matches property(es): '%s'", wildcardPattern);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        PropertyPredicate that = (PropertyPredicate) o;
        return wildcardPattern.equalsIgnoreCase(that.wildcardPattern);
    }

    @Override
    public int hashCode() {
        return wildcardPattern.toLowerCase().hashCode();
    }

    public static PropertyPredicate of(String wildcardPattern) {
        return new PropertyPredicate(wildcardPattern);
    }

    public static PropertyPredicate of(Pattern pattern) {
        return new PropertyPredicate(pattern);
    }

}
