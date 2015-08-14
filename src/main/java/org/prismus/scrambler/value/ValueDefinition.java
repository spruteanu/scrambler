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

import java.util.*;
import java.util.regex.Pattern;

/**
 * Value definitions dictionary/builder class. Responsible for building predicates/values key/value pairs
 *
 * @author Serge Pruteanu
 */
@SuppressWarnings("unchecked")
public class ValueDefinition implements Cloneable {
    public static final String DEFAULT_DEFINITIONS_RESOURCE = "/org.prismus.scrambler.value.default-value-definition.groovy";

    private ValueDefinition parent;

    private Map<ValuePredicate, Value> definitionMap = new LinkedHashMap<ValuePredicate, Value>();
    private Map<ValuePredicate, InstanceValue> instanceValueMap = new LinkedHashMap<ValuePredicate, InstanceValue>();
    private Map<String, Object> contextMap = new LinkedHashMap<String, Object>();

    public ValueDefinition() {
    }

    public ValueDefinition(Map<Object, Value> definitionMap) {
        definition((Map) definitionMap);
    }

    public void setParent(ValueDefinition parent) {
        this.parent = parent;
    }

    public Map<ValuePredicate, Value> getDefinitionMap() {
        return definitionMap;
    }

    ValueDefinition clearInternals() {
        instanceValueMap.clear();
        final Set<ValuePredicate> removedSet = new LinkedHashSet<ValuePredicate>();
        for (ValuePredicate predicate : definitionMap.keySet()) {
            if (predicate instanceof InstanceFieldPredicate) {
                removedSet.add(predicate);
            }
        }
        for (ValuePredicate predicate : removedSet) {
            definitionMap.remove(predicate);
        }
        if (parent != null) {
            parent.clearInternals();
        }
        return this;
    }

    //------------------------------------------------------------------------------------------------------------------
    // Definitions Builder Methods
    //------------------------------------------------------------------------------------------------------------------
    public ValueDefinition definition(Value value) {
        Util.checkNullValue(value);
        final Object value1 = value.get();
        Util.checkNullValue(value1);
        registerPredicateValue(ValuePredicates.typePredicate(value1.getClass()), value);
        return this;
    }

    public ValueDefinition definition(InstanceValue value) {
        Util.checkNullValue(value);
        registerPredicateValue(new TypePredicate((Class) value.lookupType()), value);
        return this;
    }

    public ValueDefinition definition(Map<Object, Object> props) {
        Util.checkNullValue(props);
        for (final Map.Entry entry : props.entrySet()) {
            final Object key = entry.getKey();
            Util.checkNullValue(key);

            final Object value = entry.getValue();
            if (String.class.isInstance(key)) {
                if (value instanceof Value) {
                    definition((String) key, (Value) value);
                } else {
                    definition((String) key, value);
                }
            } else if (Pattern.class.isInstance(key)) {
                if (value instanceof Value) {
                    definition(new PropertyPredicate((Pattern) key), (Value) value);
                } else {
                    definition(new PropertyPredicate((Pattern) key), value);
                }
            } else if (key instanceof Class) {
                if (value instanceof Value) {
                    definition((Class) key, (Value) value);
                } else {
                    definition((Class) key, value);
                }
            } else if (ValuePredicate.class.isInstance(key)) {
                if (value instanceof Value) {
                    definition((ValuePredicate) key, (Value) value);
                } else {
                    definition((ValuePredicate) key, value);
                }
            } else {
                throw new IllegalArgumentException(String.format("Key should be of following types: [String, Class, ValuePredicate]; passed map: %s", props));
            }
        }
        return this;
    }

    public ValueDefinition constant(Object value) {
        Util.checkNullValue(value);
        registerPredicateValue(ValuePredicates.typePredicate(value.getClass()), new Constant(value));
        return this;
    }

