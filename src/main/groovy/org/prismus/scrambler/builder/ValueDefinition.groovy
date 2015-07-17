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
    ValueDefinition parent;

    Map<ValuePredicate, Value> propertyValueMap = [:]
    Map<ValuePredicate, Value> typeValueMap = [:]
    protected Map<ValuePredicate, InstanceValue> instanceValueMap = [:]

    ValueDefinition() {
    }

    ValueDefinition(Map<Object, Value> propertyValueMap) {
        of(propertyValueMap)
    }

    @CompileStatic
    ValueDefinition build() {
        for (final value : instanceValueMap.values()) {
            value.build(this)
        }
        return this
    }

    @CompileStatic
    Map<ValuePredicate, Value> getPredicateValueMap() {
        final resultMap = new LinkedHashMap<ValuePredicate, Value>()
        resultMap.putAll(propertyValueMap)
        resultMap.putAll(typeValueMap)
        return resultMap
    }

    @CompileStatic
    protected void lookupInstanceValue(ValuePredicate valuePredicate, Value value) {
        if (InstanceValue.isInstance(value)) {
            instanceValueMap.put(valuePredicate, (InstanceValue)value)
        } else if (ValueCollection.isInstance(value)) {
            lookupInstanceValue(valuePredicate, ((ValueCollection)value).getInstance())
        } else if (ValueArray.isInstance(value)) {
            lookupInstanceValue(valuePredicate, ((ValueArray)value).getInstance())
        }
    }

    @CompileStatic
    protected void registerTypePredicateValue(TypePredicate valuePredicate, Value value) {
        lookupInstanceValue(valuePredicate, value)
        typeValueMap.put(valuePredicate, value)
    }

    @CompileStatic
    protected void registerPredicateValue(ValuePredicate valuePredicate, Value value) {
        lookupInstanceValue(valuePredicate, value)
        propertyValueMap.put(valuePredicate, value)
    }

    @CompileStatic
    protected void registerPredicateValue(ValuePredicate valuePredicate, ReferenceValue value) {
        value.setDefinition(this)
        registerPredicateValue(valuePredicate, (Value) value)
    }

    @CompileStatic
    protected void registerPredicateValue(ValuePredicate valuePredicate, ValueCollection value) {
        registerPredicateValue(valuePredicate, (Value) value)
    }

    @CompileStatic
    protected void registerPredicateValue(ValuePredicate valuePredicate, ValueArray value) {
        registerPredicateValue(valuePredicate, (Value) value)
    }

    @CompileStatic
    protected void registerPredicateValue(ValuePredicate valuePredicate, InstanceValue value) {
        if (valuePredicate != null) {
            if (valuePredicate instanceof TypePredicate) {
                registerTypePredicateValue(valuePredicate, (Value) value)
            } else {
                registerPredicateValue(valuePredicate, (Value) value)
            }
        }
    }

    Value lookupValue(ValuePredicate predicate) { // might be lookup should be performed by matching
        Value result = propertyValueMap.get(predicate);
        if (result == null) {
            result = typeValueMap.get(predicate);
        }
        if (result == null && parent != null) {
            result = parent.lookupValue(predicate);
        }
        return result;
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
        final ValuePredicate predicate = Util.createPropertyPredicate(propertyName)
        registerPredicateValue(predicate, new ReferenceValue(this, predicate))
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
        registerPredicateValue(value.getPredicate(), value)
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
        registerPredicateValue(valuePredicate, new ReferenceValue(this, parentPredicate))
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
    ValueDefinition of(Class type, Collection constructorArgs = null, Closure defCl = null) {
        Util.checkNullValue(type)
        registerPredicateValue(new TypePredicate(type), new InstanceValue(
                type: type,
                constructorArguments: constructorArgs,
                definitionClosure: defCl,
        ))
        return this
    }

    @CompileStatic
    ValueDefinition parent(Class type) {
        Util.checkNullValue(type)
        final ValuePredicate predicate = new TypePredicate(type)
        registerPredicateValue((ValuePredicate) predicate, new ReferenceValue(this, predicate))
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
        registerPredicateValue((ValuePredicate) predicate, new ReferenceValue(this, parentPredicate))
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
