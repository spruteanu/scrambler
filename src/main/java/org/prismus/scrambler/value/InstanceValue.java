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

import org.prismus.scrambler.*;

import java.lang.reflect.Constructor;
import java.util.*;
import java.util.concurrent.Callable;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Instance value, used to populate/generate field values using provided definitions.
 *
 * @author Serge Pruteanu
 */
@SuppressWarnings("unused")
public class InstanceValue<T> extends Constant<T> implements Value<T> {

    private ValueDefinition definition;
    private Callable<ValueDefinition> definitionClosure;
    private ValuePredicate predicate;

    private Object type;
    private Collection<Value> constructorArguments;
    private final Map<InstanceFieldPredicate, Value> fieldValueMap;

    private Map<String, Field> fieldMap;
    private List<Value> constructorValues;
    private AtomicBoolean shouldBuild = new AtomicBoolean(true);
    private boolean generateAll = true;

    public InstanceValue() {
        this(null, null);
    }

    public InstanceValue(String type) {
        this(lookupType(type, false), null);
    }

    public InstanceValue(Class type) {
        this(type, null);
    }

    public InstanceValue(Class type, Collection<Value> constructorArguments) {
        this.type = type;
        this.constructorArguments = constructorArguments;
        this.constructorValues = new ArrayList<Value>();
        fieldValueMap = new LinkedHashMap<InstanceFieldPredicate, Value>();
    }

    public ValueDefinition getDefinition() {
        return definition;
    }

    public void setPredicate(ValuePredicate predicate) {
        this.predicate = predicate;
    }

    public ValuePredicate getPredicate() {
        return predicate;
    }

    public void setType(Class type) {
        this.type = type;
    }

    public void setConstructorValues(List<Value> constructorValues) {
        this.constructorValues = constructorValues;
    }

    boolean shouldBuildInstance() {
        return shouldBuild.get();
    }

    @Override
    public T next() {
        if (shouldBuildInstance()) {
            build(null);
            final Object valueType = lookupType();
            if (valueType instanceof Class) {
                definition.registerPredicateValue(ValuePredicates.predicateOf((Class) valueType), this);
            }
        }
        final T instance = checkCreateInstance();
        setValue(instance);
        populate(instance);
        return instance;
    }

    @SuppressWarnings("unchecked")
    public void setConstructorArguments(Collection constructorArguments) {
        this.constructorArguments = new ArrayList<Value>();
        if (constructorArguments != null) {
            for (final Object value : constructorArguments) {
                if (Value.class.isInstance(value)) {
                    this.constructorArguments.add((Value) value);
                } else {
                    this.constructorArguments.add(new Constant(value));
                }
            }
        }
    }

    public InstanceValue<T> withConstructorArguments(Collection constructorArguments) {
        this.constructorArguments = new ArrayList<Value>();
        setConstructorArguments(constructorArguments);
        return this;
    }

    public InstanceValue<T> withPredicate(ValuePredicate predicate) {
        this.predicate = predicate;
        return this;
    }

    public InstanceValue<T> withDefinitionClosure(AbstractDefinitionCallable definitionClosure) {
        this.definitionClosure = definitionClosure;
        return this;
    }

    public InstanceValue<T> registerFieldValue(String field, Value value) {
        fieldValueMap.put(new InstanceFieldPredicate(field), value);
        shouldBuild.set(true);
        return this;
    }

    public Value lookupFieldValue(ValuePredicate fieldPredicate) {
        Value result = null;
        for (final Map.Entry<InstanceFieldPredicate, Value> entry : fieldValueMap.entrySet()) {
            final String fieldName = entry.getKey().getProperty();
            final Value value = entry.getValue();
            if (fieldPredicate.apply(fieldName, value)) {
                result = value;
                break;
            }
        }
        return result;
    }

    public InstanceValue<T> usingDefinitions(ValueDefinition valueDefinition) {
        registerFieldValues(valueDefinition);
        checkDefinitionCreated();
        definition.usingDefinition(valueDefinition);
        shouldBuild.set(true);
        return this;
    }

    public InstanceValue<T> usingDefinitions(Map<Object, Object> valueDefinitions) {
        if (valueDefinitions != null && valueDefinitions.size() > 0) {
            checkDefinitionCreated();
            definition.definition(valueDefinitions);
            shouldBuild.set(true);
        }
        return this;
    }

    public InstanceValue<T> using(List<Value> constructorValues) {
        this.constructorValues = constructorValues;
        return this;
    }

    public InstanceValue<T> addConstructorValue(Value value) {
        constructorValues.add(value);
        return this;
    }

    public InstanceValue<T> usingValue(T value) {
        this.value = value;
        return this;
    }

    public InstanceValue<T> usingType(Class valueType) {
        this.type = valueType;
        return this;
    }

    public InstanceValue<T> usingType(String valueType) {
        this.type = valueType;
        return this;
    }

