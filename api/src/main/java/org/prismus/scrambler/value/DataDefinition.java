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

import org.prismus.scrambler.Data;
import org.prismus.scrambler.DataPredicate;
import org.prismus.scrambler.DataPredicates;

import java.io.*;
import java.net.URL;
import java.util.*;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.regex.Pattern;
import java.util.zip.ZipEntry;

/**
 * Data definitions dictionary/builder class. Responsible for building predicates/values key/value pairs
 *
 * @author Serge Pruteanu
 */
@SuppressWarnings("unchecked")
public class DataDefinition implements Cloneable {
    private static final String JAR_SUFFIX = ".jar";
    public static final String DEFINITION_SCRIPT_SUFFIX = "-definition.groovy";
    public static final String DEFAULT_DEFINITIONS_RESOURCE = "/org.prismus.scrambler.value.default-definition.groovy";
    public static final String META_INF_ANCHOR = "META-INF/dictionary.desc";
    public static final String WILDCARD_STRING = "*";

    private DataDefinition parent;

    private Map<DataPredicate, Data> definitionMap = new LinkedHashMap<DataPredicate, Data>();
    private Map<DataPredicate, InstanceData> instanceDataMap = new LinkedHashMap<DataPredicate, InstanceData>();
    private Map<String, Object> contextMap = new LinkedHashMap<String, Object>();

    public DataDefinition() {
    }

    public DataDefinition(Map<Object, Data> definitionMap) {
        definition((Map) definitionMap);
    }

    public void setParent(DataDefinition parent) {
        this.parent = parent;
    }

    public Map<DataPredicate, Data> getDefinitionMap() {
        return definitionMap;
    }

