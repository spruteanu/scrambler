package org.prismus.scrambler.builder

import groovy.transform.CompileStatic
import org.codehaus.groovy.control.CompilerConfiguration
import org.codehaus.groovy.control.customizers.ImportCustomizer
import org.codehaus.groovy.runtime.ResourceGroovyMethods
import org.prismus.scrambler.Value
import org.prismus.scrambler.value.Constant
import org.prismus.scrambler.value.Incremental
import org.prismus.scrambler.value.InstanceValue
import org.prismus.scrambler.value.ParentValue
import org.prismus.scrambler.value.Util
import org.prismus.scrambler.value.ValueArray
import org.prismus.scrambler.value.ValueCollection
import org.prismus.scrambler.value.ValueMap

/**
 * todo: add description
 *
 * @author Serge Pruteanu
 */
class ValueDefinition extends Script {
    private Properties configurationProperties
    private GroovyShell shell

    InstanceValue instanceValue
    ValueDefinition parent
    Boolean introspect

    Map<ValuePredicate, Value> propertyValueMap = [:]
    Map<ValuePredicate, Value> typeValueMap = [:]
    protected List<InstanceValue> instanceValues = []

    ValueDefinition() {
    }

    ValueDefinition(Map<String, Value> propertyValueMap) {
        of(propertyValueMap)
    }

    @Override
    Object run() {
        return this
    }

    @CompileStatic
    boolean shouldIntrospect() {
        return introspect != null && introspect
    }

    @CompileStatic
    void setConfigurationProperties(Properties configurationProperties) {
        this.configurationProperties = configurationProperties
    }

    @CompileStatic
    Properties getConfigurationProperties() {
        return configurationProperties
    }

    @CompileStatic
    ValueDefinition build() {
        for (final value : instanceValues) {
            value.build()
        }
        return this
    }

    @CompileStatic
    protected List<ValueDefinition> getParents() {
        final parents = new LinkedList<ValueDefinition>()
        ValueDefinition parent = this.parent
        while (parent) {
            parents.add(parent)
            parent = this.parent?.parent
        }
        parents.add(this)
        return parents
    }

    @CompileStatic
    protected Map<ValuePredicate, Value> getPredicateValueMapDeep() {
        final resultMap = new LinkedHashMap<ValuePredicate, Value>()
        final valueMap = new LinkedHashMap<ValuePredicate, Value>()
        final typeMap = new LinkedHashMap<ValuePredicate, Value>()
        for (final parent : parents) {
            valueMap.putAll(parent.propertyValueMap)
            typeMap.putAll(parent.typeValueMap)
        }
        resultMap.putAll(valueMap)
        resultMap.putAll(typeMap)
        return resultMap
    }

    @CompileStatic
    protected Map<ValuePredicate, Value> getPredicateValueMap() {
        if (introspect) {
            return getPredicateValueMapDeep()
        }
        final resultMap = new LinkedHashMap<ValuePredicate, Value>()
        resultMap.putAll(propertyValueMap)
        resultMap.putAll(typeValueMap)
        return resultMap
    }

    protected void registerPredicateValue(TypePredicate valuePredicate, Value value) {
        typeValueMap.put(valuePredicate, value)
    }

    protected void registerPredicateValue(ValuePredicate valuePredicate, Value value) {
        propertyValueMap.put(valuePredicate, value)
    }

    protected void registerPredicateValue(ValuePredicate valuePredicate, ParentValue value) {
        value.setDefinition(this)
        registerPredicateValue(valuePredicate, (Value) value)
    }

    protected ValueDefinition lookupRegisterParent(Value value) {
        if (value instanceof DefinitionRegistrable) {
            ((DefinitionRegistrable) value).register(this)
        }
        return this
    }

    protected void registerPredicateValue(ValuePredicate valuePredicate, ValueCollection value) {
        lookupRegisterParent(value.instance)
        registerPredicateValue(valuePredicate, (Value) value)
    }

    protected void registerPredicateValue(ValuePredicate valuePredicate, ValueArray value) {
        lookupRegisterParent(value.instance)
        registerPredicateValue(valuePredicate, (Value) value)
    }

    protected void registerPredicateValue(ValuePredicate valuePredicate, InstanceValue value) {
        value.parentDefinition = this
        instanceValues.add(value)
        if (valuePredicate != null) {
            registerPredicateValue(valuePredicate, (Value) value)
        } else {
            Util.checkNullValue(value.predicate)
            registerPredicateValue(value.predicate, (Value) value)
        }
    }

    ValueDefinition parseText(String definitionText) {
        if (shell == null) {
            shell = createGroovyShell()
        }
        final definition = (ValueDefinition) shell.evaluate(
                definitionText
                        + "\n    build()"
                        + "\n    return this"
        )

        definition.parent = this
        propertyValueMap.putAll(definition.propertyValueMap)
        typeValueMap.putAll(definition.typeValueMap)
        if (definition.instanceValue != null) {
            definition.instanceValue.parentDefinition = this
            instanceValues.add(definition.instanceValue)
        }
        return definition
    }

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