    public ValueDefinition constant(Map<Object, Object> props) {
        Util.checkNullValue(props);
        for (final Map.Entry entry : props.entrySet()) {
            final Object key = entry.getKey();
            Util.checkNullValue(key);

            final Object value = entry.getValue();
            if (Value.class.isInstance(value)) {
                throw new IllegalArgumentException(String.format("Constant values can't be of Value type; passed map: %s", props));
            }
            if (String.class.isInstance(key)) {
                definition((String) key, new Constant(value));
            } else if (key instanceof Class) {
                definition((Class) key, new Constant(value));
            } else if (ValuePredicate.class.isInstance(key)) {
                definition((ValuePredicate) key, new Constant(value));
            } else {
                throw new IllegalArgumentException(String.format("Key should be of following types: [String, Class, ValuePredicate]; passed map: %s", props));
            }
        }
        return this;
    }

    public ValueDefinition definition(String propertyName, Object value) {
        registerPredicateValue(ValuePredicates.predicateOf(propertyName), new Constant(value));
        return this;
    }

    public ValueDefinition definition(Pattern pattern, Object value) {
        registerPredicateValue(PropertyPredicate.of(pattern), new Constant(value));
        return this;
    }

    public ValueDefinition definition(Class type, Object value) {
        Util.checkNullValue(type);
        registerPredicateValue(ValuePredicates.typePredicate(type), new Constant(value));
        return this;
    }

    public ValueDefinition definition(String propertyName, Value value) {
        Util.checkNullValue(value);
        if (value instanceof ReferenceValue) {
            ((ReferenceValue) value).setDefinition(this);
        }
        registerPredicateValue(ValuePredicates.predicateOf(propertyName), value);
        return this;
    }

    public ValueDefinition definition(Pattern pattern, Value value) {
        Util.checkNullValue(value);
        if (value instanceof ReferenceValue) {
            ((ReferenceValue) value).setDefinition(this);
        }
        registerPredicateValue(PropertyPredicate.of(pattern), value);
        return this;
    }

    public ValueDefinition definition(Class type, Value value) {
        Util.checkNullValue(type);
        Util.checkNullValue(value);
        registerPredicateValue(ValuePredicates.typePredicate(type), value);
        return this;
    }

    public ValueDefinition definition(ValuePredicate valuePredicate, Value value) {
        Util.checkNullValue(valuePredicate);
        Util.checkNullValue(value);
        registerPredicateValue(valuePredicate, value);
        return this;
    }

    public ValueDefinition definition(ValuePredicate valuePredicate, Object value) {
        Util.checkNullValue(valuePredicate);
        registerPredicateValue(valuePredicate, new Constant(value));
        return this;
    }

    public ValueDefinition reference(Class type) {
        Util.checkNullValue(type);
        final ValuePredicate predicate = ValuePredicates.typePredicate(type);
        registerPredicateValue(predicate, new ReferenceValue(this, predicate));
        return this;
    }

    public ValueDefinition reference(Class type, String parentPredicate) {
        reference(type, ValuePredicates.predicateOf(parentPredicate));
        return this;
    }

    public ValueDefinition reference(Class type, Class parentPredicate) {
        Util.checkNullValue(parentPredicate);
        reference(type, ValuePredicates.typePredicate(parentPredicate));
        return this;
    }

    public ValueDefinition reference(Class type, ValuePredicate parentPredicate) {
        Util.checkNullValue(type);
        final ValuePredicate predicate = ValuePredicates.typePredicate(type);
        registerPredicateValue(predicate, new ReferenceValue(this, parentPredicate));
        return this;
    }

    public ValueDefinition reference(String propertyName, Class parentPredicate) {
        Util.checkNullValue(parentPredicate);
        reference(ValuePredicates.predicateOf(propertyName), ValuePredicates.typePredicate(parentPredicate));
        return this;
    }

    public ValueDefinition reference(Pattern pattern, Class parentPredicate) {
        Util.checkNullValue(parentPredicate);
        reference(PropertyPredicate.of(pattern), ValuePredicates.typePredicate(parentPredicate));
        return this;
    }

