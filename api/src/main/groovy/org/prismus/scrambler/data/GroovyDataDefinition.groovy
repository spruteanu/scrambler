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

package org.prismus.scrambler.data

import com.sun.org.apache.xpath.internal.operations.Bool
import groovy.transform.CompileStatic
import groovy.transform.PackageScope
import org.codehaus.groovy.control.CompilerConfiguration
import org.codehaus.groovy.control.customizers.ImportCustomizer
import org.codehaus.groovy.runtime.ResourceGroovyMethods
import org.prismus.scrambler.ArrayScrambler
import org.prismus.scrambler.CollectionScrambler
import org.prismus.scrambler.InstanceScrambler
import org.prismus.scrambler.DateScrambler
import org.prismus.scrambler.MapScrambler
import org.prismus.scrambler.NumericScrambler
import org.prismus.scrambler.ObjectScrambler
import org.prismus.scrambler.StringScrambler
import org.prismus.scrambler.Data
import org.prismus.scrambler.DataPredicate

import java.util.regex.Pattern

/**
 * Class responsible for registering Data Scrambler DSL capabilities.
 * Also the class is responsible for parsing DSL definitions scripts.
 * Groovy compiler properties can be customized by defining in class path
 * {@link GroovyDataDefinition#DEFINITIONS_PARSER_PROPERTIES} properties file
 *
 * @author Serge Pruteanu
 */
class GroovyDataDefinition {
    static final String DEFINITIONS_PARSER_PROPERTIES = '/definitions-parser.properties'

    private Properties parserProperties
    private GroovyShell shell

    GroovyDataDefinition() {
    }

    @CompileStatic
    protected GroovyShell checkCreateShell() {
        if (!shell) {
            shell = createGroovyShell()
        }
        return shell
    }

    @CompileStatic
    DataDefinition parseDefinitionText(String definitionText, Map<String, Object> context = null) {
        return doParseDefinitionText(new DataDefinition().usingContext(context), definitionText)
    }

    @CompileStatic
    protected DataDefinition doParseDefinitionText(DataDefinition definition, String definitionText) {
        checkCreateShell()
        final script = (DelegatingScript) shell.parse(definitionText)
        script.setDelegate(definition)
        script.run()
        return definition
    }

    @CompileStatic @PackageScope
    static String loadResourceText(DataDefinition self, String resource) {
        final String text
        if (resource.endsWith('groovy')) {
            final URL url = self.getClass().getResource(resource)
            if (url == null) {
                final file = new File(resource)
                if (!file.exists()) {
                    throw new IllegalArgumentException(String.format("Not found resource for: %s", resource))
                } else {
                    text = file.text
                }
            } else {
                text = url.text
            }
        } else {
            text = resource
        }
        return text
    }

    @CompileStatic
    protected DataDefinition parseDefinition(DataDefinition definition, String resource) {
        doParseDefinitionText(definition, loadResourceText(definition, resource))
        return definition
    }

    @CompileStatic
    protected DataDefinition parseDefinition(DataDefinition definition, InputStream resourceStream) {
        doParseDefinitionText(definition, resourceStream.text)
        return definition
    }

    @CompileStatic
    DataDefinition parseDefinition(String resource, Map<String, Object> context = null) throws IOException {
        if (resource.endsWith('groovy')) {
            final URL url = this.getClass().getResource(resource)
            if (url == null) {
                if (!new File(resource).exists()) {
                    throw new IllegalArgumentException(String.format("Not found resource for: %s", resource))
                } else {
                    return parseDefinition(new File(resource))
                }
            }
            return doParseDefinitionText(new DataDefinition().usingContext(context), url.text)
        } else {
            return doParseDefinitionText(new DataDefinition().usingContext(context), resource)
        }
    }

    DataDefinition parseDefinition(def resource, Map<String, Object> context = null) throws IOException {
        return doParseDefinitionText(new DataDefinition().usingContext(context), resource.text)
    }

    @CompileStatic
    Data parseDataText(String definitionText, Map<String, Object> context = null) {
        checkCreateShell()
        final script = (DelegatingScript) shell.parse(definitionText)
        final DataDefinition definition = new DataDefinition().usingContext(context)
        script.setDelegate(definition)
        return script.run() as Data
    }

