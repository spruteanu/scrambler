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

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.*;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.regex.Pattern;
import java.util.zip.ZipEntry;

/**
 * Value definitions dictionary/builder class. Responsible for building predicates/values key/value pairs
 *
 * @author Serge Pruteanu
 */
@SuppressWarnings("unchecked")
public class ValueDefinition implements Cloneable {
    public static final String DEFINITION_SCRIPT_SUFFIX = "-definition.groovy";
    public static final String DEFAULT_DEFINITIONS_RESOURCE = "/org.prismus.scrambler.value.default-definition.groovy";
    public static final String META_INF_ANCHOR = "META-INF/MANIFEST.MF";

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
        registerPredicateValue(ValuePredicates.isTypeOf(value1.getClass()), value);
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
        registerPredicateValue(ValuePredicates.isTypeOf(value.getClass()), new Constant(value));
        return this;
    }

    public ValueDefinition constant(String propertyName, Object value) {
        Util.checkNullValue(value);
        registerPredicateValue(ValuePredicates.matchProperty(propertyName), new Constant(value));
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
        registerPredicateValue(ValuePredicates.matchProperty(propertyName), new Constant(value));
        return this;
    }

    public ValueDefinition definition(Pattern pattern, Object value) {
        registerPredicateValue(PropertyPredicate.of(pattern), new Constant(value));
        return this;
    }

    public ValueDefinition definition(Class type, Object value) {
        Util.checkNullValue(type);
        registerPredicateValue(ValuePredicates.isTypeOf(type), new Constant(value));
        return this;
    }

    public ValueDefinition definition(String propertyName, Value value) {
        Util.checkNullValue(value);
        if (value instanceof ReferenceValue) {
            ((ReferenceValue) value).setDefinition(this);
        }
        registerPredicateValue(ValuePredicates.matchProperty(propertyName), value);
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
        registerPredicateValue(ValuePredicates.isTypeOf(type), value);
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

    void registerPredicateValue(ValuePredicate valuePredicate, Value value) {
        lookupRegisterInstanceValue(valuePredicate, value);
        definitionMap.put(valuePredicate, value);
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
        final ValuePredicate predicate = ValuePredicates.isTypeOf(type);
        registerPredicateValue(predicate, new ReferenceValue(this, predicate));
        return this;
    }

    public ValueDefinition reference(Class type, String parentPredicate) {
        reference(type, ValuePredicates.matchProperty(parentPredicate));
        return this;
    }

    public ValueDefinition reference(Class type, Class parentPredicate) {
        Util.checkNullValue(parentPredicate);
        reference(type, ValuePredicates.isTypeOf(parentPredicate));
        return this;
    }

    public ValueDefinition reference(Class type, ValuePredicate parentPredicate) {
        Util.checkNullValue(type);
        final ValuePredicate predicate = ValuePredicates.isTypeOf(type);
        registerPredicateValue(predicate, new ReferenceValue(this, parentPredicate));
        return this;
    }

    public ValueDefinition reference(String propertyName, Class parentPredicate) {
        Util.checkNullValue(parentPredicate);
        reference(ValuePredicates.matchProperty(propertyName), ValuePredicates.isTypeOf(parentPredicate));
        return this;
    }

    public ValueDefinition reference(Pattern pattern, Class parentPredicate) {
        Util.checkNullValue(parentPredicate);
        reference(PropertyPredicate.of(pattern), ValuePredicates.isTypeOf(parentPredicate));
        return this;
    }

    public ValueDefinition reference(String propertyName) {
        final ValuePredicate predicate = ValuePredicates.matchProperty(propertyName);
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
            build();
        }
        return this;
    }

    public ValueDefinition scanDefinitions(List<String> definitions) {
        int foundCount = 0;
        for (String definition : definitions) {
            final URL url = getClass().getResource(definition);
            if (url == null) {
                if (!new File(definition).exists()) {
                    continue;
                }
            } // not found resources filtered, parse definition
            GroovyValueDefinition.Holder.instance.parseDefinition(this, definition);
            foundCount++;
        }
        if (foundCount > 0) {
            build();
        }
        return this;
    }

    public ValueDefinition scanDefinitions(String resource, String... resources) {
        final ArrayList<String> resourceList = new ArrayList<String>();
        resourceList.add(resource);
        if (resources != null) {
            resourceList.addAll(Arrays.asList(resources));
        }
        scanDefinitions(resourceList);
        return this;
    }

    public ValueDefinition scanLibraryDefinitions(String definitionMatcher) {
        return scanLibraryDefinitions(definitionMatcher, Holder.libraryDefinitionsCache);
    }

    public ValueDefinition usingContext(Map<String, Object> contextMap) {
        if (contextMap != null) {
            this.contextMap = contextMap;
        }
        return this;
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

    public Value lookupValue(ValuePredicate predicate) {
        Value result = definitionMap.get(predicate);
        if (result == null && parent != null) {
            result = parent.lookupValue(predicate);
        }
        return result;
    }

    public List<Value> lookupValues(Class type, Class... types) {
        final ArrayList<Class> list = new ArrayList<Class>();
        list.add(type);
        if (types != null) {
            list.addAll(Arrays.asList(types));
        }
        return lookupValues(list);
    }

    public List<Value> lookupValues(List<Class> types) {
        final ArrayList<Value> results = new ArrayList<Value>(types.size());
        for (Class type : types) {
            results.add(lookupValue(null, type));
        }
        return results;
    }

    public Value lookupValue(String property, Class type) {
        if (definitionMap.isEmpty()) {
            scanDefinitions(DEFAULT_DEFINITIONS_RESOURCE);
        }
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
        if (value == null && parent != null) {
            parent.lookupValue(property, type);
        }
        return value;
    }

    public ValueDefinition definitions() {
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

    ValueDefinition scanLibraryDefinitions(String definitionMatcher, Set<String> foundResources) {
        final List<String> matchedResources = matchValueDefinitions(definitionMatcher, foundResources);
        String jarFileName = null;
        JarFile jarFile = null;
        for (String matchedResource : matchedResources) {
            try {
                final String matchedJar = getJarFileName(matchedResource);
                if (!matchedJar.equals(jarFileName)) {
                    jarFile = new JarFile(matchedJar);
                    jarFileName = matchedJar;
                }
                final ZipEntry jarFileEntry = jarFile.getEntry(getJarFileEntry(jarFileName, matchedResource));
                if (jarFileEntry == null) {
                    continue;
                }
                final InputStream inputStream = jarFile.getInputStream(jarFileEntry);
                try {
                    GroovyValueDefinition.Holder.instance.parseDefinition(this, inputStream);
                } finally {
                    try {
                        inputStream.close();
                    } catch (IOException ignore) { }
                }
            } catch (Exception ignore) { }
        }
        if (matchedResources.size() > 0) {
            build();
        }
        return this;
    }

    static List<String> matchValueDefinitions(String definitionMatcher, Set<String> definitionsCache) {
        String wildcardPattern = "*";
        if (definitionMatcher != null) {
            wildcardPattern += definitionMatcher;
        }
        if (!wildcardPattern.endsWith(".groovy")) {
            wildcardPattern += ".groovy";
        }
        final Pattern pattern = Pattern.compile(Util.replaceWildcards(wildcardPattern));
        final List<String> matchedResources = new ArrayList<String>();
        for (String definitionResource : definitionsCache) {
            if (pattern.matcher(definitionResource).matches()) {
                matchedResources.add(definitionResource);
            }
        }
        return matchedResources;
    }

    static String getJarFileEntry(String jarFile, String fullEntryPath)  {
        return fullEntryPath.substring(jarFile.length() + 1, fullEntryPath.length());
    }

    static String getJarFileName(String file) {
        final int index = file.indexOf(".jar");
        if (index > 0) {
            file = file.substring(0, index + 4);
        }
        return file;
    }

    static String getJarFileName(URL url) throws URISyntaxException {
        return getJarFileName(url.toURI().getPath());
    }

    boolean isIterableOrMap(Class type) {
        return type != null && (Iterable.class.isAssignableFrom(type) || Map.class.isAssignableFrom(type));
    }

    @Override
    public Object clone() throws CloneNotSupportedException {
        return super.clone();
    }


    static class Holder {
        private static final Set<String> libraryDefinitionsCache = lookupDefinitionResources();

        static Set<String> lookupDefinitionResources() {
            final LinkedHashSet<String> results = new LinkedHashSet<String>(1000);
            try {
                final Enumeration<URL> enumeration = Holder.class.getClassLoader().getResources(META_INF_ANCHOR);
                while (enumeration.hasMoreElements()) {
                    lookupDefinitionResources(getJarFileName(enumeration.nextElement()), results);
                }
            } catch (Exception ignore) { }
            return results;
        }

        static void lookupDefinitionResources(String file, Set<String> results) throws IOException {
            final JarFile jarFile = new JarFile(file);
            try {
                final Enumeration<JarEntry> entries = jarFile.entries();
                while (entries.hasMoreElements()) {
                    final String resourceName = entries.nextElement().getName();
                    if (resourceName.endsWith(DEFINITION_SCRIPT_SUFFIX)) {
                        results.add(file + "/" + resourceName);
                    }
                }
            } finally {
                jarFile.close();
            }
        }
    }
}
