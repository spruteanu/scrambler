package org.prismus.scrambler.builder

import org.codehaus.groovy.control.CompilerConfiguration
import org.codehaus.groovy.control.customizers.ImportCustomizer
import org.codehaus.groovy.runtime.ResourceGroovyMethods
import org.prismus.scrambler.Value
import org.prismus.scrambler.value.Constant
import org.prismus.scrambler.value.Incremental
import org.prismus.scrambler.value.ValueArray
import org.prismus.scrambler.value.ValueCollection

/**
 * todo: add description
 *
 * @author Serge Pruteanu
 */
// todo add ValueArray for array type (object array and primitive one)
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

    boolean shouldIntrospect() {
        return introspect != null && introspect
    }

    void setConfigurationProperties(Properties configurationProperties) {
        this.configurationProperties = configurationProperties
    }

    protected Properties getConfigurationProperties() {
        return configurationProperties
    }

    protected ValueDefinition build() {
        for (final value : instanceValues) {
            value.build()
        }
        return this
    }

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

    protected void registerPredicateValue(ValuePredicate valuePredicate, ReferenceValue value) {
        value.definition = this
        registerPredicateValue(valuePredicate, (Value) value)
    }

    protected void registerPredicateValue(ValuePredicate valuePredicate, ParentValue value) {
        value.definition = this
        registerPredicateValue(valuePredicate, (Value) value)
    }

    protected ValueDefinition lookupRegisterParent(Value value) {
        if (value instanceof DefinitionRegistrable) {
            ((DefinitionRegistrable) value).registerDefinition(this)
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
        value.parent = this
        instanceValues.add(value)
        if (valuePredicate != null) {
            registerPredicateValue(valuePredicate, (Value) value)
        } else {
            ValueCategory.checkNullValue(value.predicate)
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
            definition.instanceValue.parent = this
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
                } catch (IOException ignore) {
                }
            }
        }
    }

    ValueDefinition forType(Class type, Boolean introspect = null, Collection constructorArgs = null, Closure defCl = null) {
        ValueCategory.checkNullValue(type)
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

    ValueDefinition forValue(Value value, Boolean introspect = null, Collection constructorArgs = null, Closure defCl = null) {
        ValueCategory.checkNullValue(value)
        this.introspect = introspect
        instanceValue = new InstanceValue(
                definition: this,
                type: value.class,
                constructorArguments: constructorArgs,
                predicate: new TypePredicate(value.class),
                definitionClosure: defCl,
        )
        instanceValue.build()
        return this
    }

    //------------------------------------------------------------------------------------------------------------------
    // Object Methods
    //------------------------------------------------------------------------------------------------------------------
    ValueDefinition of(Object value) {
        ValueCategory.checkNullValue(value)
        registerPredicateValue(new TypePredicate(value.class), new Constant(value))
        return this
    }

    ValueDefinition constant(Object value) {
        ValueCategory.checkNullValue(value)
        registerPredicateValue(new TypePredicate(type: value.class), new Constant(value))
        return this
    }

    //------------------------------------------------------------------------------------------------------------------
    // Number Methods
    //------------------------------------------------------------------------------------------------------------------
    ValueDefinition incremental(Number value, Number step = null) {
        ValueCategory.checkNullValue(value)
        registerPredicateValue(new TypePredicate(type: value.class), Incremental.of(value, step))
        return this
    }

    ValueDefinition random(Number minimum = null, Number maximum = null) {
        ValueCategory.checkNullValue(minimum, maximum)
        final value = ValueCategory.getNotNullValue(minimum, maximum)
        registerPredicateValue(new TypePredicate(type: value.class), org.prismus.scrambler.value.Random.of(minimum, maximum))
        return this
    }

    //------------------------------------------------------------------------------------------------------------------
    // Date Methods
    //------------------------------------------------------------------------------------------------------------------
    ValueDefinition incremental(Date value, Integer step = null, Integer calendarField = null) {
        ValueCategory.checkNullValue(value)
        registerPredicateValue(new TypePredicate(type: value.class), Incremental.of(value, step, calendarField))
        return this
    }

    ValueDefinition random(Date value, Date minimum = null, Date maximum = null) {
        ValueCategory.checkNullValue(value)
        registerPredicateValue(new TypePredicate(type: Date), org.prismus.scrambler.value.Random.of(value, minimum, maximum))
        return this
    }

    //------------------------------------------------------------------------------------------------------------------
    // String Methods
    //------------------------------------------------------------------------------------------------------------------
    ValueDefinition incremental(String value, Integer index) {
        ValueCategory.checkNullValue(value)
        registerPredicateValue(new TypePredicate(type: value.class), Incremental.of(value, index))
        return this
    }

    ValueDefinition incremental(String value, String pattern = null, Integer index = null) {
        ValueCategory.checkNullValue(value)
        registerPredicateValue(new TypePredicate(type: String), Incremental.of(value, pattern, index))
        return this
    }

    ValueDefinition random(String value, Integer count = null, Boolean includeLetters = null, Boolean includeNumbers = null) {
        ValueCategory.checkNullValue(value)
        registerPredicateValue(new TypePredicate(type: String), org.prismus.scrambler.value.Random.of(value, count, includeLetters, includeNumbers))
        return this
    }

    ValueDefinition of(String propertyName, Object value) {
        registerPredicateValue(ValueCategory.createPropertyPredicate(propertyName), new Constant(value))
        return this
    }

    ValueDefinition of(String propertyName, Value value) {
        ValueCategory.checkNullValue(value)
        registerPredicateValue(ValueCategory.createPropertyPredicate(propertyName), value)
        return this
    }

    ValueDefinition parent(String propertyName, String parentPredicate = null) {
        ValuePredicate parentPredicateInst = null
        if (parentPredicate) {
            parentPredicateInst = ValueCategory.createPropertyPredicate(parentPredicate)
        }
        parent(ValueCategory.createPropertyPredicate(propertyName), parentPredicateInst)
        return this
    }

    ValueDefinition parent(String propertyName, Class parentPredicate) {
        ValueCategory.checkNullValue(parentPredicate)
        parent(ValueCategory.createPropertyPredicate(propertyName), new TypePredicate(type: parentPredicate))
        return this
    }

    ValueDefinition parent(String propertyName, ValuePredicate parentPredicate) {
        ValueCategory.checkNullValue(parentPredicate)
        registerPredicateValue(ValueCategory.createPropertyPredicate(propertyName), new ParentValue(predicate: parentPredicate))
        return this
    }

    //------------------------------------------------------------------------------------------------------------------
    // Collection Methods
    //------------------------------------------------------------------------------------------------------------------
    ValueDefinition randomOf(Collection values) {
        ValueCategory.checkNullValue(values)
        ValueCategory.checkEmptyCollection(values)

        final value = values.iterator().next()
        registerPredicateValue(new TypePredicate(type: value.class), org.prismus.scrambler.value.Random.randomOf(values))
        return this
    }

    ValueDefinition random(Collection collection, Value value, int count = 0) {
        ValueCategory.checkNullValue(value)
        ValueCategory.checkNullValue(collection)
        registerPredicateValue(new TypePredicate(type: Collection), new ValueCollection(collection, count, value))
        return this
    }

    //------------------------------------------------------------------------------------------------------------------
    // Value Methods
    //------------------------------------------------------------------------------------------------------------------
    ValueDefinition of(Value value) {
        ValueCategory.checkNullValue(value)
        final value1 = value.value
        ValueCategory.checkNullValue(value1)
        registerPredicateValue(new TypePredicate(value1.class), value)
        return this
    }

    ValueDefinition of(InstanceValue value) {
        ValueCategory.checkNullValue(value)
        registerPredicateValue((ValuePredicate) null, value)
        return this
    }

    ValueDefinition of(ValuePredicate valuePredicate, Value value) {
        ValueCategory.checkNullValue(valuePredicate)
        ValueCategory.checkNullValue(value)
        registerPredicateValue(valuePredicate, value)
        return this
    }

    ValueDefinition of(ValuePredicate valuePredicate, Object value) {
        ValueCategory.checkNullValue(valuePredicate)
        registerPredicateValue(valuePredicate, new Constant(value))
        return this
    }

    ValueDefinition parent(ValuePredicate valuePredicate, ValuePredicate parentPredicate) {
        ValueCategory.checkNullValue(valuePredicate)
        registerPredicateValue(valuePredicate, new ParentValue(definition: this, predicate: parentPredicate))
        return this
    }

    //------------------------------------------------------------------------------------------------------------------
    // Class Methods
    //------------------------------------------------------------------------------------------------------------------
    ValueDefinition of(Class type, Object value) {
        ValueCategory.checkNullValue(type)
        registerPredicateValue(new TypePredicate(type: type), new Constant(value))
        return this
    }

    ValueDefinition of(Class type, Value value) {
        ValueCategory.checkNullValue(type)
        ValueCategory.checkNullValue(value)
        registerPredicateValue(((ValuePredicate) new TypePredicate(type: type)), value)
        return this
    }

    ValueDefinition parent(Class type) {
        ValueCategory.checkNullValue(type)
        final ValuePredicate predicate = new TypePredicate(type: type)
        registerPredicateValue((ValuePredicate) predicate, new ParentValue())
        return this
    }

    ValueDefinition parent(Class type, String parentPredicate) {
        parent(type, ValueCategory.createPropertyPredicate(parentPredicate))
        return this
    }

    ValueDefinition parent(Class type, Class parentPredicate) {
        ValueCategory.checkNullValue(parentPredicate)
        parent(type, new TypePredicate(type: parentPredicate))
        return this
    }

    ValueDefinition parent(Class type, ValuePredicate parentPredicate) {
        ValueCategory.checkNullValue(type)
        final ValuePredicate predicate = new TypePredicate(type: type)
        registerPredicateValue((ValuePredicate) predicate, new ParentValue(predicate: parentPredicate))
        return this
    }

    //------------------------------------------------------------------------------------------------------------------
    // Map Methods
    //------------------------------------------------------------------------------------------------------------------
    ValueDefinition of(Map<Object, Object> props) {
        ValueCategory.checkNullValue(props)
        for (final Map.Entry entry : props.entrySet()) {
            final key = entry.key
            ValueCategory.checkNullValue(key)

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
        ValueCategory.checkNullValue(props)
        for (final Map.Entry entry : props.entrySet()) {
            final key = entry.key
            ValueCategory.checkNullValue(key)

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


    static {
        ValueCategory.registerValueMetaClasses()
    }

}