    @CompileStatic
    Data parseData(String resource, Map<String, Object> context = null) throws IOException {
        final URL url = this.getClass().getResource(resource)
        if (url == null) {
            throw new IllegalArgumentException(String.format("Not found resource for: %s", resource))
        }
        return parseDataText(ResourceGroovyMethods.getText(url), context)
    }

    Data parseData(def resource, Map<String, Object> context = null) throws IOException {
        return parseDataText(resource.text, context)
    }

    @CompileStatic
    protected GroovyShell createGroovyShell() {
        final compilerConfiguration = (parserProperties != null && parserProperties.size() > 0) ? new CompilerConfiguration(parserProperties) : new CompilerConfiguration()
        compilerConfiguration.setScriptBaseClass(DelegatingScript.name)

        final importCustomizer = new ImportCustomizer()
        importCustomizer.addStarImports(Data.package.name)
        importCustomizer.addStarImports(ConstantData.package.name)
        importCustomizer.addStarImports(getClass().package.name)
        compilerConfiguration.addCompilationCustomizers(importCustomizer)

        return new GroovyShell(compilerConfiguration)
    }

    @CompileStatic
    GroovyDataDefinition withParserProperties(Properties configurationProperties) {
        this.parserProperties = configurationProperties
        shell = null
        return this
    }

    @CompileStatic
    GroovyDataDefinition withParserProperties(String configurationResource) throws IOException {
        return withParserProperties(getClass().getResourceAsStream(configurationResource))
    }

    @CompileStatic
    protected GroovyDataDefinition withParserProperties(InputStream inputStream) throws IOException {
        if (inputStream != null) {
            final Properties properties = new Properties()
            try {
                properties.load(inputStream)
                withParserProperties(properties)
            } finally {
                try {
                    inputStream.close()
                } catch (IOException ignore) { }
            }
        }
        return this
    }

    @CompileStatic @PackageScope
    GroovyDataDefinition lookupDefaultConfig() {
        final resource = getClass().getResource(DEFINITIONS_PARSER_PROPERTIES)
        if (resource != null) {
            withParserProperties(resource.openStream())
        }
        return this
    }

    @CompileStatic
    static class ClassCategory {

        static <T> Data<T> incrementArray(Class<T> self, T data, T step, Integer count) {
            return ArrayScrambler.incrementArray(self, data, step, count)
        }

        static <T extends Number> Data<T> increment(Class<T> self, T data, T step) {
            return NumericScrambler.increment(self, data, step)
        }

        static <T> Data<T> random(Class<T> self) {
            return ObjectScrambler.random(self)
        }

        static <T> Data<T> random(Class<T> self, T data) {
            return ObjectScrambler.random(self, data)
        }

        static <T> Data<T> random(Class<T> self, T minimum, T maximum) {
            return ObjectScrambler.random(self, minimum, maximum)
        }

        static <T> Data<T> randomArray(Class<T> self, T data, Integer count) {
            return ArrayScrambler.randomArray(self, data, count)
        }

        static <T> Data<T> definition(Class clazzType, Data val, Integer count) {
            return ArrayScrambler.of(clazzType, val, count)
        }

        static <T extends Number> Data<T> randomArray(Class self, T minimum, T maximum, Integer count) {
            return NumericScrambler.randomArray(self, minimum, maximum, count)
        }

        static <K> MapData<K> mapOf(Class<Map<K, Object>> mapType, Map<K, Data> keyDataMap) {
            return MapScrambler.of(mapType, keyDataMap)
        }

        static <K> MapData<K> mapOf(Class<Map<K, Object>> mapType, Collection<K> keys, Map<String, Object> context = null, String... definitions) {
            return MapScrambler.mapOf(mapType, keys, context, definitions)
        }

        static <V, T extends Collection<V>> CollectionData<V, T> collectionOf(Class<T> clazzType, Data<V> data) {
            return CollectionScrambler.collectionOf(clazzType, data)
        }

        static <T> InstanceData<T> instanceOf(Class<T> clazzType) {
            return InstanceScrambler.instanceOf(clazzType, (Map<Object, Object>)null)
        }

