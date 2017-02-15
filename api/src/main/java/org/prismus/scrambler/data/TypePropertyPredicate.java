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

import java.util.regex.Pattern;

/**
 * Data Predicate that will match property by name and by type
 *
 * @author Serge Pruteanu
 */
public class TypePropertyPredicate extends PropertyPredicate {
    private Class type;

    public TypePropertyPredicate() {
    }

    public TypePropertyPredicate(Class type) {
        this.type = type;
    }

    public TypePropertyPredicate(String wildcardPattern, Class type) {
        super(wildcardPattern);
        this.type = type;
    }

    public TypePropertyPredicate(Pattern pattern, Class type) {
        super(pattern);
        this.type = type;
    }

    public TypePropertyPredicate withType(Class type) {
        this.type = type;
        return this;
    }

    @Override
    public boolean matches(String property, Object data) {
        return super.matches(property, data) && type.isInstance(data);
    }

    @Override
    public String toString() {
        return String.format("%s and type: '%s'", super.toString(), type);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        if (!super.equals(o)) {
            return false;
        }
        TypePropertyPredicate that = (TypePropertyPredicate) o;
        return type.equals(that.type);
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + type.hashCode();
        return result;
    }
}