    protected GroovyShell createGroovyShell() {
        final compilerConfiguration = new CompilerConfiguration()
        compilerConfiguration.setScriptBaseClass(ValueDefinition.name)

        final importCustomizer = new ImportCustomizer()
        importCustomizer.addStarImports(Value.package.name)
        importCustomizer.addStarImports(Constant.package.name)
        importCustomizer.addStarImports(getClass().package.name)
        compilerConfiguration.addCompilationCustomizers(importCustomizer)

        return new GroovyShell(compilerConfiguration)
    }

    void setConfigurationProperties(String configurationResource) throws IOException {
        setConfigurationProperties(getClass().getResourceAsStream(configurationResource))
    }

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

    ValueDefinition of(Class type, Boolean introspect = null, Collection constructorArgs = null, Closure defCl = null) {
        Util.checkNullValue(type)
        this.introspect = introspect
        instanceValue = new InstanceValue(
                definition: this,
                type: type,
                constructorArguments: constructorArgs,
                predicate: new TypePredicate(type),
                definitionClosure: defCl,
        )
        instanceValue.build()
        return this
    }

    //------------------------------------------------------------------------------------------------------------------
    // Object Methods
    //------------------------------------------------------------------------------------------------------------------
    ValueDefinition of(Object value) {
        Util.checkNullValue(value)
        registerPredicateValue(new TypePredicate(value.class), new Constant(value))
        return this
    }

    ValueDefinition constant(Object value) {
        Util.checkNullValue(value)
        registerPredicateValue(new TypePredicate(type: value.class), new Constant(value))
        return this
    }

    def propertyMissing(String name, value) {
        println "Property '$name' with value '$value' is missing"
    }

    //------------------------------------------------------------------------------------------------------------------
    // Number Methods
    //------------------------------------------------------------------------------------------------------------------
    ValueDefinition incremental(Number value, Number step = null) {
        Util.checkNullValue(value)
        registerPredicateValue(new TypePredicate(type: value.class), Incremental.of(value, step))
        return this
    }

    ValueDefinition random(Number minimum = null, Number maximum = null) {
        Util.checkNullValue(minimum, maximum)
        final value = Util.getNotNullValue(minimum, maximum)
        registerPredicateValue(new TypePredicate(type: value.class), org.prismus.scrambler.value.Random.of(minimum, maximum))
        return this
    }

    //------------------------------------------------------------------------------------------------------------------
    // Date Methods
    //------------------------------------------------------------------------------------------------------------------
    ValueDefinition incremental(Date value, Integer step = null, Integer calendarField = null) {
        Util.checkNullValue(value)
        registerPredicateValue(new TypePredicate(type: value.class), Incremental.of(value, step, calendarField))
        return this
    }

    ValueDefinition random(Date value, Date minimum = null, Date maximum = null) {
        Util.checkNullValue(value)
        registerPredicateValue(new TypePredicate(type: Date), org.prismus.scrambler.value.Random.of(value, minimum, maximum))
        return this
    }

    //------------------------------------------------------------------------------------------------------------------
    // String Methods
    //------------------------------------------------------------------------------------------------------------------
    ValueDefinition incremental(String value, Integer index) {
        Util.checkNullValue(value)
        registerPredicateValue(new TypePredicate(type: value.class), Incremental.of(value, index))
        return this
    }

    ValueDefinition incremental(String value, String pattern = null, Integer index = null) {
        Util.checkNullValue(value)
        registerPredicateValue(new TypePredicate(type: String), Incremental.of(value, pattern, index))
        return this
    }

    ValueDefinition random(String value, Integer count = null, Boolean includeLetters = null, Boolean includeNumbers = null) {
        Util.checkNullValue(value)
        registerPredicateValue(new TypePredicate(type: String), org.prismus.scrambler.value.Random.of(value, count, includeLetters, includeNumbers))
        return this
    }

    ValueDefinition of(String propertyName, Object value) {
        registerPredicateValue(Util.createPropertyPredicate(propertyName), new Constant(value))
        return this
    }

    ValueDefinition of(String propertyName, Value value) {
        Util.checkNullValue(value)
        registerPredicateValue(Util.createPropertyPredicate(propertyName), value)
        return this
    }

    ValueDefinition parent(String propertyName, Class parentPredicate) {
        Util.checkNullValue(parentPredicate)
        parent(Util.createPropertyPredicate(propertyName), new TypePredicate(type: parentPredicate))
        return this
    }

    ValueDefinition parent(String propertyName) {
        registerPredicateValue(Util.createPropertyPredicate(propertyName), new ParentValue())
        return this
    }

    //------------------------------------------------------------------------------------------------------------------
    // Collection Methods
    //------------------------------------------------------------------------------------------------------------------
    ValueDefinition randomOf(Collection values) {
        Util.checkNullValue(values)
        Util.checkEmptyCollection(values)

        final value = values.iterator().next()
        registerPredicateValue(new TypePredicate(type: value.class), org.prismus.scrambler.value.Random.randomOf(values))
        return this
    }