    public InstanceValue<T> generateAll() {
        generateAll = true;
        return this;
    }

    public InstanceValue<T> generateKnown() {
        generateAll = false;
        return this;
    }

    //------------------------------------------------------------------------------------------------------------------
    // Internal Methods
    //------------------------------------------------------------------------------------------------------------------
    void setDefinitionClosure(Callable<ValueDefinition> definitionClosure) {
        this.definitionClosure = definitionClosure;
        shouldBuild.set(true);
    }

    void registerFieldValues(ValueDefinition valueDefinition) {
        checkFieldMapCreated();

        final Map<String, Field> unresolvedProps = new HashMap<String, Field>(fieldMap);
        for (final Map.Entry<ValuePredicate, Value> entry : valueDefinition.getDefinitionMap().entrySet()) {
            final ValuePredicate predicate = entry.getKey();
            for (final Field field : fieldMap.values()) {
                final String propertyName = field.getName();
                if (!fieldValueMap.containsKey(new InstanceFieldPredicate(propertyName))) {
                    if (predicate.apply(propertyName, field.getValueType())) {
                        registerFieldValue(propertyName, entry.getValue());
                        break;
                    }
                }
            }
        }
        for (InstanceFieldPredicate fieldPredicate : fieldValueMap.keySet()) {
            unresolvedProps.remove(fieldPredicate.getProperty());
        }
        if (generateAll) {
            fieldValueMap.putAll(introspectTypes(valueDefinition, unresolvedProps));
        }
    }

    void checkDefinitionCreated() {
        if (definition == null) {
            definition = new ValueDefinition();
        }
    }

    @SuppressWarnings("unchecked")
    InstanceValue<T> build(ValueDefinition parent) {
        checkDefinitionCreated();
        checkFieldMapCreated();

        fieldValueMap.clear();
        definition.clearInternals();
        definition.setParent(parent);

        registerFieldValues(definition);
        definition.definition((Map) fieldValueMap);

        if (definitionClosure != null) {
            executeDefinitionClosure();
        }
        if (definitionClosure != null || fieldValueMap.size() > 0) {
            definition.build();
        }
        shouldBuild.set(false);
        return this;
    }

    void executeDefinitionClosure() {
        try {
            if (definitionClosure instanceof AbstractDefinitionCallable) {
                final AbstractDefinitionCallable callable = (AbstractDefinitionCallable) this.definitionClosure;
                if (callable.getDefinition() == null) {
                    callable.setDefinition(definition);
                }
            }
            definitionClosure.call();
        } catch (Exception e) {
            throw new RuntimeException("Failed to execute definition closure", e);
        }
    }

    Set<Class> getSupportedTypes() {
        final Set<Class> knownTypes = new HashSet<Class>();
        knownTypes.addAll(Types.getSupportedIncrementTypes());
        knownTypes.addAll(Types.getSupportedRandomTypes());
        return knownTypes;
    }

    @SuppressWarnings("unchecked")
    Map<InstanceFieldPredicate, Value> introspectTypes(ValueDefinition valueDefinition, Map<String, Field> unresolvedProps) {
        final Set<Class> supportedTypes = getSupportedTypes();
        final Map<InstanceFieldPredicate, Value> propertyValueMap = new LinkedHashMap<InstanceFieldPredicate, Value>();
        for (final Map.Entry<String, Field> entry : unresolvedProps.entrySet()) {
            final String propertyName = entry.getKey();
            final Field field = entry.getValue();
            final Class propertyType = field.getType();
            Value val = null;
            if (supportedTypes.contains(propertyType)) {
                val = ObjectScrambler.random(propertyType);
            } else if (propertyType.isArray() && supportedTypes.contains(propertyType.getComponentType())) {
                val = ObjectScrambler.random(propertyType, null);
            } else {
                if (Iterable.class.isAssignableFrom(propertyType) || Map.class.isAssignableFrom(propertyType)) {
                    continue;
                }
                final List<Value> ctorArgs = lookupConstructorArguments(valueDefinition, propertyType, supportedTypes);
                if (ctorArgs != null) {
                    val = new InstanceValue(propertyType, ctorArgs);
                }
            }
            if (val != null) {
                propertyValueMap.put(new InstanceFieldPredicate(propertyName), val);
            }
        }
        return propertyValueMap;
    }

    List<Value> lookupConstructorArguments(ValueDefinition valueDefinition, Class type, Set<Class> supportedTypes) {
        List<Value> result = null;
        try {
            final Map<ValuePredicate, Value> typeValueMap = valueDefinition.getDefinitionMap();
            for (final Constructor ctor : type.getConstructors()) {
                final Class[] types = ctor.getParameterTypes();
                if (types == null) {
                    return new ArrayList<Value>();
                }
                final List<Value> values = new ArrayList<Value>();
                for (final Class argType : types) {
                    Value val = typeValueMap.get(ValuePredicates.predicateOf(argType));
                    if (val == null && supportedTypes.contains(argType)) {
                        val = ObjectScrambler.random(argType);
                    }
                    if (val != null) {
                        values.add(val);
                    }
                }
                if (values.size() == types.length) {
                    result = values;
                    break;
                }
            }
        } catch (Exception ignore) { }
        return result;
    }

