package org.prismus.scrambler.builder

import org.prismus.scrambler.Value
import org.prismus.scrambler.property.Constant
import org.prismus.scrambler.property.Incremental
import org.prismus.scrambler.property.ValueCollection

/**
 * todo: add description
 *
 * @author Serge Pruteanu
 */
// todo add ValueMap
// todo add randomOf for array type (object array and primitive one)
// todo add ValueArray for array type (object array and primitive one)
class ValueDefinition extends Script {
    InstanceValue instanceValue

    Map<ValuePredicate, Value> propertyValueMap = [:]
    Map<ValuePredicate, Value> typeValueMap = [:]

    ValueDefinition parent

    protected List<InstanceValue> instanceValues = []

    @Override
    Object run() {
        return this
    }

    void process() {
        for (final value : instanceValues) {
            value.process()
        }
    }

    protected static void checkNullValue(Object value) {
        if (value == null) {
            throw new IllegalArgumentException("Value can't be null")
        }
    }

    protected static void checkEmpty(String value) {
        if (!value) {
            throw new IllegalArgumentException("Value can't be null or empty")
        }
    }

    protected static void checkEmptyCollection(Collection values) {
        if (values.size() == 0) {
            throw new IllegalArgumentException("Values collection can't be empty")
        }
    }

    protected static void checkNullValue(Object minimum, Object maximum) {
        if (minimum == null && maximum == null) {
            throw new IllegalArgumentException('Either minimum or maximum should be not null')
        }
    }

    protected static ValuePredicate createPropertyPredicate(String propertyWildcard) {
        checkEmpty(propertyWildcard)
//        propertyWildcard.matches()
        return new PropertyPredicate(propertyWildcard)
    }

