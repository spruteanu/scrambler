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

import org.prismus.scrambler.*;

import java.lang.reflect.Constructor;
import java.net.URL;
import java.util.*;
import java.util.concurrent.Callable;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Instance object, used to populate/generate field objects using provided definitions.
 *
 * @author Serge Pruteanu
 */
public class InstanceData<T> extends ConstantData<T> implements Data<T> {

    private DataDefinition definition;
    private DataDefinition defaultDefinition;
    private Callable<DataDefinition> definitionClosure;

    private Object type;
    private List constructorArguments;

    private final Map<InstanceFieldPredicate, Data> fieldDataMap;

    private Map<String, Field> fieldMap;
    private AtomicBoolean shouldBuild = new AtomicBoolean(true);
    private boolean generateAll = true;

    public InstanceData() {
        this(null, null);
    }

    public InstanceData(String type) {
        this(lookupType(type, false), null);
    }

    public InstanceData(Class type) {
        this(type, null);
    }

    public InstanceData(Class type, Collection<Data> constructorArguments) {
        ofType(type);
        if (constructorArguments != null) {
            this.constructorArguments = new ArrayList<Data>(constructorArguments);
        }
        fieldDataMap = new LinkedHashMap<InstanceFieldPredicate, Data>();
    }

    public DataDefinition getDefinition() {
        return definition;
    }

    boolean shouldBuildInstance() {
        return shouldBuild.get();
    }

    boolean hasFieldsDefined() {
        return fieldDataMap != null && fieldDataMap.size() > 0;
    }

    @Override
    public T next() {
        if (!hasFieldsDefined() && generateAll) {
            scanDefinitions(lookupType().toString() + DataDefinition.DEFINITION_SCRIPT_SUFFIX);
        }
        if (shouldBuildInstance()) {
            final Class dataType = lookupType();
            build(null);
            definition.definition(DataPredicates.isTypeOf(dataType), this);
        }
        final T instance = checkCreateInstance();
        setObject(instance);
        populate(instance);
        return instance;
    }

    public InstanceData<T> withConstructorData(List<Data> dataList) {
        this.constructorArguments = dataList;
        return this;
    }

    @SuppressWarnings("unchecked")
    public InstanceData<T> withConstructorArguments(Collection constructorArguments) {
        this.constructorArguments = new ArrayList<Data>();
        if (constructorArguments != null) {
            for (final Object obj : constructorArguments) {
                if (Data.class.isInstance(obj)) {
                    this.constructorArguments.add(obj);
                } else {
                    this.constructorArguments.add(new ConstantData(obj));
                }
            }
        }
        return this;
    }

    public InstanceData<T> withDefinitionClosure(AbstractDefinitionCallable definitionClosure) {
        this.definitionClosure = definitionClosure;
        return this;
    }

    public InstanceData<T> definition(String field, Data data) {
        fieldDataMap.put(new InstanceFieldPredicate(field), data);
        shouldBuild.set(true);
        return this;
    }

    public InstanceData<T> usingDefinitions(DataDefinition dataDefinition) {
        registerFieldData(dataDefinition);
        checkDefinitionCreated();
        definition.usingDefinition(dataDefinition);
        shouldBuild.set(true);
        return this;
    }

    public InstanceData<T> usingDefinitions(Map<Object, Object> dataDefinitions) {
        if (dataDefinitions != null && dataDefinitions.size() > 0) {
            checkDefinitionCreated();
            definition.definition(dataDefinitions);
            shouldBuild.set(true);
        }
        return this;
    }

    public InstanceData<T> usingDefinitions(String resource, String... resources) {
        checkDefinitionCreated();
        GroovyDataDefinition.Holder.instance.parseDefinition(definition, resource);
        if (resources != null) {
            for (String defResource : resources) {
                GroovyDataDefinition.Holder.instance.parseDefinition(definition, defResource);
            }
        }
        return this;
    }

    public InstanceData<T> scanDefinitions(String... resources) {
        checkDefinitionCreated();
        if (resources != null) {
            definition.scanDefinitions(Arrays.asList(resources));
        } else {
            definition.usingLibraryDefinitions();
        }
        if (hasFieldsDefined()) {
            shouldBuild.set(true);
        }
        return this;
    }

    /**
     * Scans and parses ALL library definitions
     */
    public InstanceData<T> usingLibraryDefinitions() {
        checkDefinitionCreated();
        definition.usingLibraryDefinitions();
        if (hasFieldsDefined()) {
            shouldBuild.set(true);
        }
        return this;
    }

    /**
     * Scans library definitions using provided {@code definitionMatcher} pattern and parses matched definition resources
     *
     * @param definitionMatcher    wildcard or reg-ex to match library definitions for parsing
     */
    public InstanceData<T> usingLibraryDefinitions(String definitionMatcher) {
        checkDefinitionCreated();
        definition.usingLibraryDefinitions(definitionMatcher);
        if (hasFieldsDefined()) {
            shouldBuild.set(true);
        }
        return this;
    }

