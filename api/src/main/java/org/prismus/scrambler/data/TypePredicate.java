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

/**
 * Type predicate, used to match definitions by class type
 *
 * @author Serge Pruteanu
 */
public class TypePredicate implements DataPredicate {
    private Class type;

    public TypePredicate() {
    }

    public TypePredicate(Class type) {
        this.type = type;
    }

    @SuppressWarnings("unchecked")
    @Override
    public boolean matches(String property, Object data) {
        return data instanceof Class ? type.isAssignableFrom((Class<?>) data) : type.isInstance(data);
    }

    public void setType(Class type) {
        this.type = type;
    }

    public TypePredicate withType(Class type) {
        this.type = type;
        return this;
    }

    public Class getType() {
        return type;
    }

    @Override
    public String toString() {
        return String.format("Is type of '%s'", type.toString());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        TypePredicate that = (TypePredicate) o;
        return type.equals(that.type);
    }

    @Override
    public int hashCode() {
        return type.hashCode();
    }

}