    @SuppressWarnings("GroovyAssignabilityCheck")
    ValueDefinition random(Collection collection, Value value, int count = 0) {
        Util.checkNullValue(value)
        Util.checkNullValue(collection)
        registerPredicateValue(new TypePredicate(Collection), new ValueCollection(collection, count, value))
        return this
    }

    //------------------------------------------------------------------------------------------------------------------
    // Value Methods
    //------------------------------------------------------------------------------------------------------------------
    ValueDefinition of(Value value) {
        Util.checkNullValue(value)
        final value1 = value.value
        Util.checkNullValue(value1)
        registerPredicateValue(new TypePredicate(value1.class), value)
        return this
    }

    ValueDefinition of(InstanceValue value) {
        Util.checkNullValue(value)
        registerPredicateValue((ValuePredicate) null, value)
        return this
    }

    ValueDefinition of(ValuePredicate valuePredicate, Value value) {
        Util.checkNullValue(valuePredicate)
        Util.checkNullValue(value)
        registerPredicateValue(valuePredicate, value)
        return this
    }

    ValueDefinition of(ValuePredicate valuePredicate, Object value) {
        Util.checkNullValue(valuePredicate)
        registerPredicateValue(valuePredicate, new Constant(value))
        return this
    }

    ValueDefinition parent(ValuePredicate valuePredicate, ValuePredicate parentPredicate) {
        Util.checkNullValue(valuePredicate)
        registerPredicateValue(valuePredicate, new ParentValue(definition: this, predicate: parentPredicate))
        return this
    }

    //------------------------------------------------------------------------------------------------------------------
    // Class Methods
    //------------------------------------------------------------------------------------------------------------------
    ValueDefinition of(Class type, Object value) {
        Util.checkNullValue(type)
        registerPredicateValue(new TypePredicate(type: type), new Constant(value))
        return this
    }

    ValueDefinition of(Class type, Value value) {
        Util.checkNullValue(type)
        Util.checkNullValue(value)
        registerPredicateValue(((ValuePredicate) new TypePredicate(type: type)), value)
        return this
    }

    ValueDefinition parent(Class type) {
        Util.checkNullValue(type)
        final ValuePredicate predicate = new TypePredicate(type: type)
        registerPredicateValue((ValuePredicate) predicate, new ParentValue())
        return this
    }

    ValueDefinition parent(Class type, String parentPredicate) {
        parent(type, Util.createPropertyPredicate(parentPredicate))
        return this
    }

    ValueDefinition parent(Class type, Class parentPredicate) {
        Util.checkNullValue(parentPredicate)
        parent(type, new TypePredicate(type: parentPredicate))
        return this
    }

    ValueDefinition parent(Class type, ValuePredicate parentPredicate) {
        Util.checkNullValue(type)
        final ValuePredicate predicate = new TypePredicate(type: type)
        registerPredicateValue((ValuePredicate) predicate, new ParentValue(predicate: parentPredicate))
        return this
    }

    //------------------------------------------------------------------------------------------------------------------
    // Map Methods
    //------------------------------------------------------------------------------------------------------------------
    ValueDefinition of(Map<Object, Object> props) {
        Util.checkNullValue(props)
        for (final Map.Entry entry : props.entrySet()) {
            final key = entry.key
            Util.checkNullValue(key)

            final value = entry.value
            if (String.isInstance(key)) {
                of((String) key, value)
            } else if (Class.isInstance(key)) {
                of((Class) key, value)
            } else if (ValuePredicate.isInstance(key)) {
                of((ValuePredicate) key, value)
            } else {
                throw new IllegalArgumentException("Key should be of following types: ${[String, Class, ValuePredicate]}; passed map: $props")
            }
        }
        return this
    }

    ValueDefinition constant(Map props) {
        Util.checkNullValue(props)
        for (final Map.Entry entry : props.entrySet()) {
            final key = entry.key
            Util.checkNullValue(key)

            final value = entry.value
            if (Value.isInstance(value)) {
                throw new IllegalArgumentException("Constant values can't be of Value type; passed map: $props")
            }
            if (String.isInstance(key)) {
                of((String) key, new Constant(value))
            } else if (Class.isInstance(key)) {
                of((Class) key, new Constant(value))
            } else if (ValuePredicate.isInstance(key)) {
                of((ValuePredicate) key, new Constant(value))
            } else {
                throw new IllegalArgumentException("Key should be of following types: ${[String, Class, ValuePredicate]}; passed map: $props")
            }
        }
        return this
    }

    @SuppressWarnings("GroovyAssignabilityCheck")
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
                        propertyValueMap: propertyValueMap,
                        predicate: new TypePredicate(type: (Class) delegate),
                        definitionClosure: defCl
                )
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
                return ValueArray.of(val, (Class)delegate, count, randomCount)
            }
        }
    }

    static {
        register()
    }

}
