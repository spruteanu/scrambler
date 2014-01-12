package org.prismus.scrambler.builder

import org.prismus.scrambler.Value
import org.prismus.scrambler.value.Constant
import org.prismus.scrambler.value.Incremental
import org.prismus.scrambler.value.ValueCollection

/**
 * todo: add description
 *
 * @author Serge Pruteanu
 */
// todo add randomOf for array type (object array and primitive one) ???
// todo add ValueArray for array type (object array and primitive one)
class ValueDefinition extends Script {
    InstanceValue instanceValue

    ValueDefinition parent

    protected Map<ValuePredicate, Value> propertyValueMap = [:]
    protected Map<ValuePredicate, Value> typeValueMap = [:]
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

    ValueDefinition build() {
        for (final value : instanceValues) {
            value.build()
        }
        return this
    }

    Map<ValuePredicate, Value> getPredicateValueMap() {
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
        value.parent = parent
        registerPredicateValue(valuePredicate, (Value)value)
    }

    protected void registerPredicateValue(ValuePredicate valuePredicate, InstanceValue value) {
        value.parent = this
        instanceValues.add(value)
        if (valuePredicate != null) {
            registerPredicateValue(valuePredicate, (Value)value)
        } else {
            ValueCategory.checkNullValue(value.predicate)
            registerPredicateValue(value.predicate, (Value)value)
        }
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
    ValueDefinition incremental(Number value) {
        ValueCategory.checkNullValue(value)
        registerPredicateValue(new TypePredicate(type: value.class), Incremental.of(value))
        return this
    }

    ValueDefinition incremental(Number value, Number step) {
        ValueCategory.checkNullValue(value)
        registerPredicateValue(new TypePredicate(type: value.class), Incremental.of(value, step))
        return this
    }

    ValueDefinition random(Number value) {
        ValueCategory.checkNullValue(value)
        registerPredicateValue(new TypePredicate(type: value.class), org.prismus.scrambler.value.Random.of(value))
        return this
    }

    ValueDefinition random(Number value, Number minimum, Number maximum) {
        ValueCategory.checkNullValue(value)
        registerPredicateValue(new TypePredicate(type: value.class), org.prismus.scrambler.value.Random.of(value, minimum, maximum))
        return this
    }

    ValueDefinition random(Number minimum, Number maximum) {
        ValueCategory.checkNullValue(minimum, maximum)
        final value = ValueCategory.getNotNullValue(minimum, maximum)
        registerPredicateValue(new TypePredicate(type: value.class), org.prismus.scrambler.value.Random.of(minimum, maximum))
        return this
    }


    //------------------------------------------------------------------------------------------------------------------
    // Date Methods
    //------------------------------------------------------------------------------------------------------------------
    ValueDefinition incremental(Date value) {
        ValueCategory.checkNullValue(value)
        registerPredicateValue(new TypePredicate(type: value.class), Incremental.of(value))
        return this
    }

    ValueDefinition incremental(Date value, int step) {
        ValueCategory.checkNullValue(value)
        registerPredicateValue(new TypePredicate(type: value.class), Incremental.of(value, step))
        return this
    }

    ValueDefinition incremental(Date value, int step, int calendarField) {
        ValueCategory.checkNullValue(value)
        registerPredicateValue(new TypePredicate(type: value.class), Incremental.of(value, step, calendarField))
        return this
    }

    ValueDefinition random(Date value) {
        ValueCategory.checkNullValue(value)
        registerPredicateValue(new TypePredicate(type: value.class), org.prismus.scrambler.value.Random.of(value))
        return this
    }

    ValueDefinition random(Date minimum, Date maximum) {
        ValueCategory.checkNullValue(minimum, maximum)
        registerPredicateValue(new TypePredicate(type: Date), org.prismus.scrambler.value.Random.of(minimum, maximum))
        return this
    }

    ValueDefinition random(Date value, Date minimum, Date maximum) {
        ValueCategory.checkNullValue(value)
        registerPredicateValue(new TypePredicate(type: value.class), org.prismus.scrambler.value.Random.of(value, minimum, maximum))
        return this
    }


    //------------------------------------------------------------------------------------------------------------------
    // String Methods
    //------------------------------------------------------------------------------------------------------------------
    ValueDefinition incremental(String value) {
        ValueCategory.checkNullValue(value)
        registerPredicateValue(new TypePredicate(type: value.class), Incremental.of(value))
        return this
    }

    ValueDefinition incremental(String value, int index) {
        ValueCategory.checkNullValue(value)
        registerPredicateValue(new TypePredicate(type: value.class), Incremental.of(value, index))
        return this
    }

    ValueDefinition incremental(String value, String pattern) {
        ValueCategory.checkNullValue(value)
        registerPredicateValue(new TypePredicate(type: value.class), Incremental.of(value, pattern))
        return this
    }

    ValueDefinition incremental(String value, String pattern, Integer index) {
        ValueCategory.checkNullValue(value)
        registerPredicateValue(new TypePredicate(type: String), Incremental.of(value, pattern, index))
        return this
    }

    ValueDefinition random(String value) {
        ValueCategory.checkNullValue(value)
        registerPredicateValue(new TypePredicate(type: String), org.prismus.scrambler.value.Random.of(value))
        return this
    }

    ValueDefinition random(String value, Integer count) {
        registerPredicateValue(new TypePredicate(type: String), org.prismus.scrambler.value.Random.of(value, count))
        return this
    }

    ValueDefinition random(String value, Integer count, boolean includeLetters) {
        ValueCategory.checkNullValue(value)
        registerPredicateValue(new TypePredicate(type: String), org.prismus.scrambler.value.Random.of(value, count, includeLetters))
        return this
    }

    ValueDefinition random(String value, Integer count, boolean includeLetters, boolean includeNumbers) {
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

    ValueDefinition parent(String propertyName) {
        registerPredicateValue(ValueCategory.createPropertyPredicate(propertyName), new ParentValue())
        return this
    }

    ValueDefinition parent(String propertyName, String parentPredicate) {
        parent(ValueCategory.createPropertyPredicate(propertyName), ValueCategory.createPropertyPredicate(parentPredicate))
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
        registerPredicateValue((ValuePredicate)null, value)
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
        registerPredicateValue(valuePredicate, new ParentValue(predicate: parentPredicate))
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
        registerPredicateValue((ValuePredicate)predicate, new ParentValue())
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
        registerPredicateValue((ValuePredicate)predicate, new ParentValue(predicate: parentPredicate))
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
                of((String)key, value)
            } else if(Class.isInstance(key)) {
                of((Class)key, value)
            } else if(ValuePredicate.isInstance(key)) {
                of((ValuePredicate)key, value)
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
                of((String)key, new Constant(value))
            } else if(Class.isInstance(key)) {
                of((Class)key, new Constant(value))
            } else if(ValuePredicate.isInstance(key)) {
                of((ValuePredicate)key, new Constant(value))
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