        static <T> InstanceData<T> instanceOf(Class<T> clazzType, Map<Object, Object> fieldMap) {
            return InstanceScrambler.instanceOf(clazzType, fieldMap)
        }

        static <T> InstanceData<T> instanceOf(String type) {
            return InstanceScrambler.instanceOf(type, (Map<Object, Object>)null)
        }

        static <T> InstanceData<T> instanceOf(String type, Map<Object, Object> fieldMap) {
            return InstanceScrambler.instanceOf(type, fieldMap)
        }

        static <T> InstanceData<T> definition(Class<T> self, Closure defCl) {
            return InstanceScrambler.instanceOf(self, new GroovyDefinitionCallable(defCl))
        }

        static <T> InstanceData<T> definition(Class<T> self, Map<Object, Object> propertyDataMap, Closure defCl = null) {
            GroovyDefinitionCallable definitionCallable = null
            if (defCl != null) {
                definitionCallable = new GroovyDefinitionCallable(defCl)
            }
            return InstanceScrambler.instanceOf(self, propertyDataMap, definitionCallable)
        }

        static <T> InstanceData<T> definition(Class<T> self, Collection constructorArgs, Closure defCl) {
            return InstanceScrambler.instanceOf(self, constructorArgs, new GroovyDefinitionCallable(defCl))
        }

        static ReferenceData reference(Class self, String propertyPredicate = null) {
            return InstanceScrambler.reference(self, propertyPredicate)
        }

        static <T> Data<T> arrayOf(Class<T> self, Data val, Integer count = null) {
            return ArrayScrambler.of(self, val, count)
        }

    }

    @CompileStatic
    static class CollectionCategory {

        static <V, T extends Collection<V>> CollectionData<V, T> of(T self, Data<V> data, Integer count = null) {
            return CollectionScrambler.of(self, data, count)
        }

        static <T> Data<T> randomOf(List<T> self) {
            return CollectionScrambler.randomOf(self)
        }

        static <T> Data<List<T>> combinationsOf(List<T> self) {
            return CollectionScrambler.combinationsOf(self)
        }

        static <T> Data<List<T>> dataCombinations(List<Data<T>> self) {
            return CollectionScrambler.dataCombinations(self)
        }

        static <T> Data<T> randomOf(Collection<T> self) {
            return CollectionScrambler.randomOf(self)
        }

        static <K> MapData<K> mapOf(Set<K> self, Map<DataPredicate, Data> definitionMap) {
            return MapScrambler.of(self, definitionMap)
        }

        static <K> MapData<K> mapOf(Set<K> self, Map<String, Object> contextMap = null, String... definitions) {
            return MapScrambler.mapOf(self, contextMap, definitions)
        }

    }

    @CompileStatic
    static class DateCategory {

        static IncrementalDate increment(Date self, Integer calendarField = null, Integer step = null) {
            return DateScrambler.increment(self, calendarField, step)
        }

        static IncrementalDate increment(Date self, Map<Integer, Integer> calendarFieldStepMap) {
            return DateScrambler.increment(self, calendarFieldStepMap)
        }

        static ArrayData<Date> incrementArray(Date self, Integer step = null, Integer count = null) {
            return DateScrambler.incrementArray(self, step, count)
        }

        static ArrayData<Date> incrementArray(Date self, Map<Integer, Integer> calendarFieldStepMap, Integer count = null) {
            return DateScrambler.incrementArray(self, calendarFieldStepMap, count)
        }

        static ArrayData<Date> incrementArray(Date self, Integer calendarField, Integer step, Integer count) {
            return DateScrambler.incrementArray(self, calendarField, step, count)
        }

        static RandomDate random(Date self, Date minimum = null, Date maximum = null) {
            return DateScrambler.random(self, minimum, maximum)
        }

        static ArrayData<Date> randomArray(Date self, Date minimum = null, Date maximum = null, Integer count = null) {
            return DateScrambler.randomArray(self, minimum, maximum, count)
        }

    }

    @CompileStatic
    static class MapCategory {

        static <K> MapData<K> of(Map<K, Data> keyDataMap) {
            return MapScrambler.of(keyDataMap);
        }

        static <K> MapData<K> of(Map<K, Object> self, Map<K, Data> keyDataMap) {
            return MapScrambler.of(self, keyDataMap)
        }
    }

