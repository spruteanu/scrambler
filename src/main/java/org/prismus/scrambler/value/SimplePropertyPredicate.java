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

import org.prismus.scrambler.ValuePredicate;

/**
 * Property predicate, represents a simple property, matched by name
 *
 * @author Serge Pruteanu
 */
public class SimplePropertyPredicate implements ValuePredicate {
    private String property;

    public SimplePropertyPredicate(String property) {
        this.property = property;
    }

    @Override
    public boolean apply(String property, Object value) {
        return this.property.equalsIgnoreCase(property);
    }

    public String getProperty() {
        return property;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        SimplePropertyPredicate that = (SimplePropertyPredicate) o;
        return property.equals(that.property);
    }

    @Override
    public int hashCode() {
        return property.hashCode();
    }

    @Override
    public String toString() {
        return String.format("Matches property: '%s'", property);
    }
}
