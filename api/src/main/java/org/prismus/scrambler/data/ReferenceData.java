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

import org.prismus.scrambler.Data;
import org.prismus.scrambler.DataPredicate;
import org.prismus.scrambler.DataPredicates;

import java.util.regex.Pattern;

/**
 * Represents a reference in parent definitions, referencing either an instance ({@code predicate})
 * or reference is a field from instance ({@code fieldPredicate})
 *
 * @author Serge Pruteanu
 */
public class ReferenceData extends ConstantData<Object> {
    private DataDefinition definition;
    private DataPredicate predicate;
    private DataPredicate fieldPredicate;

    public ReferenceData(String fieldPredicate) {
        this((DataPredicate)null, DataPredicates.matchProperty(fieldPredicate));
    }

    public ReferenceData(DataPredicate fieldPredicate) {
        this((DataPredicate) null, fieldPredicate);
    }

    public ReferenceData(Pattern fieldPattern) {
        this((DataPredicate)null, PropertyPredicate.of(fieldPattern));
    }

    public ReferenceData(java.lang.Class type, java.lang.String fieldPredicate) {
        this(DataPredicates.isTypeOf(type), DataPredicates.matchProperty(fieldPredicate));
    }

    public ReferenceData(DataPredicate predicate, DataPredicate fieldPredicate) {
        this.predicate = predicate;
        this.fieldPredicate = fieldPredicate;
    }

    public ReferenceData(Pattern predicatePattern, Pattern fieldPattern) {
        this(PropertyPredicate.of(predicatePattern), PropertyPredicate.of(fieldPattern));
    }

    public ReferenceData(DataDefinition definition, Pattern pattern) {
        this(definition, PropertyPredicate.of(pattern));
    }

    public ReferenceData(DataDefinition definition, DataPredicate predicate) {
        this.definition = definition;
        this.predicate = predicate;
    }

    @Override
    protected Object doNext() {
        Object result = null;
        if (definition != null) {
            Data referencedInstance = null;

            if (predicate != null) {
                referencedInstance = definition.lookupData(predicate);
            }
            if (referencedInstance != null) {
                result = referencedInstance.get();
            }
            if (fieldPredicate != null) {
                Data referencedFieldData = null;
                if (referencedInstance instanceof InstanceData) {
                    referencedFieldData = ((InstanceData) referencedInstance).lookupData(fieldPredicate);
                }
                if (referencedFieldData == null && definition != null) {
                    referencedFieldData = definition.lookupData(fieldPredicate);
                }
                if (referencedFieldData != null) {
                    result = referencedFieldData.get();
                }
            }
        }
        return result;
    }

    public void setDefinition(DataDefinition definition) {
        this.definition = definition;
    }

    public void setPredicate(DataPredicate predicate) {
        this.predicate = predicate;
    }

    public void setFieldPredicate(DataPredicate fieldPredicate) {
        this.fieldPredicate = fieldPredicate;
    }

}
