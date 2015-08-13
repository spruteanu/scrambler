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

package org.prismus.scrambler.value

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
import org.prismus.scrambler.NumberScrambler
import org.prismus.scrambler.ObjectScrambler
import org.prismus.scrambler.StringScrambler
import org.prismus.scrambler.Value
import org.prismus.scrambler.ValuePredicate

import java.util.regex.Pattern

/**
 * Class responsible for registering Data Scrambler DSL capabilities.
 * Also the class is responsible for parsing DSL definitions scripts.
 * Groovy compiler properties can be customized by defining in class path
 * {@link GroovyValueDefinition#GROOVY_COMPILER_PROPERTIES} properties file
 *
 * @author Serge Pruteanu
 */
class GroovyValueDefinition {
    static final String GROOVY_COMPILER_PROPERTIES = '/groovy-compiler.properties'

    private Properties configurationProperties
    private GroovyShell shell

    GroovyValueDefinition() {
    }

    @CompileStatic
    protected GroovyShell checkCreateShell() {
        if (!shell) {
            shell = createGroovyShell()
        }
        return shell
    }

    @CompileStatic
    ValueDefinition parseDefinitionText(String definitionText, Map<String, Object> context = null) {
        return doParseDefinitionText(new ValueDefinition().usingContext(context), definitionText)
    }

    @CompileStatic
    protected ValueDefinition doParseDefinitionText(ValueDefinition definition, String definitionText) {
        checkCreateShell()
        final script = (DelegatingScript) shell.parse(definitionText)
        script.setDelegate(definition)
        script.run()
        return definition
    }

