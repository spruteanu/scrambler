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

import org.prismus.scrambler.Value;
import org.prismus.scrambler.ValuePredicate;
import org.prismus.scrambler.ValuePredicates;

import java.util.regex.Pattern;

/**
 * Represents a reference in parent definitions, referencing either an instance ({@code predicate})
 * or reference is a field from instance ({@code fieldPredicate})
 *
 * @author Serge Pruteanu
 */
public class ReferenceValue extends Constant<Object> {
    private ValueDefinition definition;
    private ValuePredicate predicate;
    private ValuePredicate fieldPredicate;

    public ReferenceValue(String fieldPredicate) {
        this((ValuePredicate)null, ValuePredicates.predicateOf(fieldPredicate));
    }

    public ReferenceValue(ValuePredicate fieldPredicate) {
        this((ValuePredicate) null, fieldPredicate);
    }

    public ReferenceValue(Pattern fieldPattern) {
        this((ValuePredicate)null, PropertyPredicate.of(fieldPattern));
    }

    public ReferenceValue(java.lang.Class type, java.lang.String fieldPredicate) {
        this(ValuePredicates.typePredicate(type), ValuePredicates.predicateOf(fieldPredicate));
    }

    public ReferenceValue(ValuePredicate predicate, ValuePredicate fieldPredicate) {
        this.predicate = predicate;
        this.fieldPredicate = fieldPredicate;
    }

    public ReferenceValue(Pattern predicatePattern, Pattern fieldPattern) {
        this(PropertyPredicate.of(predicatePattern), PropertyPredicate.of(fieldPattern));
    }

    public ReferenceValue(ValueDefinition definition, Pattern pattern) {
        this(definition, PropertyPredicate.of(pattern));
    }

    public ReferenceValue(ValueDefinition definition, ValuePredicate predicate) {
        this.definition = definition;
        this.predicate = predicate;
    }

    @Override
    protected Object doNext() {
        Object result = null;
        if (definition != null) {
            Value referencedInstance = null;

            if (predicate != null) {
                referencedInstance = definition.lookupValue(predicate);
            }
            if (referencedInstance != null) {
                result = referencedInstance.get();
            }
            if (fieldPredicate != null) {
                Value referencedFieldValue = null;
                if (referencedInstance instanceof InstanceValue) {
                    referencedFieldValue = ((InstanceValue) referencedInstance).lookupValue(fieldPredicate);
                }
                if (referencedFieldValue == null && definition != null) {
                    referencedFieldValue = definition.lookupValue(fieldPredicate);
                }
                if (referencedFieldValue != null) {
                    result = referencedFieldValue.get();
                }
            }
        }
        return result;
    }

    public void setDefinition(ValueDefinition definition) {
        this.definition = definition;
    }

    public void setPredicate(ValuePredicate predicate) {
        this.predicate = predicate;
    }

    public void setFieldPredicate(ValuePredicate fieldPredicate) {
        this.fieldPredicate = fieldPredicate;
    }

}