    public InstanceData<T> usingDefaultDefinitions(DataDefinition defaultDefinition) {
        this.defaultDefinition = defaultDefinition;
        return this;
    }

    public InstanceData<T> usingDefaultDefinitions(String... definitions) {
        this.defaultDefinition = new DataDefinition().usingDefinitions(definitions);
        return this;
    }

    public InstanceData<T> usingInstance(T instance) {
        this.object = instance;
        if (instance != null) {
            ofType(lookupType());
        }
        return this;
    }

    public InstanceData<T> ofType(Class clazzType) {
        this.type = clazzType;
        if (clazzType != null && generateAll) {
            final String classDefinitionResource = clazzType.getName() + ".groovy";
            final URL resource = clazzType.getResource(classDefinitionResource);
            if (resource != null) {
                checkDefinitionCreated();
                GroovyDataDefinition.Holder.instance.parseDefinition(definition, classDefinitionResource);
            }
        }
        return this;
    }

    public InstanceData<T> ofType(String dataType) {
        return ofType(lookupType(dataType, true));
    }

    public InstanceData<T> generateAll() {
        generateAll = true;
        return this;
    }

    public InstanceData<T> generateKnown() {
        generateAll = false;
        return this;
    }

    public Data lookupData(DataPredicate fieldPredicate) {
        Data result = null;
        for (final Map.Entry<InstanceFieldPredicate, Data> entry : fieldDataMap.entrySet()) {
            final String fieldName = entry.getKey().getProperty();
            final Data data = entry.getValue();
            if (fieldPredicate.apply(fieldName, data)) {
                result = data;
                break;
            }
        }
        return result;
    }

    public Data[] lookupData(Class... clazzTypes) {
        if (clazzTypes == null || clazzTypes.length == 0) {
            throw new IllegalArgumentException("Class types can't be null or empty");
        }
        final Data[] datas = new Data[clazzTypes.length];
        for (int i = 0; i < clazzTypes.length; i++) {
            datas[i] = lookupData(new TypePredicate(clazzTypes[i]));
        }
        return datas;
    }

    //------------------------------------------------------------------------------------------------------------------
    // Internal Methods
    //------------------------------------------------------------------------------------------------------------------
    void registerFieldData(DataDefinition dataDefinition) {
        checkFieldMapCreated();

        final Map<String, Field> unresolvedProps = new HashMap<String, Field>(fieldMap);
        for (final Map.Entry<DataPredicate, Data> entry : dataDefinition.getDefinitionMap().entrySet()) {
            final DataPredicate predicate = entry.getKey();
            for (final Field field : fieldMap.values()) {
                final String propertyName = field.getName();
                if (!fieldDataMap.containsKey(new InstanceFieldPredicate(propertyName))) {
                    if (predicate.apply(propertyName, field.getDataType())) {
                        definition(propertyName, entry.getValue());
                        break;
                    }
                }
            }
        }
        for (InstanceFieldPredicate fieldPredicate : fieldDataMap.keySet()) {
            unresolvedProps.remove(fieldPredicate.getProperty());
        }
        if (generateAll) {
            fieldDataMap.putAll(lookupUnresolved(dataDefinition, unresolvedProps));
        }
    }

    void checkDefinitionCreated() {
        if (definition == null) {
            definition = new DataDefinition();
        }
    }

    @SuppressWarnings("unchecked")
    InstanceData<T> build(DataDefinition parent) {
        checkDefinitionCreated();
        checkFieldMapCreated();

        fieldDataMap.clear();
        definition.clearInternals();
        definition.setParent(parent);

        registerFieldData(definition);
        definition.definition((Map) fieldDataMap);

        if (definitionClosure != null) {
            executeDefinitionClosure();
        }
        if (definitionClosure != null || fieldDataMap.size() > 0) {
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

    void checkDefaultDefinitionCreated() {
        if (defaultDefinition == null) {
            usingDefaultDefinitions(DataDefinition.DEFAULT_DEFINITIONS_RESOURCE);
        }
    }

    @SuppressWarnings("unchecked")
    Map<InstanceFieldPredicate, Data> lookupUnresolved(DataDefinition dataDefinition, Map<String, Field> unresolvedProps) {
        checkDefaultDefinitionCreated();
        final Map<InstanceFieldPredicate, Data> propertyDataMap = new LinkedHashMap<InstanceFieldPredicate, Data>();
        for (final Map.Entry<String, Field> entry : unresolvedProps.entrySet()) {
            final String fieldName = entry.getKey();
            final Class fieldType = entry.getValue().getType();
            Data val = defaultDefinition.lookupData(fieldName, fieldType);
            if (val != null) {
                propertyDataMap.put(new InstanceFieldPredicate(fieldName), val);
            }
        }
        return propertyDataMap;
    }

    void populate(Object instance) {
        final Map<String, Object> propertyObjectMap = new LinkedHashMap<String, Object>(fieldDataMap.size());
        for (Map.Entry<InstanceFieldPredicate, Data> entry : fieldDataMap.entrySet()) {
            propertyObjectMap.put(entry.getKey().getProperty(), entry.getValue().next());
        }
        populate(instance, propertyObjectMap);
    }

    void populate(Object instance, Map<String, Object> properties) {
        for (final Map.Entry<String, Object> entry : properties.entrySet()) {
            final String key = entry.getKey();
            final Object obj = entry.getValue();
            try {
                populateField(instance, key, obj);
            } catch (Exception e) {
                throw new RuntimeException(String.format("Failed to set instance: %s with property: %s; object: %s", instance, key, obj), e);
            }
        }
    }

    Map<String, Field> checkFieldMapCreated() {
        if (fieldMap != null) {
            return fieldMap;
        }
        fieldMap = lookupFields(object != null ? object : type);
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
                final Object obj = isInstance ? getFieldObject(field, instanceType) : null;
                propertyDefinitionMap.put(name, new Field(field, obj));
            }
            propertyDefinitionMap.remove("class");
            propertyDefinitionMap.remove("metaClass");
        }
        return propertyDefinitionMap;
    }