    @CompileStatic
    static class NumberCategory {

        static <T extends Number> Data<T> increment(T self, T step = null) {
            return NumericScrambler.increment(self, step)
        }

        static <T extends Number> Data incrementArray(T self, T step = null, Integer count = null) {
            return ArrayScrambler.incrementArray(self, step, count)
        }

        static <T extends Number> Data<T> random(T data) {
            return NumericScrambler.random(data)
        }

        static <T extends Number> Data<T> random(T minimum, T maximum) {
            return NumericScrambler.random(minimum, maximum)
        }

        static <T extends Number> Data<T> random(T val, T minimum, T maximum) {
            return NumericScrambler.random(val, minimum, maximum)
        }
    }

    static class ObjectCategory {

        @CompileStatic
        static <T> Data<T> constant(T self) {
            return ObjectScrambler.constant(self)
        }

        @CompileStatic
        static <T> Data<T> randomOf(T[] self) {
            return ArrayScrambler.randomOf(self)
        }

        @CompileStatic
        static <T> Data<T[]> combinationsOf(T[] self) {
            return ArrayScrambler.combinationsOf(self)
        }

        @CompileStatic
        static <T> Data<T[]> combinationOf(Data<T>[] self, Class<T> dataType) {
            return ArrayScrambler.combinationsOf(dataType, self)
        }

        static <T> Data<T> combinationsOf(def self) {
            return ArrayScrambler.combinationsOf(self)
        }

        static <T> Data<T> randomOf(def self) {
            return ArrayScrambler.randomOf(self)
        }

        @CompileStatic
        static <T> Data<T> randomArray(Object data, Integer count = null) {
            return ArrayScrambler.randomArray(data, count);
        }

        @CompileStatic
        static <T> Data<T> arrayOf(Object self, Data data) {
            return ArrayScrambler.arrayOf(self, data);
        }

        @CompileStatic
        static <T> Data<T> arrayOf(Object self, Data data, Integer count) {
            return ArrayScrambler.arrayOf(self, data, count)
        }

        @CompileStatic
        static ArrayContainerData arrayOf(Object[] self, Data... data) {
            return new ArrayContainerData(Arrays.asList(data))
        }

    }

    @CompileStatic
    static class StringCategory {

        static IncrementalString increment(String self, String pattern = null, Integer index = null) {
            return StringScrambler.increment(self, pattern, index)
        }

        static ArrayData<String> incrementArray(String self, Integer count) {
            return StringScrambler.incrementArray(self, count)
        }

        static ArrayData<String> incrementArray(String self, String pattern, Integer count) {
            return StringScrambler.incrementArray(self, pattern, count)
        }

        static ArrayData<String> incrementArray(String data, Integer index, Integer count) {
            return StringScrambler.incrementArray(data, index, count)
        }

        static ArrayData<String> incrementArray(String data, String pattern, Integer index, Integer count) {
            return StringScrambler.incrementArray(data, pattern, index, count)
        }

        static RandomString random(String data, Integer count = null) {
            return StringScrambler.random(data, count)
        }

        static ArrayData<String> randomArray(String data, Integer count = null, Integer arrayCount = null) {
            return StringScrambler.randomArray(data, count, arrayCount)
        }

        static RandomUuid randomUuid() {
            return StringScrambler.randomUuid()
        }

    }

    @CompileStatic
    static class BooleanCategory {

        static Data<Boolean> random(Boolean data) {
            return ObjectScrambler.random(data);
        }

    }

    //------------------------------------------------------------------------------------------------------------------
    // Definitions registration
    //------------------------------------------------------------------------------------------------------------------
    static void register() {
        Object.metaClass.mixin ObjectCategory
        Number.metaClass.mixin NumberCategory
        Date.metaClass.mixin DateCategory
        String.metaClass.mixin StringCategory
        Collection.metaClass.mixin CollectionCategory
        Map.metaClass.mixin MapCategory
        Boolean.metaClass.mixin BooleanCategory

        Class.metaClass.mixin ClassCategory
    }

    static {
        register()
    }

    static interface Holder {
        static GroovyDataDefinition instance = new GroovyDataDefinition().lookupDefaultConfig()
    }

}
