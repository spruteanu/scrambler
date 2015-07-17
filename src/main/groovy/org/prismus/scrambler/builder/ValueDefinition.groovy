package org.prismus.scrambler.builder

import groovy.transform.CompileStatic
import org.prismus.scrambler.Value
import org.prismus.scrambler.value.*

/**
 * todo: add description
 *
 * @author Serge Pruteanu
 */
@SuppressWarnings("UnnecessaryQualifiedReference")
class ValueDefinition {
    InstanceValue instanceValue
    ValueDefinition parent
    Boolean introspect

    Map<ValuePredicate, Value> propertyValueMap = [:]
    Map<ValuePredicate, Value> typeValueMap = [:]
    protected List<InstanceValue> instanceValues = []

    ValueDefinition() {
    }

    ValueDefinition(Map<Object, Value> propertyValueMap) {
        of(propertyValueMap)
    }

    @CompileStatic
    boolean shouldIntrospect() {
        return introspect != null && introspect
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
    Map<ValuePredicate, Value> getPredicateValueMapDeep() {
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
    Map<ValuePredicate, Value> getPredicateValueMap() {
        if (introspect) {
            return getPredicateValueMapDeep()
        }
        final resultMap = new LinkedHashMap<ValuePredicate, Value>()
        resultMap.putAll(propertyValueMap)
        resultMap.putAll(typeValueMap)
        return resultMap
    }

    @CompileStatic
    protected void registerTypePredicateValue(TypePredicate valuePredicate, Value value) {
        typeValueMap.put(valuePredicate, value)
    }

    @CompileStatic
    protected void registerPredicateValue(ValuePredicate valuePredicate, Value value) {
        propertyValueMap.put(valuePredicate, value)
    }

    @CompileStatic
    protected void registerPredicateValue(ValuePredicate valuePredicate, ParentValue value) {
        value.setDefinition(this)
        registerPredicateValue(valuePredicate, (Value) value)
    }

    @CompileStatic
    protected ValueDefinition lookupRegisterParent(Value value) {
        if (value instanceof DefinitionRegistrable) {
            value.register(this)
        }
        return this
    }

    @CompileStatic
    protected void registerPredicateValue(ValuePredicate valuePredicate, ValueCollection value) {
        lookupRegisterParent(value.instance)
        registerPredicateValue(valuePredicate, (Value) value)
    }

    @CompileStatic
    protected void registerPredicateValue(ValuePredicate valuePredicate, ValueArray value) {
        lookupRegisterParent(value.instance)
        registerPredicateValue(valuePredicate, (Value) value)
    }

    @CompileStatic
    protected void registerPredicateValue(ValuePredicate valuePredicate, InstanceValue value) {
        value.parentDefinition = this
        instanceValues.add(value)
        if (valuePredicate == null) {
            Util.checkNullValue(value.predicate)
            valuePredicate = value.predicate
        }
        if (valuePredicate != null) {
            if (valuePredicate instanceof TypePredicate) {
                registerTypePredicateValue(valuePredicate, (Value) value)
            } else {
                registerPredicateValue(valuePredicate, (Value) value)
            }
        }
    }

    //------------------------------------------------------------------------------------------------------------------
    // Object Methods
    //------------------------------------------------------------------------------------------------------------------
    @CompileStatic
    ValueDefinition of(Object value) {
        Util.checkNullValue(value)
        registerTypePredicateValue(new TypePredicate(value.class), new Constant(value))
        return this
    }

    @CompileStatic
    ValueDefinition constant(Object value) {
        Util.checkNullValue(value)
        registerTypePredicateValue(new TypePredicate(value.class), new Constant(value))
        return this
    }

    //------------------------------------------------------------------------------------------------------------------
    // Number Methods
    //------------------------------------------------------------------------------------------------------------------
    @CompileStatic
    ValueDefinition incremental(Number value, Number step = null) {
        Util.checkNullValue(value)
        registerTypePredicateValue(new TypePredicate(value.class), Incremental.of(value, step))
        return this
    }

    @CompileStatic
    ValueDefinition random(Number minimum = null, Number maximum = null) {
        Util.checkNullValue(minimum, maximum)
        final value = Util.getNotNullValue(minimum, maximum)
        registerTypePredicateValue(new TypePredicate(value.class), org.prismus.scrambler.value.Random.of(minimum, maximum))
        return this
    }

    //------------------------------------------------------------------------------------------------------------------
    // Date Methods
    //------------------------------------------------------------------------------------------------------------------
    @CompileStatic
    ValueDefinition incremental(Date value, Integer step = null, Integer calendarField = null) {
        Util.checkNullValue(value)
        registerTypePredicateValue(new TypePredicate(value.class), Incremental.of(value, step, calendarField))
        return this
    }

    @CompileStatic
    ValueDefinition random(Date value, Date minimum = null, Date maximum = null) {
        Util.checkNullValue(value)
        registerTypePredicateValue(new TypePredicate(Date), org.prismus.scrambler.value.Random.of(value, minimum, maximum))
        return this
    }

    //------------------------------------------------------------------------------------------------------------------
    // String Methods
    //------------------------------------------------------------------------------------------------------------------
    @CompileStatic
    ValueDefinition incremental(String value, Integer index) {
        Util.checkNullValue(value)
        registerTypePredicateValue(new TypePredicate(value.class), Incremental.of(value, index))
        return this
    }

    @CompileStatic
    ValueDefinition incremental(String value, String pattern = null, Integer index = null) {
        Util.checkNullValue(value)
        registerTypePredicateValue(new TypePredicate(String), Incremental.of(value, pattern, index))
        return this
    }

    @CompileStatic
    ValueDefinition random(String value, Integer count = null, Boolean includeLetters = null, Boolean includeNumbers = null) {
        Util.checkNullValue(value)
        registerTypePredicateValue(new TypePredicate(String), org.prismus.scrambler.value.Random.of(value, count, includeLetters, includeNumbers))
        return this
    }

    @CompileStatic
    ValueDefinition of(String propertyName, Object value) {
        registerPredicateValue(Util.createPropertyPredicate(propertyName), new Constant(value))
        return this
    }

//    @CompileStatic // todo Serge: ???????!!! fails on static compile
    ValueDefinition of(String propertyName, Value value) {
        Util.checkNullValue(value)
        registerPredicateValue(Util.createPropertyPredicate(propertyName), value)
        return this
    }

    @CompileStatic
    ValueDefinition parent(String propertyName, Class parentPredicate) {
        Util.checkNullValue(parentPredicate)
        parent(Util.createPropertyPredicate(propertyName), new TypePredicate(parentPredicate))
        return this
    }

    @CompileStatic
    ValueDefinition parent(String propertyName) {
        registerPredicateValue(Util.createPropertyPredicate(propertyName), new ParentValue(this))
        return this
    }

    //------------------------------------------------------------------------------------------------------------------
    // Collection Methods
    //------------------------------------------------------------------------------------------------------------------
    @CompileStatic
    ValueDefinition randomOf(Collection values) {
        Util.checkNullValue(values)
        Util.checkEmptyCollection(values)

        final value = values.iterator().next()
        registerTypePredicateValue(new TypePredicate(value.class), org.prismus.scrambler.value.Random.randomOf(values))
        return this
    }

    @SuppressWarnings("GroovyAssignabilityCheck")
    @CompileStatic
    ValueDefinition random(Collection collection, Value value, int count = 0) {
        Util.checkNullValue(value)
        Util.checkNullValue(collection)
        registerTypePredicateValue(new TypePredicate(Collection), new ValueCollection(collection, count, value))
        return this
    }

    //------------------------------------------------------------------------------------------------------------------
    // Value Methods
    //------------------------------------------------------------------------------------------------------------------
    @CompileStatic
    ValueDefinition of(Value value) {
        Util.checkNullValue(value)
        final value1 = value.get()
        Util.checkNullValue(value1)
        registerTypePredicateValue(new TypePredicate(value1.class), value)
        return this
    }

    @CompileStatic
    ValueDefinition of(InstanceValue value) {
        Util.checkNullValue(value)
        registerPredicateValue((ValuePredicate) null, value)
        return this
    }

    @CompileStatic
    ValueDefinition of(ValuePredicate valuePredicate, Value value) {
        Util.checkNullValue(valuePredicate)
        Util.checkNullValue(value)
        registerPredicateValue(valuePredicate, value)
        return this
    }

    @CompileStatic
    ValueDefinition of(ValuePredicate valuePredicate, Object value) {
        Util.checkNullValue(valuePredicate)
        registerPredicateValue(valuePredicate, new Constant(value))
        return this
    }

    @CompileStatic
    ValueDefinition parent(ValuePredicate valuePredicate, ValuePredicate parentPredicate) {
        Util.checkNullValue(valuePredicate)
        registerPredicateValue(valuePredicate, new ParentValue(this, parentPredicate))
        return this
    }

    //------------------------------------------------------------------------------------------------------------------
    // Class Methods
    //------------------------------------------------------------------------------------------------------------------
    @CompileStatic
    ValueDefinition of(Class type, Object value) {
        Util.checkNullValue(type)
        registerTypePredicateValue(new TypePredicate(type), new Constant(value))
        return this
    }

//    @CompileStatic // todo Serge: ???????!!! fails on static compile
    ValueDefinition of(Class type, Value value) {
        Util.checkNullValue(type)
        Util.checkNullValue(value)
        registerPredicateValue(((ValuePredicate) new TypePredicate(type)), value)
        return this
    }

    @CompileStatic
    ValueDefinition of(Class type, Boolean introspect = null, Collection constructorArgs = null, Closure defCl = null) {
        Util.checkNullValue(type)
        this.introspect = introspect
        instanceValue = new InstanceValue(
                type: type,
                constructorArguments: constructorArgs,
                predicate: new TypePredicate(type),
                definitionClosure: defCl,
        ).usingDefinitions(this).build()
        return this
    }

    @CompileStatic
    ValueDefinition parent(Class type) {
        Util.checkNullValue(type)
        final ValuePredicate predicate = new TypePredicate(type)
        registerPredicateValue((ValuePredicate) predicate, new ParentValue(this))
        return this
    }

    @CompileStatic
    ValueDefinition parent(Class type, String parentPredicate) {
        parent(type, Util.createPropertyPredicate(parentPredicate))
        return this
    }

    @CompileStatic
    ValueDefinition parent(Class type, Class parentPredicate) {
        Util.checkNullValue(parentPredicate)
        parent(type, new TypePredicate(parentPredicate))
        return this
    }

    @CompileStatic
    ValueDefinition parent(Class type, ValuePredicate parentPredicate) {
        Util.checkNullValue(type)
        final ValuePredicate predicate = new TypePredicate(type)
        registerPredicateValue((ValuePredicate) predicate, new ParentValue(this, parentPredicate))
        return this
    }

    //------------------------------------------------------------------------------------------------------------------
    // Map Methods
    //------------------------------------------------------------------------------------------------------------------
//    @CompileStatic // todo Serge: ???????!!! fails on static compile
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

    @CompileStatic
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

}
