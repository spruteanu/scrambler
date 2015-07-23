package org.prismus.scrambler.value

import groovy.transform.CompileStatic
import org.codehaus.groovy.control.CompilerConfiguration
import org.codehaus.groovy.control.customizers.ImportCustomizer
import org.codehaus.groovy.runtime.ResourceGroovyMethods
import org.prismus.scrambler.DataScrambler
import org.prismus.scrambler.Value

/**
 * todo: add description
 *
 * @author Serge Pruteanu
 */
@SuppressWarnings(["GroovyAssignabilityCheck", "UnnecessaryQualifiedReference"])
class GroovyValueDefinition {
    private Properties configurationProperties
    private GroovyShell shell

    private ValueDefinition definition = new ValueDefinition()

    GroovyValueDefinition() {
    }

    @CompileStatic
    ValueDefinition parseText(String definitionText) {
        if (!shell) {
            shell = createGroovyShell()
        }
        final script = (DelegatingScript)shell.parse(definitionText)
        script.setDelegate(definition)
        script.run()
        return definition
    }

    @CompileStatic
    ValueDefinition parse(String resource) throws IOException {
        final URL url = this.getClass().getResource(resource)
        if (url == null) {
            throw new IllegalArgumentException(String.format("Not found resource for: %s", resource))
        }
        return parseText(ResourceGroovyMethods.getText(url))
    }

    ValueDefinition parse(def resource) throws IOException {
        return parseText(resource.text)
    }

