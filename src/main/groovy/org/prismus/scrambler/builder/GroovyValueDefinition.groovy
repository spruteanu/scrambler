package org.prismus.scrambler.builder

import groovy.transform.CompileStatic
import org.codehaus.groovy.control.CompilerConfiguration
import org.codehaus.groovy.control.customizers.ImportCustomizer
import org.codehaus.groovy.runtime.ResourceGroovyMethods
import org.prismus.scrambler.Value
import org.prismus.scrambler.value.*

/**
 * todo: add description
 *
 * @author Serge Pruteanu
 */
@SuppressWarnings(["GroovyAssignabilityCheck", "UnnecessaryQualifiedReference"])
class GroovyValueDefinition extends Script {
    private Properties configurationProperties
    private GroovyShell shell

    private ValueDefinition definition = new ValueDefinition()

    GroovyValueDefinition() {
    }

    @CompileStatic
    ValueDefinition getDefinition() {
        return definition
    }

    @Override
    @CompileStatic
    Object run() {
        return this
    }

    @CompileStatic
    ValueDefinition parseText(String definitionText) {
        if (!shell) {
            shell = createGroovyShell()
        }
        final GroovyValueDefinition result = (GroovyValueDefinition)shell.evaluate(
                definitionText
                        + "\n    definition.build()"
                        + "\n    return this"
        )
        definition = result.definition
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
        compilerConfiguration.setScriptBaseClass(GroovyValueDefinition.name)

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
    //------------------------------------------------------------------------------------------------------------------
    // Object Methods
    //------------------------------------------------------------------------------------------------------------------
    @CompileStatic
    ValueDefinition of(Object value) {
        return definition.of(value)
    }

    @CompileStatic
    ValueDefinition constant(Object value) {
        return definition.constant(value)
    }

    //------------------------------------------------------------------------------------------------------------------
    // Number Methods
    //------------------------------------------------------------------------------------------------------------------
    @CompileStatic
    ValueDefinition incremental(Number value, Number step = null) {
        return definition.incremental(value, step)
    }

    @CompileStatic
    ValueDefinition random(Number minimum = null, Number maximum = null) {
        return definition.incremental(minimum, maximum)
    }

    //------------------------------------------------------------------------------------------------------------------
    // Date Methods
    //------------------------------------------------------------------------------------------------------------------
    @CompileStatic
    ValueDefinition incremental(Date value, Integer step = null, Integer calendarField = null) {
        return definition.incremental(value, step, calendarField)
    }

    @CompileStatic
    ValueDefinition random(Date value, Date minimum = null, Date maximum = null) {
        return definition.random(value, minimum, maximum)
    }

    //------------------------------------------------------------------------------------------------------------------
    // String Methods
    //------------------------------------------------------------------------------------------------------------------
    @CompileStatic
    ValueDefinition incremental(String value, Integer index) {
        return definition.incremental(value, index)
    }

    @CompileStatic
    ValueDefinition incremental(String value, String pattern = null, Integer index = null) {
        return definition.incremental(value, pattern , index )
    }

    @CompileStatic
    ValueDefinition random(String value, Integer count = null, Boolean includeLetters = null, Boolean includeNumbers = null) {
        return definition.random(value, count , includeLetters , includeNumbers )
    }

    @CompileStatic
    ValueDefinition of(String propertyName, Object value) {
        return definition.of(propertyName, value)
    }

    @CompileStatic
    ValueDefinition of(String propertyName, Value value) {
        return definition.of(propertyName, value)
    }

    @CompileStatic
    ValueDefinition parent(String propertyName, Class parentPredicate) {
        return definition.parent(propertyName, parentPredicate)
    }

    @CompileStatic
    ValueDefinition parent(String propertyName) {
        return definition.parent(propertyName)
    }

    //------------------------------------------------------------------------------------------------------------------
    // Collection Methods
    //------------------------------------------------------------------------------------------------------------------
    @CompileStatic
    ValueDefinition randomOf(Collection values) {
        return definition.randomOf(values)
    }

    @SuppressWarnings("GroovyAssignabilityCheck")
    @CompileStatic
    ValueDefinition random(Collection collection, Value value, int count = 0) {
        return definition.random(collection, value, count)
    }

    //------------------------------------------------------------------------------------------------------------------
    // Value Methods
    //------------------------------------------------------------------------------------------------------------------
    @CompileStatic
    ValueDefinition of(Value value) {
        return definition.of(value)
    }

    @CompileStatic
    ValueDefinition of(InstanceValue value) {
        return definition.of(value)
    }

    @CompileStatic
    ValueDefinition of(ValuePredicate valuePredicate, Value value) {
        return definition.of(valuePredicate, value)
    }

    @CompileStatic
    ValueDefinition of(ValuePredicate valuePredicate, Object value) {
        return definition.of(valuePredicate, value)
    }

    @CompileStatic
    ValueDefinition parent(ValuePredicate valuePredicate, ValuePredicate parentPredicate) {
        return definition.parent(valuePredicate, parentPredicate)
    }

    //------------------------------------------------------------------------------------------------------------------
    // Class Methods
    //------------------------------------------------------------------------------------------------------------------
    @CompileStatic
    ValueDefinition of(Class type, Object value) {
        return definition.of(type, value)
    }

    @CompileStatic
    ValueDefinition of(Class type, Value value) {
        return definition.of(type, value)
    }

    @CompileStatic
    ValueDefinition parent(Class type) {
        return definition.parent(type)
    }

    @CompileStatic
    ValueDefinition parent(Class type, String parentPredicate) {
        return definition.parent(type, parentPredicate)
    }

    @CompileStatic
    ValueDefinition parent(Class type, Class parentPredicate) {
        return definition.parent(type, parentPredicate)
    }

    @CompileStatic
    ValueDefinition parent(Class type, ValuePredicate parentPredicate) {
        return definition.parent(type, parentPredicate)
    }

    //------------------------------------------------------------------------------------------------------------------
    // Map Methods
    //------------------------------------------------------------------------------------------------------------------
    @CompileStatic
    ValueDefinition of(Map<Object, Object> props) {
        return definition.of(props)
    }

    @CompileStatic
    ValueDefinition constant(Map props) {
        return definition.constant(props)
    }

    //------------------------------------------------------------------------------------------------------------------
    static void register() {
        Object.metaClass {
            constant { ->
                return new Constant(delegate)
            }
        }

        Number.metaClass {
            incremental { Number step = null ->
                return Incremental.of((Number) delegate, step)
            }

            random { Number minimum = null, Number maximum = null ->
                return org.prismus.scrambler.value.Random.of((Number) delegate, minimum, maximum)
            }
        }

        Date.metaClass {
            incremental { Integer step = null, Integer calendarField = null ->
                return Incremental.of((Date) delegate, step, calendarField)
            }

            random { Date minimum = null, Date maximum = null ->
                return org.prismus.scrambler.value.Random.of((Date) delegate, minimum, maximum)
            }
        }

        String.metaClass {
            incremental { String pattern = null, Integer index = null ->
                return Incremental.of((String) delegate, pattern, index)
            }

            random { Integer count = null, Boolean includeLetters = null, Boolean includeNumbers = null ->
                return org.prismus.scrambler.value.Random.of((String) delegate, count, includeLetters, includeNumbers)
            }
        }

        Collection.metaClass {
            of { Value val, Integer count = null, Boolean randomCount = null ->
                Util.checkNullValue(val)
                return new ValueCollection((Collection) delegate, count, val, randomCount)
            }

            randomOf { ->
                final collection = (Collection) delegate
                Util.checkEmptyCollection(collection)
                return org.prismus.scrambler.value.Random.randomOf(collection)
            }
        }

        Map.metaClass {
            of { Value entryKey, Value entryValue, Integer count = null, Boolean randomCount = null ->
                Util.checkNullValue(entryKey)
                Util.checkNullValue(entryValue)
                return new ValueMap((Map) delegate, entryKey, entryValue, count, randomCount)
            }
        }

        Class.metaClass {
            of { Closure defCl ->
                return new InstanceValue(
                        type: (Class) delegate,
                        predicate: new TypePredicate(type: (Class) delegate),
                        definitionClosure: defCl
                )
            }

            of { Map<Object, Object> propertyValueMap, Closure defCl = null ->
                return new InstanceValue(
                        type: (Class) delegate,
                        predicate: new TypePredicate(type: (Class) delegate),
                        definitionClosure: defCl
                ).usingDefinitions(propertyValueMap)
            }

            of { String propertyName, Closure defCl ->
                return new InstanceValue(
                        type: (Class) delegate,
                        predicate: Util.createPropertyPredicate(propertyName),
                        definitionClosure: defCl
                )
            }

            of { Collection constructorArgs, Closure defCl ->
                return new InstanceValue(
                        type: (Class) delegate,
                        constructorArguments: constructorArgs,
                        predicate: new TypePredicate(type: (Class) delegate),
                        definitionClosure: defCl
                )
            }

            of { String propertyName, Collection constructorArgs, Closure defCl ->
                return new InstanceValue(
                        type: (Class) delegate,
                        constructorArguments: constructorArgs,
                        predicate: Util.createPropertyPredicate(propertyName),
                        definitionClosure: defCl
                )
            }

            array { Value val, Integer count = null, Boolean randomCount = null ->
                return ValueArray.of(val, (Class) delegate, count, randomCount)
            }

            reference { String propertyPredicate = null ->
                return new ReferenceValue(new TypePredicate((Class) delegate), propertyPredicate != null ? Util.createPropertyPredicate(propertyPredicate) : null)
            }
        }
    }

    static {
        register()
    }

}