    @CompileStatic @PackageScope
    static String loadResourceText(ValueDefinition self, String resource) {
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
    protected ValueDefinition parseDefinition(ValueDefinition definition, String resource) {
        doParseDefinitionText(definition, loadResourceText(definition, resource))
        return definition
    }

    @CompileStatic
    ValueDefinition parseDefinition(String resource, Map<String, Object> context = null) throws IOException {
        if (resource.endsWith('groovy')) {
            final URL url = this.getClass().getResource(resource)
            if (url == null) {
                if (!new File(resource).exists()) {
                    throw new IllegalArgumentException(String.format("Not found resource for: %s", resource))
                } else {
                    return parseDefinition(new File(resource))
                }
            }
            return doParseDefinitionText(new ValueDefinition().usingContext(context), url.text)
        } else {
            return doParseDefinitionText(new ValueDefinition().usingContext(context), resource)
        }
    }

    ValueDefinition parseDefinition(def resource, Map<String, Object> context = null) throws IOException {
        return doParseDefinitionText(new ValueDefinition().usingContext(context), resource.text)
    }

    @CompileStatic
    Value parseValueText(String definitionText, Map<String, Object> context = null) {
        checkCreateShell()
        final script = (DelegatingScript) shell.parse(definitionText)
        final ValueDefinition definition = new ValueDefinition().usingContext(context)
        script.setDelegate(definition)
        return script.run() as Value
    }

    @CompileStatic
    Value parseValue(String resource, Map<String, Object> context = null) throws IOException {
        final URL url = this.getClass().getResource(resource)
        if (url == null) {
            throw new IllegalArgumentException(String.format("Not found resource for: %s", resource))
        }
        return parseValueText(ResourceGroovyMethods.getText(url), context)
    }

    Value parseValue(def resource, Map<String, Object> context = null) throws IOException {
        return parseValueText(resource.text, context)
    }

    @CompileStatic
    protected GroovyShell createGroovyShell() {
        final compilerConfiguration = (configurationProperties != null && configurationProperties.size() > 0) ? new CompilerConfiguration(configurationProperties) : new CompilerConfiguration()
        compilerConfiguration.setScriptBaseClass(DelegatingScript.name)

        final importCustomizer = new ImportCustomizer()
        importCustomizer.addStarImports(Value.package.name)
        importCustomizer.addStarImports(Constant.package.name)
        importCustomizer.addStarImports(getClass().package.name)
        compilerConfiguration.addCompilationCustomizers(importCustomizer)

        return new GroovyShell(compilerConfiguration)
    }

    @CompileStatic
    Properties getConfigurationProperties() {
        return configurationProperties
    }

    @CompileStatic
    void setConfigurationProperties(Properties configurationProperties) {
        this.configurationProperties = configurationProperties
    }

    @CompileStatic
    void setConfigurationProperties(String configurationResource) throws IOException {
        setConfigurationProperties(getClass().getResourceAsStream(configurationResource))
    }

    @CompileStatic
    protected void setConfigurationProperties(InputStream inputStream) throws IOException {
        if (inputStream != null) {
            final Properties properties = new Properties()
            try {
                properties.load(inputStream)
                this.configurationProperties = properties
            } finally {
                try {
                    inputStream.close()
                } catch (IOException ignore) { }
            }
        }
    }

    @CompileStatic
    @PackageScope
    GroovyValueDefinition lookupDefaultConfig() {
        final resource = getClass().getResource(GROOVY_COMPILER_PROPERTIES)
        if (resource != null) {
            setConfigurationProperties(resource.openStream())
        }
        return this
    }

    @CompileStatic
    static class ClassCategory {

        static <T> Value<T> incrementArray(Class<T> self, T defaultValue, T step, Integer count) {
            return ArrayScrambler.incrementArray(self, defaultValue, step, count)
        }

        static <T extends Number> Value<T> increment(Class<T> self, T defaultValue, T step) {
            return NumberScrambler.increment(self, defaultValue, step)
        }

        static <T> Value<T> random(Class<T> self) {
            return ObjectScrambler.random(self)
        }

        static <T> Value<T> random(Class<T> self, T defaultValue) {
            return ObjectScrambler.random(self, defaultValue)
        }

        static <T> Value<T> random(Class<T> self, T minimum, T maximum) {
            return ObjectScrambler.random(self, minimum, maximum)
        }

        static <T> Value<T> randomArray(Class<T> self, T defaultValue, Integer count) {
            return ArrayScrambler.randomArray(self, defaultValue, count)
        }

        static <T> Value<T> definition(Class clazzType, Value val, Integer count) {
            return ArrayScrambler.of(clazzType, val, count)
        }

        static <T extends Number> Value<T> randomArray(Class self, T minimum, T maximum, Integer count) {
            return NumberScrambler.randomArray(self, minimum, maximum, count)
        }

        static <K> MapValue<K> mapOf(Class<Map<K, Object>> mapType, Map<K, Value> keyValueMap) {
            return MapScrambler.mapOf(mapType, keyValueMap)
        }

        static <V, T extends Collection<V>> CollectionValue<V, T> collectionOf(Class<T> clazzType, Value<V> value) {
            return CollectionScrambler.collectionOf(clazzType, value)
        }

        static <T> InstanceValue<T> instanceOf(Class<T> clazzType) {
            return InstanceScrambler.instanceOf(clazzType, (Map<Object, Object>)null)
        }

        static <T> InstanceValue<T> instanceOf(Class<T> clazzType, Map<Object, Object> fieldMap) {
            return InstanceScrambler.instanceOf(clazzType, fieldMap)
        }

        static <T> InstanceValue<T> instanceOf(String type) {
            return InstanceScrambler.instanceOf(type, (Map<Object, Object>)null)
        }

        static <T> InstanceValue<T> instanceOf(String type, Map<Object, Object> fieldMap) {
            return InstanceScrambler.instanceOf(type, fieldMap)
        }

        static <T> InstanceValue<T> definition(Class<T> self, Closure defCl) {
            return InstanceScrambler.instanceOf(self, new GroovyDefinitionCallable(defCl))
        }

        static <T> InstanceValue<T> definition(Class<T> self, Map<Object, Object> propertyValueMap, Closure defCl = null) {
            GroovyDefinitionCallable definitionCallable = null
            if (defCl != null) {
                definitionCallable = new GroovyDefinitionCallable(defCl)
            }
            return InstanceScrambler.instanceOf(self, propertyValueMap, definitionCallable)
        }

        static <T> InstanceValue<T> definition(Class<T> self, String propertyName, Closure defCl) {
            return InstanceScrambler.instanceOf(self, new GroovyDefinitionCallable(defCl))
        }

        static <T> InstanceValue<T> definition(Class<T> self, Collection constructorArgs, Closure defCl) {
            return InstanceScrambler.instanceOf(self, constructorArgs, new GroovyDefinitionCallable(defCl))
        }

        static <T> InstanceValue<T> definition(Class<T> self, String propertyName, Collection constructorArgs, Closure defCl) {
            return InstanceScrambler.instanceOf(self, constructorArgs, new GroovyDefinitionCallable(defCl))
        }

        static ReferenceValue reference(Class self, String propertyPredicate = null) {
            return InstanceScrambler.reference(self, propertyPredicate)
        }

        static <T> Value<T> arrayOf(Class<T> self, Value val, Integer count = null) {
            return ArrayScrambler.of(self, val, count)
        }

    }

    @CompileStatic
    static class CollectionCategory {

        static <V, T extends Collection<V>> CollectionValue<V, T> of(T collection, Value<V> value, Integer count = null) {
            return CollectionScrambler.of(collection, value, count)
        }

        static <T> Value<T> randomOf(List<T> values) {
            return CollectionScrambler.randomOf(values)
        }

        static <T> Value<T> randomOf(Collection<T> collection) {
            return CollectionScrambler.randomOf(collection)
        }

        static <K> MapValue<K> mapOf(Set<K> self, Map<ValuePredicate, Value> definitionMap) {
            return MapScrambler.mapOf(self, definitionMap)
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

        static ArrayValue<Date> incrementArray(Date self, Integer step = null, Integer count = null) {
            return DateScrambler.incrementArray(self, step, count)
        }

        static ArrayValue<Date> incrementArray(Date self, Map<Integer, Integer> calendarFieldStepMap, Integer count = null) {
            return DateScrambler.incrementArray(self, calendarFieldStepMap, count)
        }

        static ArrayValue<Date> incrementArray(Date self, Integer calendarField, Integer step, Integer count) {
            return DateScrambler.incrementArray(self, calendarField, step, count)
        }

        static RandomDate random(Date self, Date minimum = null, Date maximum = null) {
            return DateScrambler.random(self, minimum, maximum)
        }

        static ArrayValue<Date> randomArray(Date self, Date minimum = null, Date maximum = null, Integer count = null) {
            return DateScrambler.randomArray(self, minimum, maximum, count)
        }

    }

    @CompileStatic
    static class MapCategory {

        static <K> MapValue<K> of(Map<K, Value> keyValueMap) {
            return MapScrambler.of(keyValueMap);
        }

        static <K> MapValue<K> of(Map<K, Object> self, Map<K, Value> keyValueMap) {
            return MapScrambler.of(self, keyValueMap)
        }
    }

    @CompileStatic
    static class NumberCategory {

        static <T extends Number> Value<T> increment(T self, T step = null) {
            return NumberScrambler.increment(self, step)
        }

        static <T extends Number> Value incrementArray(T self, T step = null, Integer count = null) {
            return ArrayScrambler.incrementArray(self, step, count)
        }

        static <T extends Number> Value<T> random(T value) {
            return NumberScrambler.random(value)
        }

        static <T extends Number> Value<T> random(T minimum, T maximum) {
            return NumberScrambler.random(minimum, maximum)
        }

        static <T extends Number> Value<T> random(T val, T minimum, T maximum) {
            return NumberScrambler.random(val, minimum, maximum)
        }

    }

    @CompileStatic
    static class ObjectCategory {

        static <T> Value<T> constant(T self) {
            return ObjectScrambler.constant(self)
        }

        static <T> Value<T> randomOf(T[] self) {
            return ArrayScrambler.randomOf(self)
        }

        static <T> Value<T>  randomArray(Object value, Integer count = null) {
            return ArrayScrambler.randomArray(value, count);
        }

        static <T> Value<T> arrayOf(Object self, Value value) {
            return ArrayScrambler.arrayOf(self, value);
        }

        static <T> Value<T> arrayOf(Object self, Value value, Integer count) {
            return ArrayScrambler.arrayOf(self, value, count)
        }

        static ArrayContainerValue arrayOf(Object[] self, Value... values) {
            return new ArrayContainerValue(Arrays.asList(values))
        }

    }

    @CompileStatic
    static class StringCategory {

        static IncrementalString increment(String self, String pattern = null, Integer index = null) {
            return StringScrambler.increment(self, pattern, index)
        }

        static ArrayValue<String> incrementArray(String self, Integer count) {
            return StringScrambler.incrementArray(self, count)
        }

        static ArrayValue<String> incrementArray(String self, String pattern, Integer count) {
            return StringScrambler.incrementArray(self, pattern, count)
        }

        static ArrayValue<String> incrementArray(String value, Integer index, Integer count) {
            return StringScrambler.incrementArray(value, index, count)
        }

        static ArrayValue<String> incrementArray(String value, String pattern, Integer index, Integer count) {
            return StringScrambler.incrementArray(value, pattern, index, count)
        }

        static RandomString random(String value, Integer count = null) {
            return StringScrambler.random(value, count)
        }

        static ArrayValue<String> randomArray(String value, Integer count = null, Integer arrayCount = null) {
            return StringScrambler.randomArray(value, count, arrayCount)
        }

        static RandomUuid randomUuid() {
            return StringScrambler.randomUuid()
        }

    }

    @CompileStatic
    static class BooleanCategory {

        static Value<Boolean> random(Boolean value) {
            return ObjectScrambler.random(value);
        }

    }

    @CompileStatic
    static class ValueDefinitionCategory {

        static ValueDefinition definition(ValueDefinition self, Value value) {
            return self.definition(value)
        }

        static ValueDefinition definition(ValueDefinition self, InstanceValue value) {
            return self.definition(value)
        }

        static ValueDefinition definition(ValueDefinition self, Map<Object, Object> props) {
            return self.definition(props)
        }

        static ValueDefinition constant(ValueDefinition self, Object value) {
            return self.constant(value)
        }

        static ValueDefinition constant(ValueDefinition self, Map<Object, Object> props) {
            return self.constant(props)
        }

        static ValueDefinition definition(ValueDefinition self, String propertyName, Object value) {
            return self.definition(propertyName, value)
        }

        static ValueDefinition definition(ValueDefinition self, Pattern pattern, Object value) {
            return self.definition(pattern, value)
        }

        static ValueDefinition definition(ValueDefinition self, Class type, Object value) {
            return self.definition(type, value)
        }

        static ValueDefinition definition(ValueDefinition self, String propertyName, Value value) {
            return self.definition(propertyName, value)
        }

        static ValueDefinition definition(ValueDefinition self, Pattern pattern, Value value) {
            return self.definition(pattern, value)
        }

        static ValueDefinition definition(ValueDefinition self, Class type, Value value) {
            return self.definition(type, value)
        }

        static ValueDefinition definition(ValueDefinition self, ValuePredicate valuePredicate, Value value) {
            return self.definition(valuePredicate, value)
        }

        static ValueDefinition definition(ValueDefinition self, ValuePredicate valuePredicate, Object value) {
            return self.definition(valuePredicate, value)
        }

        static ValueDefinition reference(ValueDefinition self, Class type) {
            return self.reference(type)
        }

        static ValueDefinition reference(ValueDefinition self, Class type, String parentPredicate) {
            return self.reference(type, parentPredicate)
        }

        static ValueDefinition reference(ValueDefinition self, Class type, Class parentPredicate) {
            return self.reference(type, parentPredicate)
        }

        static ValueDefinition reference(ValueDefinition self, Class type, ValuePredicate parentPredicate) {
            return self.reference(type, parentPredicate)
        }

        static ValueDefinition reference(ValueDefinition self, String propertyName, Class parentPredicate) {
            return self.reference(propertyName, parentPredicate)
        }

        static ValueDefinition reference(ValueDefinition self, Pattern pattern, Class parentPredicate) {
            return self.reference(pattern, parentPredicate)
        }

        static ValueDefinition reference(ValueDefinition self, String propertyName) {
            return self.reference(propertyName)
        }

        static ValueDefinition reference(ValueDefinition self, Pattern pattern) {
            return self.reference(pattern)
        }

        static ValueDefinition reference(ValueDefinition self, ValuePredicate valuePredicate, ValuePredicate parentPredicate) {
            return self.reference(valuePredicate, parentPredicate)
        }

        static Object getContextProperty(ValueDefinition self, String propertyName, Object defaultValue = null) {
            return self.getContextProperty(propertyName, defaultValue)
        }

        static ValueDefinition usingDefinition(ValueDefinition self, ValueDefinition valueDefinition) {
            return self.usingDefinition(valueDefinition)
        }

        static ValueDefinition usingDefinition(ValueDefinition self, String resource) {
            return Holder.instance.doParseDefinitionText(self, loadResourceText(self, resource))
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
        static GroovyValueDefinition instance = new GroovyValueDefinition().lookupDefaultConfig()
    }

}