    public ValueDefinition reference(String propertyName) {
        final ValuePredicate predicate = ValuePredicates.predicateOf(propertyName);
        registerPredicateValue(predicate, new ReferenceValue(this, predicate));
        return this;
    }

    public ValueDefinition reference(Pattern pattern) {
        final PropertyPredicate predicate = PropertyPredicate.of(pattern);
        registerPredicateValue(predicate, new ReferenceValue(this, predicate));
        return this;
    }

    public ValueDefinition reference(ValuePredicate valuePredicate, ValuePredicate parentPredicate) {
        Util.checkNullValue(valuePredicate);
        registerPredicateValue(valuePredicate, new ReferenceValue(this, parentPredicate));
        return this;
    }

    public ValueDefinition usingContext(Map<String, Object> contextMap) {
        if (contextMap != null) {
            this.contextMap = contextMap;
        }
        return this;
    }

    //------------------------------------------------------------------------------------------------------------------
    // Internal Methods
    //------------------------------------------------------------------------------------------------------------------
    ValueDefinition build() {
        for (final InstanceValue value : instanceValueMap.values()) {
            value.build(this);
        }
        return this;
    }

    void lookupRegisterInstanceValue(ValuePredicate valuePredicate, Value value) {
        if (InstanceValue.class.isInstance(value)) {
            final InstanceValue instanceValue = (InstanceValue) value;
            if (instanceValue.getDefinition() != this) {
                instanceValueMap.put(valuePredicate, instanceValue);
            }
        } else if (CollectionValue.class.isInstance(value)) {
            lookupRegisterInstanceValue(valuePredicate, ((CollectionValue) value).getInstance());
        } else if (value instanceof ArrayValue) {
            lookupRegisterInstanceValue(valuePredicate, ((ArrayValue) value).getInstance());
        }
    }

    public ValueDefinition registerPredicateValue(ValuePredicate valuePredicate, Value value) {
        lookupRegisterInstanceValue(valuePredicate, value);
        definitionMap.put(valuePredicate, value);
        return this;
    }

    public ValueDefinition usingDefinition(ValueDefinition definition) {
        contextMap.putAll(definition.contextMap);
        return usingDefinitions(definition.getDefinitionMap());
    }

    public ValueDefinition usingDefinitions(Map<ValuePredicate, Value> definitions) {
        definitionMap.putAll(definitions);
        return this;
    }

    public ValueDefinition usingDefinitions(String... definitions) {
        if (definitions != null) {
            for (String definition : definitions) {
                GroovyValueDefinition.Holder.instance.parseDefinition(this, definition);
            }
        }
        return this;
    }

    public Value lookupValue(ValuePredicate predicate) {
        Value result = definitionMap.get(predicate);
        if (result == null && parent != null) {
            result = parent.lookupValue(predicate);
        }
        return result;
    }

    public Value lookupValue(String property, Class type) {
        Value value = null;
        for (Map.Entry<ValuePredicate, Value> entry : definitionMap.entrySet()) {
            if (!isIterableOrMap(type) && entry.getKey().apply(property, type)) {
                value = entry.getValue();
                break;
            }
        }
        if (value instanceof InstanceTypeValue) {
            value = ((InstanceTypeValue) value).next(type);
        } else if (value instanceof RandomTypeValue) {
            value = ((RandomTypeValue) value).next(type);
        } else if (value instanceof IncrementalTypeValue) {
            value = ((IncrementalTypeValue) value).next(type);
        }
        return value;
    }

    boolean isIterableOrMap(Class type) {
        return Iterable.class.isAssignableFrom(type) || Map.class.isAssignableFrom(type);
    }

    public Object getContextProperty(String property) {
        return contextMap.get(property);
    }

    public Object getContextProperty(String property, Object defaultValue) {
        Object result = contextMap.get(property);
        if (result == null) {
            result = defaultValue;
        }
        return result;
    }

    @Override
    public Object clone() throws CloneNotSupportedException {
        return super.clone();
    }

}