    DataDefinition clearInternals() {
        instanceDataMap.clear();
        final Set<DataPredicate> removedSet = new LinkedHashSet<DataPredicate>();
        for (DataPredicate predicate : definitionMap.keySet()) {
            if (predicate instanceof InstanceFieldPredicate) {
                removedSet.add(predicate);
            }
        }
        for (DataPredicate predicate : removedSet) {
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
    public DataDefinition definition(Data data) {
        Util.checkNullValue(data);
        final Object value1 = data.get();
        Util.checkNullValue(value1);
        registerDataPredicate(DataPredicates.isTypeOf(value1.getClass()), data);
        return this;
    }

    public DataDefinition definition(InstanceData value) {
        Util.checkNullValue(value);
        registerDataPredicate(new TypePredicate((Class) value.lookupType()), value);
        return this;
    }

    public DataDefinition definition(Map<Object, Object> props) {
        Util.checkNullValue(props);
        for (final Map.Entry entry : props.entrySet()) {
            final Object key = entry.getKey();
            Util.checkNullValue(key);

            final Object value = entry.getValue();
            if (String.class.isInstance(key)) {
                if (value instanceof Data) {
                    definition((String) key, (Data) value);
                } else {
                    definition((String) key, value);
                }
            } else if (Pattern.class.isInstance(key)) {
                if (value instanceof Data) {
                    definition(new PropertyPredicate((Pattern) key), (Data) value);
                } else {
                    definition(new PropertyPredicate((Pattern) key), value);
                }
            } else if (key instanceof Class) {
                if (value instanceof Data) {
                    definition((Class) key, (Data) value);
                } else {
                    definition((Class) key, value);
                }
            } else if (DataPredicate.class.isInstance(key)) {
                if (value instanceof Data) {
                    definition((DataPredicate) key, (Data) value);
                } else {
                    definition((DataPredicate) key, value);
                }
            } else {
                throw new IllegalArgumentException(String.format("Key should be of following types: [String, Class, DataPredicate]; passed map: %s", props));
            }
        }
        return this;
    }

    public DataDefinition constant(Object value) {
        Util.checkNullValue(value);
        registerDataPredicate(DataPredicates.isTypeOf(value.getClass()), new ConstantData(value));
        return this;
    }

    public DataDefinition constant(String propertyName, Object value) {
        Util.checkNullValue(value);
        registerDataPredicate(DataPredicates.matchProperty(propertyName), new ConstantData(value));
        return this;
    }

    public DataDefinition constant(Map<Object, Object> props) {
        Util.checkNullValue(props);
        for (final Map.Entry entry : props.entrySet()) {
            final Object key = entry.getKey();
            Util.checkNullValue(key);

            final Object value = entry.getValue();
            if (Data.class.isInstance(value)) {
                throw new IllegalArgumentException(String.format("ConstantData values can't be of Data type; passed map: %s", props));
            }
            if (String.class.isInstance(key)) {
                definition((String) key, new ConstantData(value));
            } else if (key instanceof Class) {
                definition((Class) key, new ConstantData(value));
            } else if (DataPredicate.class.isInstance(key)) {
                definition((DataPredicate) key, new ConstantData(value));
            } else {
                throw new IllegalArgumentException(String.format("Key should be of following types: [String, Class, DataPredicate]; passed map: %s", props));
            }
        }
        return this;
    }

    public DataDefinition definition(String propertyName, Object value) {
        registerDataPredicate(DataPredicates.matchProperty(propertyName), new ConstantData(value));
        return this;
    }

    public DataDefinition definition(Pattern pattern, Object value) {
        registerDataPredicate(PropertyPredicate.of(pattern), new ConstantData(value));
        return this;
    }

    public DataDefinition definition(Class type, Object value) {
        Util.checkNullValue(type);
        registerDataPredicate(DataPredicates.isTypeOf(type), new ConstantData(value));
        return this;
    }

    public DataDefinition definition(String propertyName, Data data) {
        Util.checkNullValue(data);
        if (data instanceof ReferenceData) {
            ((ReferenceData) data).setDefinition(this);
        }
        registerDataPredicate(DataPredicates.matchProperty(propertyName), data);
        return this;
    }

    public DataDefinition definition(Pattern pattern, Data data) {
        Util.checkNullValue(data);
        if (data instanceof ReferenceData) {
            ((ReferenceData) data).setDefinition(this);
        }
        registerDataPredicate(PropertyPredicate.of(pattern), data);
        return this;
    }

    public DataDefinition definition(Class type, Data data) {
        Util.checkNullValue(type);
        Util.checkNullValue(data);
        registerDataPredicate(DataPredicates.isTypeOf(type), data);
        return this;
    }

    void lookupRegisterInstanceValue(DataPredicate dataPredicate, Data data) {
        if (InstanceData.class.isInstance(data)) {
            final InstanceData instanceData = (InstanceData) data;
            if (instanceData.getDefinition() != this) {
                instanceDataMap.put(dataPredicate, instanceData);
            }
        } else if (CollectionData.class.isInstance(data)) {
            lookupRegisterInstanceValue(dataPredicate, ((CollectionData) data).getInstance());
        } else if (data instanceof ArrayData) {
            lookupRegisterInstanceValue(dataPredicate, ((ArrayData) data).getInstance());
        }
    }

    void registerDataPredicate(DataPredicate dataPredicate, Data data) {
        lookupRegisterInstanceValue(dataPredicate, data);
        definitionMap.put(dataPredicate, data);
    }

    public DataDefinition definition(DataPredicate dataPredicate, Data data) {
        Util.checkNullValue(dataPredicate);
        Util.checkNullValue(data);
        registerDataPredicate(dataPredicate, data);
        return this;
    }

    public DataDefinition definition(DataPredicate dataPredicate, Object value) {
        Util.checkNullValue(dataPredicate);
        registerDataPredicate(dataPredicate, new ConstantData(value));
        return this;
    }

    public DataDefinition reference(Class type) {
        Util.checkNullValue(type);
        final DataPredicate predicate = DataPredicates.isTypeOf(type);
        registerDataPredicate(predicate, new ReferenceData(this, predicate));
        return this;
    }

    public DataDefinition reference(Class type, String parentPredicate) {
        reference(type, DataPredicates.matchProperty(parentPredicate));
        return this;
    }

    public DataDefinition reference(Class type, Class parentPredicate) {
        Util.checkNullValue(parentPredicate);
        reference(type, DataPredicates.isTypeOf(parentPredicate));
        return this;
    }

    public DataDefinition reference(Class type, DataPredicate parentPredicate) {
        Util.checkNullValue(type);
        final DataPredicate predicate = DataPredicates.isTypeOf(type);
        registerDataPredicate(predicate, new ReferenceData(this, parentPredicate));
        return this;
    }

    public DataDefinition reference(String propertyName, Class parentPredicate) {
        Util.checkNullValue(parentPredicate);
        reference(DataPredicates.matchProperty(propertyName), DataPredicates.isTypeOf(parentPredicate));
        return this;
    }

    public DataDefinition reference(Pattern pattern, Class parentPredicate) {
        Util.checkNullValue(parentPredicate);
        reference(PropertyPredicate.of(pattern), DataPredicates.isTypeOf(parentPredicate));
        return this;
    }

    public DataDefinition reference(String propertyName) {
        final DataPredicate predicate = DataPredicates.matchProperty(propertyName);
        registerDataPredicate(predicate, new ReferenceData(this, predicate));
        return this;
    }

    public DataDefinition reference(Pattern pattern) {
        final PropertyPredicate predicate = PropertyPredicate.of(pattern);
        registerDataPredicate(predicate, new ReferenceData(this, predicate));
        return this;
    }

    public DataDefinition reference(DataPredicate dataPredicate, DataPredicate parentPredicate) {
        Util.checkNullValue(dataPredicate);
        registerDataPredicate(dataPredicate, new ReferenceData(this, parentPredicate));
        return this;
    }

    public DataDefinition usingDefinition(DataDefinition definition) {
        contextMap.putAll(definition.contextMap);
        return usingDefinitions(definition.getDefinitionMap());
    }

    public DataDefinition usingDefinitions(Map<DataPredicate, Data> definitions) {
        definitionMap.putAll(definitions);
        return this;
    }

    public DataDefinition usingDefinitions(String... definitions) {
        if (definitions != null) {
            for (String definition : definitions) {
                GroovyDataDefinition.Holder.instance.parseDefinition(this, definition);
            }
            build();
        }
        return this;
    }

    public DataDefinition scanDefinitions(List<String> definitions) {
        int foundCount = 0;
        for (String definition : definitions) {
            final URL url = getClass().getResource(definition);
            if (url == null) {
                if (!new File(definition).exists()) {
                    continue;
                }
            } // not found resources filtered, parse definition
            GroovyDataDefinition.Holder.instance.parseDefinition(this, definition);
            foundCount++;
        }
        if (foundCount > 0) {
            build();
        }
        return this;
    }

    public DataDefinition scanDefinitions(String resource, String... resources) {
        final ArrayList<String> resourceList = new ArrayList<String>();
        resourceList.add(resource);
        if (resources != null) {
            resourceList.addAll(Arrays.asList(resources));
        }
        scanDefinitions(resourceList);
        return this;
    }

    /**
     * Scans and parses ALL library definitions
     */
    public DataDefinition usingLibraryDefinitions() {
        return usingLibraryDefinitions(null, Holder.libraryDefinitionsCache);
    }

    /**
     * Scans library definitions using provided {@code definitionMatcher} pattern and parses matched definition resources
     *
     * @param definitionMatcher    wildcard or reg-ex to match library definitions for parsing
     */
    public DataDefinition usingLibraryDefinitions(String definitionMatcher) {
        return usingLibraryDefinitions(definitionMatcher, Holder.libraryDefinitionsCache);
    }

    public DataDefinition usingContext(Map<String, Object> contextMap) {
        if (contextMap != null) {
            this.contextMap = contextMap;
        }
        return this;
    }

    public Map<String, Object> getContextMap() {
        return contextMap;
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

    public Data lookupValue(DataPredicate predicate) {
        Data result = definitionMap.get(predicate);
        if (result == null && parent != null) {
            result = parent.lookupValue(predicate);
        }
        return result;
    }

    public List<Data> lookupValues(Class type, Class... types) {
        final ArrayList<Class> list = new ArrayList<Class>();
        list.add(type);
        if (types != null) {
            list.addAll(Arrays.asList(types));
        }
        return lookupValues(list);
    }

    public List<Data> lookupValues(List<Class> types) {
        final ArrayList<Data> results = new ArrayList<Data>(types.size());
        for (Class type : types) {
            results.add(lookupValue(null, type));
        }
        return results;
    }

    public boolean hasDefinitions() {
        return definitionMap.isEmpty();
    }

    public Data lookupValue(String property, Class type) {
        if (hasDefinitions()) {
            scanDefinitions(DEFAULT_DEFINITIONS_RESOURCE);
        }
        Data data = null;
        for (Map.Entry<DataPredicate, Data> entry : definitionMap.entrySet()) {
            if (!isIterableOrMap(type) && entry.getKey().apply(property, type)) {
                data = entry.getValue();
                break;
            }
        }
        if (data instanceof InstanceTypeData) {
            data = ((InstanceTypeData) data).next(type);
        } else if (data instanceof RandomTypeData) {
            data = ((RandomTypeData) data).next(type);
        } else if (data instanceof IncrementalTypeData) {
            data = ((IncrementalTypeData) data).next(type);
        }
        if (data == null && parent != null) {
            parent.lookupValue(property, type);
        }
        return data;
    }

    public DataDefinition getValueDefinition() {
        return this;
    }

    //------------------------------------------------------------------------------------------------------------------
    // Internal Methods
    //------------------------------------------------------------------------------------------------------------------
    protected DataDefinition build() {
        for (final InstanceData value : instanceDataMap.values()) {
            value.build(this);
        }
        return this;
    }

    DataDefinition usingLibraryDefinitions(String definitionMatcher, Set<String> foundResources) {
        final List<String> matchedResources = matchDefinitions(definitionMatcher, foundResources);
        String jarFileName = null;
        JarFile jarFile = null;
        for (String matchedResource : matchedResources) {
            try {
                final String definitionSource = getDefinitionsSource(matchedResource);
                final InputStream inputStream;
                if (definitionSource.endsWith(JAR_SUFFIX)) {
                    if (!definitionSource.equals(jarFileName)) {
                        jarFile = new JarFile(definitionSource);
                        jarFileName = definitionSource;
                    }
                    final ZipEntry jarFileEntry = jarFile.getEntry(getJarFileEntry(jarFileName, matchedResource));
                    if (jarFileEntry == null) {
                        continue;
                    }
                    inputStream = jarFile.getInputStream(jarFileEntry);
                } else {
                    inputStream = new FileInputStream(definitionSource);
                }
                try {
                    GroovyDataDefinition.Holder.instance.parseDefinition(this, inputStream);
                } finally {
                    try { inputStream.close(); } catch (IOException ignore) { }
                }
            } catch (Exception ignore) { }
        }
        if (matchedResources.size() > 0) {
            build();
        }
        return this;
    }

    static List<String> matchDefinitions(String definitionMatcher, Set<String> definitionsCache) {
        String wildcardPattern;
        if (definitionMatcher == null || (!definitionMatcher.contains(WILDCARD_STRING))) {
            wildcardPattern = WILDCARD_STRING;
            if (definitionMatcher != null) {
                wildcardPattern += definitionMatcher;
            }
        } else {
            wildcardPattern = definitionMatcher;
        }
        if (!wildcardPattern.startsWith(WILDCARD_STRING)) {
            wildcardPattern = WILDCARD_STRING + wildcardPattern;
        }
        if (!wildcardPattern.endsWith(".groovy")) {
            if (!wildcardPattern.endsWith(WILDCARD_STRING)) {
                wildcardPattern += WILDCARD_STRING;
            }
            wildcardPattern += "-definition.groovy";
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

    static String getDefinitionsSource(String file) {
        int index = file.indexOf(JAR_SUFFIX);
        if (index > 0) {
            file = file.substring(0, index + 4);
        }
        if (file.startsWith("file:")) {
            file = file.substring(5, file.length());
        }
        return file;
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
            final Set<String> results = new LinkedHashSet<String>(1000);
            try {
                final Enumeration<URL> enumeration = Holder.class.getClassLoader().getResources(META_INF_ANCHOR);
                while (enumeration.hasMoreElements()) {
                    final String definitionsSource = enumeration.nextElement().toURI().getSchemeSpecificPart();
                    if (definitionsSource.contains(JAR_SUFFIX)) {
                        lookupJarDefinitions(definitionsSource, results);
                    } else {
                        lookupFolderDefinitions(definitionsSource.substring(0, definitionsSource.length() - META_INF_ANCHOR.length()), results);
                    }
                }
            } catch (Exception ignore) { }
            return results;
        }

        static void lookupJarDefinitions(String file, Set<String> results) throws IOException {
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

        @SuppressWarnings("ResultOfMethodCallIgnored")
        static void lookupFolderDefinitions(String folder, final Set<String> results) {
            final File defFolder = new File(folder);
            defFolder.listFiles(new FileFilter() {
                @Override
                public boolean accept(File file) {
                    final String resourceName = file.getAbsolutePath();
                    if (file.isDirectory()) {
                        lookupFolderDefinitions(resourceName, results);
                    } else if (resourceName.endsWith(DEFINITION_SCRIPT_SUFFIX)) {
                        results.add(resourceName);
                    }
                    return false;
                }
            });
        }
    }
}