    void populateField(Object instance, String propertyName, Object obj) throws Exception {
        final Field field = fieldMap.get(propertyName);
        if (field != null) {
            field.populate(instance, obj);
        }
    }

    Object getFieldObject(java.lang.reflect.Field field, Object instance) {
        Object obj = null;
        try {
            field.setAccessible(true);
            obj = field.get(instance);
        } catch (Exception ignore) { }
        return obj;
    }

    @SuppressWarnings("unchecked")
    List<Data> lookupConstructorArguments() {
        final List<Data> result = new ArrayList<Data>(constructorArguments.size());
        for (Object argument : constructorArguments) {
            if (argument instanceof Data) {
                result.add((Data) argument);
            } else {
                result.add(new ConstantData(argument));
            }
        }
        return result;
    }

    List<Data> lookupConstructorArguments(Class type) {
        List<Data> result = null;
        for (final Constructor ctor : type.getConstructors()) {
            final Class[] types = ctor.getParameterTypes();
            if (types == null) {
                return new ArrayList<Data>();
            }
            final List<Data> datas = new ArrayList<Data>();
            for (final Class argType : types) {
                Data val = definition.lookupData(null, argType);
                if (val == null) {
                    val = defaultDefinition.lookupData(null, argType);
                }
                if (val != null) {
                    datas.add(val);
                }
            }
            if (datas.size() == types.length) {
                result = datas;
                break;
            }
        }
        return result;
    }

    @SuppressWarnings({"unchecked"})
    List<Data> lookupConstructorData(Class clazzType) {
        List<Data> constructorDatas = null;
        if (constructorArguments != null && constructorArguments.size() > 0) {
            constructorDatas = lookupConstructorArguments();
        } else {
            Constructor constructor = null;
            try {
                constructor = clazzType.getConstructor();
            } catch (NoSuchMethodException ignore) { }
            if (constructor == null) {
                constructorDatas = lookupConstructorArguments(clazzType);
            }
        }
        return constructorDatas;
    }

    @SuppressWarnings({"unchecked"})
    T checkCreateInstance() {
        final Class clazzType = lookupType();
        final List<Data> constructorDatas = lookupConstructorData(clazzType);
        Object[] arguments = null;
        Class[] types = null;
        if (constructorDatas != null && constructorDatas.size() > 0) {
            arguments = new Object[constructorDatas.size()];
            types = new Class[constructorDatas.size()];
            for (int i = 0; i < constructorDatas.size(); i++) {
                final Data data = constructorDatas.get(i);
                final Object obj = data.next();
                arguments[i] = obj;
                types[i] = obj.getClass();
            }
        }
        return (T) Util.createInstance(clazzType, arguments, types);
    }

    public Class lookupType() {
        Object dataType = type;
        if (dataType == null) {
            dataType = object;
        }
        if (dataType instanceof String) {
            dataType = lookupType((String) dataType, true);
        } else {
            if (dataType != null && !(dataType instanceof Class)) {
                dataType = dataType.getClass();
            }
        }
        return (Class)dataType;
    }

    static Class lookupType(String classType, boolean throwError) {
        Class result = null;
        try {
            result = Class.forName(((String) classType));
        } catch (ClassNotFoundException e) {
            if (throwError) {
                throw new IllegalStateException(String.format("Not found class for specified object: %s", classType), e);
            }
        }
        return result;
    }

    static class Field {
        final java.lang.reflect.Field field;
        final Object object;

        public Field(java.lang.reflect.Field field, Object object) {
            this.field = field;
            this.object = object;
            field.setAccessible(true);
        }

        public Object getDataType() {
            return object != null ? object : getType();
        }

        public String getName() {
            return field.getName();
        }

        public Class getType() {
            return field.getType();
        }

        void populate(Object instance, Object obj) throws IllegalAccessException {
            field.set(instance, obj);
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