    void populate(Object instance) {
        final Map<String, Object> propertyObjectMap = new LinkedHashMap<String, Object>(fieldValueMap.size());
        for (Map.Entry<InstanceFieldPredicate, Value> entry : fieldValueMap.entrySet()) {
            propertyObjectMap.put(entry.getKey().getProperty(), entry.getValue().next());
        }
        populate(instance, propertyObjectMap);
    }

    void populate(Object instance, Map<String, Object> properties) {
        for (final Map.Entry<String, Object> entry : properties.entrySet()) {
            final String key = entry.getKey();
            final Object value = entry.getValue();
            try {
                setPropertyValue(instance, key, value);
            } catch (Exception e) {
                throw new RuntimeException(String.format("Failed to set instance: %s with property: %s; value: %s", instance, key, value), e);
            }
        }
    }

    Map<String, Field> checkFieldMapCreated() {
        if (fieldMap != null) {
            return fieldMap;
        }
        fieldMap = lookupFields(value != null ? value : type);
        return fieldMap;
    }

    Map<String, Field> lookupFields(Object instanceType) {
        final boolean isInstance = !(instanceType instanceof Class);
        final Map<String, Field> propertyDefinitionMap = new LinkedHashMap<String, Field>();

        final List<java.lang.reflect.Field> fields = new ArrayList<java.lang.reflect.Field>(100);
        if (isInstance) {
            lookupFields(instanceType.getClass(), fields);
        } else {
            lookupFields((Class<?>) instanceType, fields);
        }
        if (fields.size() > 0) {
            for (java.lang.reflect.Field field : fields) {
                final String name = field.getName();
                final Object value = isInstance ? getPropertyValue(field, instanceType) : null;
                propertyDefinitionMap.put(name, new Field(field, value));
            }
            propertyDefinitionMap.remove("class");
            propertyDefinitionMap.remove("metaClass");
        }
        return propertyDefinitionMap;
    }

    void setPropertyValue(Object instance, String propertyName, Object value) throws Exception {
        final Field field = fieldMap.get(propertyName);
        if (field != null) {
            field.setValue(instance, value);
        }
    }

    Object getPropertyValue(java.lang.reflect.Field field, Object instance) {
        Object value = null;
        try {
            field.setAccessible(true);
            value = field.get(instance);
        } catch (Exception ignore) { }
        return value;
    }

    @SuppressWarnings({"unchecked"})
    T checkCreateInstance() {
        T result = this.value;
        Object valueType = lookupType();
        if (valueType instanceof Class) {
            Object[] arguments = null;
            Class[] types = null;
            if (constructorValues != null && constructorValues.size() > 0) {
                arguments = new Object[constructorValues.size()];
                types = new Class[constructorValues.size()];
                for (int i = 0; i < constructorValues.size(); i++) {
                    final Value value = constructorValues.get(i);
                    final Object valueObject = value.next();
                    arguments[i] = valueObject;
                    types[i] = valueObject.getClass();
                }
            }
            result = (T) Util.createInstance((Class) valueType, arguments, types);
        }
        return result;
    }

    public Object lookupType() {
        Object valueType = type;
        if (valueType == null) {
            valueType = value;
        }
        if (valueType instanceof String) {
            valueType = lookupType((String) valueType, true);
        }
        return valueType;
    }

    static Class lookupType(String classType, boolean throwError) {
        Class result = null;
        try {
            result = Class.forName(((String) classType));
        } catch (ClassNotFoundException e) {
            if (throwError) {
                throw new IllegalStateException(String.format("Not found class for specified value: %s", classType), e);
            }
        }
        return result;
    }

    static class Field {
        final java.lang.reflect.Field field;
        final Object value;

        public Field(java.lang.reflect.Field field, Object value) {
            this.field = field;
            this.value = value;
            field.setAccessible(true);
        }

        public Object getValueType() {
            return value != null ? value : getType();
        }

        public String getName() {
            return field.getName();
        }

        public Class getType() {
            return field.getType();
        }

        void setValue(Object instance, Object value) throws IllegalAccessException {
            field.set(instance, value);
        }

    }

    static void lookupFields(Class<?> clazzType, List<java.lang.reflect.Field> fields) {
        final java.lang.reflect.Field[] declaredFields = clazzType.getDeclaredFields();
        for (java.lang.reflect.Field field : declaredFields) {
            if (!field.isSynthetic()) {
                fields.add(field);
            }
        }
        final Class<?> superclass = clazzType.getSuperclass();
        if (superclass != Object.class) {
            lookupFields(superclass, fields);
        }
    }

}