    @CompileStatic
    protected GroovyShell createGroovyShell() {
        final compilerConfiguration = new CompilerConfiguration()
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

    //------------------------------------------------------------------------------------------------------------------
    static void register() {
        Object.metaClass.mixin ObjectCategory
        Number.metaClass.mixin NumberCategory
        Date.metaClass.mixin DateCategory
        String.metaClass.mixin StringCategory
        Collection.metaClass.mixin CollectionCategory
        Map.metaClass.mixin MapCategory

        Class.metaClass.mixin ClassCategory
    }

    static {
        register()
    }

    @CompileStatic
    static class ClassCategory {

        static <T> Value<T> incrementArray(Class<T> self, T defaultValue, T step, Integer count) {
            return DataScrambler.incrementArray(self, defaultValue, step, count)
        }

        static <T extends Number> Value<T> increment(Class<T> self, T defaultValue, T step) {
            return DataScrambler.increment(self, defaultValue, step)
        }

        static <T> Value<T> random(Class<T> self) {
            return DataScrambler.random(self)
        }

        static <T> Value<T> random(Class<T> self, T defaultValue) {
            return DataScrambler.random(self, defaultValue)
        }

        static <T> Value<T> random(Class<T> self, T minimum, T maximum) {
            return DataScrambler.random(self, minimum, maximum)
        }

        static <T> Value<T> random(Class<T> self, T defaultValue, Integer count) {
            return DataScrambler.random(self, defaultValue, count)
        }

        static <T> Value<T> of(Class clazzType, Value val, Integer count) {
            return DataScrambler.of(clazzType, val, count)
        }

        static <T extends Number> Value<T> randomArray(Class self, T minimum, T maximum, Integer count) {
            return DataScrambler.randomArray(self, minimum, maximum, count)
        }

        static <K> MapValue<K> mapOf(Class<Map<K, Object>> mapType, Map<K, Value> keyValueMap) {
            return DataScrambler.mapOf(mapType, keyValueMap)
        }

        static <V, T extends Collection<V>> CollectionValue<V, T> collectionOf(Class<V> clazzType, Value<V> value) {
            return DataScrambler.collectionOf(clazzType, value);
        }

        static <T> InstanceValue<T> instanceOf(Class<T> clazzType) {
            return DataScrambler.instanceOf(clazzType, null)
        }

        static <T> InstanceValue<T> instanceOf(Class<T> clazzType, Map<Object, Object> fieldMap) {
            return DataScrambler.instanceOf(clazzType, fieldMap);
        }

        static <T> InstanceValue<T> instanceOf(String type) {
            return DataScrambler.instanceOf(type, null)
        }

        static <T> InstanceValue<T> instanceOf(String type, Map<Object, Object> fieldMap) {
            return DataScrambler.instanceOf(type, fieldMap);
        }

        static <T> InstanceValue<T> of(Class<T> self, Closure defCl) {
            return DataScrambler.of(self, defCl);
        }

        static <T> InstanceValue<T> of(Class<T> self, Map<Object, Object> propertyValueMap, Closure defCl = null) {
            return DataScrambler.of(self, propertyValueMap, defCl);
        }

        static <T> InstanceValue<T> of(Class<T> self, String propertyName, Closure defCl) {
            return DataScrambler.of(self, propertyName, defCl);
        }

        static <T> InstanceValue<T> of(Class<T> self, Collection constructorArgs, Closure defCl) {
            return DataScrambler.of(self, constructorArgs, defCl);
        }

        static <T> InstanceValue<T> of(Class<T> self, String propertyName, Collection constructorArgs, Closure defCl) {
            return DataScrambler.of(self, propertyName, constructorArgs, defCl);
        }

        static ReferenceValue reference(Class self, String propertyPredicate = null) {
            return DataScrambler.reference(self, propertyPredicate);
        }

        static <T> Value<T> arrayOf(Class<T> self, Value val, Integer count = null) {
            return DataScrambler.of(self, val, count)
        }

    }

    @CompileStatic
    static class CollectionCategory {

        public
        static <V, T extends Collection<V>> CollectionValue<V, T> of(T collection, Value<V> value, Integer count = null) {
            return DataScrambler.of(collection, value, count)
        }

        public static <T> Value<T> randomOf(List<T> values) {
            return DataScrambler.randomOf(values)
        }

        public static <T> Value<T> randomOf(Collection<T> collection) {
            return DataScrambler.randomOf(collection)
        }

    }

    @CompileStatic
    static class DateCategory {

        public static IncrementalDate increment(Date self, Integer calendarField = null, Integer step = null) {
            return DataScrambler.increment(self, calendarField, step);
        }

        static IncrementalDate increment(Date self, Map<Integer, Integer> calendarFieldStepMap) {
            return DataScrambler.increment(self, calendarFieldStepMap);
        }

        static ArrayValue<Date> incrementArray(Date self, Integer step = null, Integer count = null) {
            return DataScrambler.incrementArray(self, step, count)
        }

        static ArrayValue<Date> incrementArray(Date self, Map<Integer, Integer> calendarFieldStepMap, Integer count = null) {
            return DataScrambler.incrementArray(self, calendarFieldStepMap, count)
        }

        static ArrayValue<Date> incrementArray(Date self, Integer calendarField, Integer step, Integer count) {
            return DataScrambler.incrementArray(self, calendarField, step, count)
        }

        static RandomDate random(Date self, Date minimum = null, Date maximum = null) {
            return DataScrambler.random(self, minimum, maximum)
        }

        static ArrayValue<Date> randomArray(Date self, Date minimum = null, Date maximum = null, Integer count = null) {
            return DataScrambler.randomArray(self, minimum, maximum, count)
        }

    }

    @CompileStatic
    static class MapCategory {

        public static <K> MapValue<K> of(Map<K, Object> self, Map<K, Value> keyValueMap) {
            return DataScrambler.of(self, keyValueMap)
        }

    }

    @CompileStatic
    static class NumberCategory {

        static <T extends Number> Value<T> increment(T self, T step = null) {
            return DataScrambler.increment(self, step)
        }

        static <T extends Number> Value incrementArray(T self, T step = null, Integer count = null) {
            return DataScrambler.incrementArray(self, step, count)
        }

        static <T extends Number> Value<T> random(T value) {
            return DataScrambler.random(value)
        }

        static <T extends Number> Value<T> random(T minimum, T maximum) {
            return DataScrambler.random(minimum, maximum)
        }

        static <T extends Number> Value<T> random(T val, T minimum, T maximum) {
            return DataScrambler.random(val, minimum, maximum);
        }

    }

    @CompileStatic
    static class ObjectCategory {

        static <T> Value<T> constant(T self) {
            return DataScrambler.constant(self)
        }

        static <T> Value<T> randomOf(T[] self) {
            return DataScrambler.randomOf(self)
        }

    }

    @CompileStatic
    static class StringCategory {

        static IncrementalString increment(String self, String pattern = null, Integer index = null) {
            return DataScrambler.increment(self, pattern, index)
        }

        static ArrayValue<String> incrementArray(String self, Integer count) {
            return DataScrambler.incrementArray(self, count)
        }

        static ArrayValue<String> incrementArray(String self, String pattern, Integer count) {
            return DataScrambler.incrementArray(self, pattern, count)
        }

        static ArrayValue<String> incrementArray(String value, Integer index, Integer count) {
            return DataScrambler.incrementArray(value, index, count)
        }

        static ArrayValue<String> incrementArray(String value, String pattern, Integer index, Integer count) {
            return DataScrambler.incrementArray(value, pattern, index, count)
        }

        static RandomString random(String value, Integer count = null) {
            return DataScrambler.random(value, count)
        }

        static ArrayValue<String> randomArray(String value, Integer count = null, Integer arrayCount = null) {
            return DataScrambler.randomArray(value, count, arrayCount)
        }

    }

}