    protected static Number getNotNullValue(Number minimum, Number maximum) {
        Object value = minimum
        if (value == null) {
            value = maximum
        }
        return value
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
            checkNullValue(value.predicate)
            registerPredicateValue(value.predicate, (Value)value)
        }
    }

    ValueDefinition constant(Object value) {
        checkNullValue(value)
        registerPredicateValue(new TypePredicate(type: value.class), new Constant(value))
        return this
    }

    ValueDefinition constant(ValuePredicate propertyPredicate, Object value) {
        checkNullValue(value)
        registerPredicateValue(propertyPredicate, new Constant(value))
        return this
    }

    ValueDefinition incremental(Number value) {
        checkNullValue(value)
        registerPredicateValue(new TypePredicate(type: value.class), Incremental.of(value))
        return this
    }

    ValueDefinition incremental(ValuePredicate propertyPredicate, Number value) {
        checkNullValue(value)
        registerPredicateValue(propertyPredicate, Incremental.of(value))
        return this
    }

    ValueDefinition incremental(String value) {
        checkNullValue(value)
        registerPredicateValue(new TypePredicate(type: value.class), Incremental.of(value))
        return this
    }

    ValueDefinition incremental(ValuePredicate propertyPredicate, String value) {
        checkNullValue(value)
        registerPredicateValue(propertyPredicate, Incremental.of(value))
        return this
    }

    ValueDefinition incremental(Date value) {
        checkNullValue(value)
        registerPredicateValue(new TypePredicate(type: value.class), Incremental.of(value))
        return this
    }

    ValueDefinition incremental(ValuePredicate propertyPredicate, Date value) {
        checkNullValue(value)
        registerPredicateValue(propertyPredicate, Incremental.of(value))
        return this
    }

    ValueDefinition incremental(Number value, Number step) {
        checkNullValue(value)
        registerPredicateValue(new TypePredicate(type: value.class), Incremental.of(value, step))
        return this
    }

    ValueDefinition incremental(ValuePredicate propertyPredicate, Number value, Number step) {
        checkNullValue(value)
        registerPredicateValue(propertyPredicate, Incremental.of(value, step))
        return this
    }

    ValueDefinition incremental(String value, int index) {
        checkNullValue(value)
        registerPredicateValue(new TypePredicate(type: value.class), Incremental.of(value, index))
        return this
    }

    ValueDefinition incremental(ValuePredicate propertyPredicate, String value, int index) {
        checkNullValue(value)
        registerPredicateValue(propertyPredicate, Incremental.of(value, index))
        return this
    }

    ValueDefinition incremental(String value, String pattern) {
        checkNullValue(value)
        registerPredicateValue(new TypePredicate(type: value.class), Incremental.of(value, pattern))
        return this
    }

    ValueDefinition incremental(ValuePredicate propertyPredicate, String value, String pattern) {
        checkNullValue(value)
        registerPredicateValue(propertyPredicate, Incremental.of(value, pattern))
        return this
    }

    ValueDefinition incremental(Date value, int step) {
        checkNullValue(value)
        registerPredicateValue(new TypePredicate(type: value.class), Incremental.of(value, step))
        return this
    }

    ValueDefinition incremental(ValuePredicate propertyPredicate, Date value, int step) {
        checkNullValue(value)
        registerPredicateValue(propertyPredicate, Incremental.of(value, step))
        return this
    }

    ValueDefinition incremental(Date value, int step, int calendarField) {
        checkNullValue(value)
        registerPredicateValue(new TypePredicate(type: value.class), Incremental.of(value, step, calendarField))
        return this
    }

    ValueDefinition incremental(ValuePredicate propertyPredicate, Date value, int step, int calendarField) {
        checkNullValue(value)
        registerPredicateValue(propertyPredicate, Incremental.of(value, step, calendarField))
        return this
    }

    ValueDefinition incremental(String value, String pattern, Integer index) {
        checkNullValue(value)
        registerPredicateValue(new TypePredicate(type: String), Incremental.of(value, pattern, index))
        return this
    }

    ValueDefinition incremental(ValuePredicate propertyPredicate, String value, String pattern, Integer index) {
        checkNullValue(value)
        registerPredicateValue(propertyPredicate, Incremental.of(value, pattern, index))
        return this
    }

    ValueDefinition random(Number value) {
        checkNullValue(value)
        registerPredicateValue(new TypePredicate(type: value.class), org.prismus.scrambler.property.Random.of(value))
        return this
    }

    ValueDefinition random(ValuePredicate propertyPredicate, Number value) {
        checkNullValue(value)
        registerPredicateValue(propertyPredicate, org.prismus.scrambler.property.Random.of(value))
        return this
    }

    ValueDefinition random(Date value) {
        checkNullValue(value)
        registerPredicateValue(new TypePredicate(type: value.class), org.prismus.scrambler.property.Random.of(value))
        return this
    }

    ValueDefinition random(ValuePredicate propertyPredicate, Date value) {
        checkNullValue(value)
        registerPredicateValue(propertyPredicate, org.prismus.scrambler.property.Random.of(value))
        return this
    }

    ValueDefinition random(String value) {
        checkNullValue(value)
        registerPredicateValue(new TypePredicate(type: String), org.prismus.scrambler.property.Random.of(value))
        return this
    }

    ValueDefinition random(ValuePredicate propertyPredicate, String value) {
        checkNullValue(value)
        registerPredicateValue(propertyPredicate, org.prismus.scrambler.property.Random.of(value))
        return this
    }

    ValueDefinition random(Number minimum, Number maximum) {
        checkNullValue(minimum, maximum)
        final value = getNotNullValue(minimum, maximum)
        registerPredicateValue(new TypePredicate(type: value.class), org.prismus.scrambler.property.Random.of(minimum, maximum))
        return this
    }

    ValueDefinition random(ValuePredicate propertyPredicate, Number minimum, Number maximum) {
        checkNullValue(minimum, maximum)
        registerPredicateValue(propertyPredicate, org.prismus.scrambler.property.Random.of(minimum, maximum))
        return this
    }

    ValueDefinition random(Date minimum, Date maximum) {
        checkNullValue(minimum, maximum)
        registerPredicateValue(new TypePredicate(type: Date), org.prismus.scrambler.property.Random.of(minimum, maximum))
        return this
    }

    ValueDefinition random(ValuePredicate propertyPredicate, Date minimum, Date maximum) {
        checkNullValue(minimum, maximum)
        registerPredicateValue(propertyPredicate, org.prismus.scrambler.property.Random.of(minimum, maximum))
        return this
    }

    ValueDefinition random(Number value, Number minimum, Number maximum) {
        checkNullValue(value)
        registerPredicateValue(new TypePredicate(type: value.class), org.prismus.scrambler.property.Random.of(value, minimum, maximum))
        return this
    }

    ValueDefinition random(ValuePredicate propertyPredicate, Number value, Number minimum, Number maximum) {
        checkNullValue(value)
        registerPredicateValue(propertyPredicate, org.prismus.scrambler.property.Random.of(value, minimum, maximum))
        return this
    }

    ValueDefinition random(Date value, Date minimum, Date maximum) {
        checkNullValue(value)
        registerPredicateValue(new TypePredicate(type: value.class), org.prismus.scrambler.property.Random.of(value, minimum, maximum))
        return this
    }

    ValueDefinition random(ValuePredicate propertyPredicate, Date value, Date minimum, Date maximum) {
        checkNullValue(value)
        registerPredicateValue(propertyPredicate, org.prismus.scrambler.property.Random.of(value, minimum, maximum))
        return this
    }

    ValueDefinition randomOf(Collection values) {
        checkNullValue(values)
        checkEmptyCollection(values)

        final value = values.iterator().next()
        registerPredicateValue(new TypePredicate(type: value.class), org.prismus.scrambler.property.Random.randomOf(values))
        return this
    }

    ValueDefinition randomOf(ValuePredicate propertyPredicate, Collection values) {
        checkNullValue(values)
        checkEmptyCollection(values)

        registerPredicateValue(propertyPredicate, org.prismus.scrambler.property.Random.randomOf(values))
        return this
    }

    ValueDefinition random(String value, Integer count) {
        registerPredicateValue(new TypePredicate(type: String), org.prismus.scrambler.property.Random.of(value, count))
        return this
    }

    ValueDefinition random(ValuePredicate propertyPredicate, String value, Integer count) {
        registerPredicateValue(propertyPredicate, org.prismus.scrambler.property.Random.of(value, count))
        return this
    }

    ValueDefinition random(String value, Integer count, boolean includeLetters) {
        checkNullValue(value)
        registerPredicateValue(new TypePredicate(type: String), org.prismus.scrambler.property.Random.of(value, count, includeLetters))
        return this
    }

    ValueDefinition random(ValuePredicate propertyPredicate, String value, Integer count, boolean includeLetters) {
        checkNullValue(value)
        registerPredicateValue(propertyPredicate, org.prismus.scrambler.property.Random.of(value, count, includeLetters))
        return this
    }

    ValueDefinition random(String value, Integer count, boolean includeLetters, boolean includeNumbers) {
        checkNullValue(value)
        registerPredicateValue(new TypePredicate(type: String), org.prismus.scrambler.property.Random.of(value, count, includeLetters, includeNumbers))
        return this
    }

    ValueDefinition random(ValuePredicate propertyPredicate, String value, Integer count, boolean includeLetters, boolean includeNumbers) {
        checkNullValue(value)
        registerPredicateValue(propertyPredicate, org.prismus.scrambler.property.Random.of(value, count, includeLetters, includeNumbers))
        return this
    }

    ValueDefinition random(Value value, Collection collection, int count = 0) {
        checkNullValue(value)
        checkNullValue(collection)
        registerPredicateValue(new TypePredicate(type: Collection), new ValueCollection(collection, count, value))
        return this
    }

    ValueDefinition random(ValuePredicate propertyPredicate, Value value, Collection collection, int count = 0) {
        checkNullValue(value)
        checkNullValue(collection)
        registerPredicateValue(propertyPredicate, new ValueCollection(collection, count, value))
        return this
    }

    ValueDefinition parent(Class type) {
        checkNullValue(type)
        final ValuePredicate predicate = new TypePredicate(type: type)
        registerPredicateValue((ValuePredicate)predicate, new ParentValue())
        return this
    }

    ValueDefinition parent(Class type, String parentPredicate) {
        parent(type, createPropertyPredicate(parentPredicate))
        return this
    }

    ValueDefinition parent(Class type, Class parentPredicate) {
        checkNullValue(parentPredicate)
        parent(type, new TypePredicate(type: parentPredicate))
        return this
    }

    ValueDefinition parent(Class type, ValuePredicate parentPredicate) {
        checkNullValue(type)
        final ValuePredicate predicate = new TypePredicate(type: type)
        registerPredicateValue((ValuePredicate)predicate, new ParentValue(predicate: parentPredicate))
        return this
    }

    ValueDefinition parent(String propertyName) {
        registerPredicateValue(createPropertyPredicate(propertyName), new ParentValue())
        return this
    }

    ValueDefinition parent(String propertyName, String parentPredicate) {
        parent(createPropertyPredicate(propertyName), createPropertyPredicate(parentPredicate))
        return this
    }

    ValueDefinition parent(String propertyName, Class parentPredicate) {
        checkNullValue(parentPredicate)
        parent(createPropertyPredicate(propertyName), new TypePredicate(type: parentPredicate))
        return this
    }

    ValueDefinition parent(String propertyName, ValuePredicate parentPredicate) {
        checkNullValue(parentPredicate)
        registerPredicateValue(createPropertyPredicate(propertyName), new ParentValue(predicate: parentPredicate))
        return this
    }

    ValueDefinition parent(ValuePredicate valuePredicate, ValuePredicate parentPredicate) {
        checkNullValue(valuePredicate)
        registerPredicateValue(valuePredicate, new ParentValue(predicate: parentPredicate))
        return this
    }

    ValueDefinition of(ValuePredicate valuePredicate, Value value) {
        checkNullValue(valuePredicate)
        checkNullValue(value)
        registerPredicateValue(valuePredicate, value)
        return this
    }

    ValueDefinition of(Class type, Value value) {
        checkNullValue(type)
        checkNullValue(value)
        registerPredicateValue(new TypePredicate(type: type), value)
        return this
    }

    ValueDefinition of(String propertyWildcard, Value value) {
        checkNullValue(value)
        registerPredicateValue(createPropertyPredicate(propertyWildcard), value)
        return this
    }

    ValueDefinition of(Value value) {
        checkNullValue(value)
        final value1 = value.value
        checkNullValue(value1)
        registerPredicateValue(new TypePredicate(value1.class), value)
        return this
    }

    ValueDefinition of(InstanceValue value) {
        checkNullValue(value)
        registerPredicateValue((ValuePredicate)null, value)
        return this
    }

    ValueDefinition of(ValuePredicate valuePredicate, Object value) {
        checkNullValue(valuePredicate)
        registerPredicateValue(valuePredicate, new Constant(value))
        return this
    }

    ValueDefinition of(Class type, Object value) {
        checkNullValue(type)
        registerPredicateValue(new TypePredicate(type: type), new Constant(value))
        return this
    }

    ValueDefinition of(String propertyWildcard, Object value) {
        registerPredicateValue(createPropertyPredicate(propertyWildcard), new Constant(value))
        return this
    }

    ValueDefinition of(Object value) {
        checkNullValue(value)
        registerPredicateValue(new TypePredicate(value.class), new Constant(value))
        return this
    }

    static {

        Object.metaClass {
            constant { ->
                return new Constant(delegate)
            }
        }

        Number.metaClass {
            incremental { ->
                return Incremental.of((Number) delegate)
            }

            incremental { Number step ->
                return Incremental.of((Number) delegate, step)
            }

            random { ->
                return org.prismus.scrambler.property.Random.of((Number) delegate)
            }

            random { Number minimum, Number maximum ->
                return org.prismus.scrambler.property.Random.of((Number) delegate, minimum, maximum)
            }
        }

        Date.metaClass {
            incremental { ->
                return Incremental.of((Date) delegate)
            }

            incremental { Integer step ->
                return Incremental.of((Date) delegate, step)
            }

            incremental { int step, int calendarField ->
                return Incremental.of((Date) delegate, step, calendarField)
            }

            random { ->
                return org.prismus.scrambler.property.Random.of((Date) delegate)
            }

            random { Date minimum, Date maximum ->
                return org.prismus.scrambler.property.Random.of((Date) delegate, minimum, maximum)
            }
        }

        String.metaClass {
            incremental { ->
                return Incremental.of((Date) delegate)
            }

            incremental { Integer index ->
                return Incremental.of((String) delegate, index)
            }

            incremental { String pattern ->
                return Incremental.of((String) delegate, pattern)
            }

            incremental { String pattern, Integer index ->
                return Incremental.of((String) delegate, pattern, index)
            }

            random { ->
                return org.prismus.scrambler.property.Random.of((String) delegate)
            }

            random { Integer count ->
                return org.prismus.scrambler.property.Random.of((String) delegate, count)
            }

            random { ->
                return org.prismus.scrambler.property.Random.of((String) delegate)
            }

            random { Integer count, boolean includeLetters ->
                return org.prismus.scrambler.property.Random.of((String) delegate, count, includeLetters)
            }

            random { Integer count, boolean includeLetters, boolean includeNumbers ->
                return org.prismus.scrambler.property.Random.of((String) delegate, count, includeLetters, includeNumbers)
            }
        }

        Collection.metaClass {
            random { Value val ->
                checkNullValue(val)
                return new ValueCollection((Collection) delegate, val)
            }

            random { Value val, int count ->
                checkNullValue(val)
                return new ValueCollection((Collection) delegate, count, val)
            }

            randomOf { ->
                final collection = (Collection) delegate
                checkEmptyCollection(collection)
                return org.prismus.scrambler.property.Random.randomOf(collection)
            }
        }

        Class.metaClass {
            of { Closure defCl ->
                return new InstanceValue(
                        instanceType: (Class)delegate,
                        predicate: new TypePredicate(type: (Class)delegate),
                        definitionClosure: defCl
                )
            }

            of { String propertyName, Closure defCl ->
                return new InstanceValue(
                        instanceType: (Class)delegate,
                        predicate: createPropertyPredicate(propertyName),
                        definitionClosure: defCl
                )
            }

            of { def constructorArgs, Closure defCl ->
                return new InstanceValue(
                        instanceType: (Class)delegate,
                        constructorArguments: constructorArgs,
                        predicate: new TypePredicate(type: (Class)delegate),
                        definitionClosure: defCl
                )
            }

            of { String propertyName, def constructorArgs, Closure defCl ->
                return new InstanceValue(
                        instanceType: (Class)delegate,
                        constructorArguments: constructorArgs,
                        predicate: createPropertyPredicate(propertyName),
                        definitionClosure: defCl
                )
            }

            constant { Map props ->
                throw new UnsupportedOperationException("The constant method is not supported for given implementation")
            }

            incremental { Map props ->
                throw new UnsupportedOperationException("The incremental method is not supported for given implementation")
            }

            random { Map props ->
                throw new UnsupportedOperationException("The random method is not supported for given implementation")
            }

        }

    }

}
