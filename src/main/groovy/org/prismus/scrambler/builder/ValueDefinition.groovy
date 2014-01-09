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
class ValueDefinition extends Script {
    Map<ValuePredicate, Value> propertyValueMap = [:]
    Map<ValuePredicate, Value> typeValueMap = [:]

    @Override
    Object run() {
        return this
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

    protected static RegexPredicate createPropertyPredicate(String propertyName) {
        return new RegexPredicate(propertyName)
    }

    protected static RegexPredicate createTypePredicate(String typeName) {
        return new TypePredicate(Class.forName(typeName))
    }

    protected static Number getNotNullValue(Number minimum, Number maximum) {
        Object value = minimum
        if (value == null) {
            value = maximum
        }
        return value
    }

    ValueDefinition constant(Object value) {
        checkNullValue(value)
        typeValueMap.put(new TypePredicate(type: value.class), new Constant(value))
        return this
    }

    ValueDefinition constant(ValuePredicate propertyPredicate, Object value) {
        checkNullValue(value)
        propertyValueMap.put(propertyPredicate, new Constant(value))
        return this
    }

    ValueDefinition incremental(Number value) {
        checkNullValue(value)
        typeValueMap.put(new TypePredicate(type: value.class), Incremental.of(value))
        return this
    }

    ValueDefinition incremental(ValuePredicate propertyPredicate, Number value) {
        checkNullValue(value)
        propertyValueMap.put(propertyPredicate, Incremental.of(value))
        return this
    }

    ValueDefinition incremental(String value) {
        checkNullValue(value)
        typeValueMap.put(new TypePredicate(type: value.class), Incremental.of(value))
        return this
    }

    ValueDefinition incremental(ValuePredicate propertyPredicate, String value) {
        checkNullValue(value)
        propertyValueMap.put(propertyPredicate, Incremental.of(value))
        return this
    }

    ValueDefinition incremental(Date value) {
        checkNullValue(value)
        typeValueMap.put(new TypePredicate(type: value.class), Incremental.of(value))
        return this
    }

    ValueDefinition incremental(ValuePredicate propertyPredicate, Date value) {
        checkNullValue(value)
        propertyValueMap.put(propertyPredicate, Incremental.of(value))
        return this
    }

    ValueDefinition incremental(Number value, Number step) {
        checkNullValue(value)
        typeValueMap.put(new TypePredicate(type: value.class), Incremental.of(value, step))
        return this
    }

    ValueDefinition incremental(ValuePredicate propertyPredicate, Number value, Number step) {
        checkNullValue(value)
        propertyValueMap.put(propertyPredicate, Incremental.of(value, step))
        return this
    }

    ValueDefinition incremental(String value, int index) {
        checkNullValue(value)
        typeValueMap.put(new TypePredicate(type: value.class), Incremental.of(value, index))
        return this
    }

    ValueDefinition incremental(ValuePredicate propertyPredicate, String value, int index) {
        checkNullValue(value)
        propertyValueMap.put(propertyPredicate, Incremental.of(value, index))
        return this
    }

    ValueDefinition incremental(String value, String pattern) {
        checkNullValue(value)
        typeValueMap.put(new TypePredicate(type: value.class), Incremental.of(value, pattern))
        return this
    }

    ValueDefinition incremental(ValuePredicate propertyPredicate, String value, String pattern) {
        checkNullValue(value)
        propertyValueMap.put(propertyPredicate, Incremental.of(value, pattern))
        return this
    }

    ValueDefinition incremental(Date value, int step) {
        checkNullValue(value)
        typeValueMap.put(new TypePredicate(type: value.class), Incremental.of(value, step))
        return this
    }

    ValueDefinition incremental(ValuePredicate propertyPredicate, Date value, int step) {
        checkNullValue(value)
        propertyValueMap.put(propertyPredicate, Incremental.of(value, step))
        return this
    }

    ValueDefinition incremental(Date value, int step, int calendarField) {
        checkNullValue(value)
        typeValueMap.put(new TypePredicate(type: value.class), Incremental.of(value, step, calendarField))
        return this
    }

    ValueDefinition incremental(ValuePredicate propertyPredicate, Date value, int step, int calendarField) {
        checkNullValue(value)
        propertyValueMap.put(propertyPredicate, Incremental.of(value, step, calendarField))
        return this
    }

    ValueDefinition incremental(String value, String pattern, Integer index) {
        checkNullValue(value)
        typeValueMap.put(new TypePredicate(type: String), Incremental.of(value, pattern, index))
        return this
    }

    ValueDefinition incremental(ValuePredicate propertyPredicate, String value, String pattern, Integer index) {
        checkNullValue(value)
        propertyValueMap.put(propertyPredicate, Incremental.of(value, pattern, index))
        return this
    }

    ValueDefinition random(Number value) {
        checkNullValue(value)
        typeValueMap.put(new TypePredicate(type: value.class), org.prismus.scrambler.property.Random.of(value))
        return this
    }

    ValueDefinition random(ValuePredicate propertyPredicate, Number value) {
        checkNullValue(value)
        propertyValueMap.put(propertyPredicate, org.prismus.scrambler.property.Random.of(value))
        return this
    }

    ValueDefinition random(Date value) {
        checkNullValue(value)
        typeValueMap.put(new TypePredicate(type: value.class), org.prismus.scrambler.property.Random.of(value))
        return this
    }

    ValueDefinition random(ValuePredicate propertyPredicate, Date value) {
        checkNullValue(value)
        propertyValueMap.put(propertyPredicate, org.prismus.scrambler.property.Random.of(value))
        return this
    }

    ValueDefinition random(String value) {
        checkNullValue(value)
        typeValueMap.put(new TypePredicate(type: String), org.prismus.scrambler.property.Random.of(value))
        return this
    }

    ValueDefinition random(ValuePredicate propertyPredicate, String value) {
        checkNullValue(value)
        propertyValueMap.put(propertyPredicate, org.prismus.scrambler.property.Random.of(value))
        return this
    }

    ValueDefinition random(Number minimum, Number maximum) {
        checkNullValue(minimum, maximum)
        final value = getNotNullValue(minimum, maximum)
        typeValueMap.put(new TypePredicate(type: value.class), org.prismus.scrambler.property.Random.of(minimum, maximum))
        return this
    }

    ValueDefinition random(ValuePredicate propertyPredicate, Number minimum, Number maximum) {
        checkNullValue(minimum, maximum)
        propertyValueMap.put(propertyPredicate, org.prismus.scrambler.property.Random.of(minimum, maximum))
        return this
    }

    ValueDefinition random(Date minimum, Date maximum) {
        checkNullValue(minimum, maximum)
        typeValueMap.put(new TypePredicate(type: Date), org.prismus.scrambler.property.Random.of(minimum, maximum))
        return this
    }

    ValueDefinition random(ValuePredicate propertyPredicate, Date minimum, Date maximum) {
        checkNullValue(minimum, maximum)
        propertyValueMap.put(propertyPredicate, org.prismus.scrambler.property.Random.of(minimum, maximum))
        return this
    }

    ValueDefinition random(Number value, Number minimum, Number maximum) {
        checkNullValue(value)
        typeValueMap.put(new TypePredicate(type: value.class), org.prismus.scrambler.property.Random.of(value, minimum, maximum))
        return this
    }

    ValueDefinition random(ValuePredicate propertyPredicate, Number value, Number minimum, Number maximum) {
        checkNullValue(value)
        propertyValueMap.put(propertyPredicate, org.prismus.scrambler.property.Random.of(value, minimum, maximum))
        return this
    }

    ValueDefinition random(Date value, Date minimum, Date maximum) {
        checkNullValue(value)
        typeValueMap.put(new TypePredicate(type: value.class), org.prismus.scrambler.property.Random.of(value, minimum, maximum))
        return this
    }

    ValueDefinition random(ValuePredicate propertyPredicate, Date value, Date minimum, Date maximum) {
        checkNullValue(value)
        propertyValueMap.put(propertyPredicate, org.prismus.scrambler.property.Random.of(value, minimum, maximum))
        return this
    }

    ValueDefinition randomOf(Collection values) {
        checkNullValue(values)
        checkEmptyCollection(values)

        final value = values.iterator().next()
        typeValueMap.put(new TypePredicate(type: value.class), org.prismus.scrambler.property.Random.randomOf(values))
        return this
    }

    ValueDefinition randomOf(ValuePredicate propertyPredicate, Collection values) {
        checkNullValue(values)
        checkEmptyCollection(values)

        propertyValueMap.put(propertyPredicate, org.prismus.scrambler.property.Random.randomOf(values))
        return this
    }

    ValueDefinition random(String value, Integer count) {
        typeValueMap.put(new TypePredicate(type: String), org.prismus.scrambler.property.Random.of(value, count))
        return this
    }

    ValueDefinition random(ValuePredicate propertyPredicate, String value, Integer count) {
        propertyValueMap.put(propertyPredicate, org.prismus.scrambler.property.Random.of(value, count))
        return this
    }

    ValueDefinition random(String value, Integer count, boolean includeLetters) {
        checkNullValue(value)
        typeValueMap.put(new TypePredicate(type: String), org.prismus.scrambler.property.Random.of(value, count, includeLetters))
        return this
    }

    ValueDefinition random(ValuePredicate propertyPredicate, String value, Integer count, boolean includeLetters) {
        checkNullValue(value)
        propertyValueMap.put(propertyPredicate, org.prismus.scrambler.property.Random.of(value, count, includeLetters))
        return this
    }

    ValueDefinition random(String value, Integer count, boolean includeLetters, boolean includeNumbers) {
        checkNullValue(value)
        typeValueMap.put(new TypePredicate(type: String), org.prismus.scrambler.property.Random.of(value, count, includeLetters, includeNumbers))
        return this
    }

    ValueDefinition random(ValuePredicate propertyPredicate, String value, Integer count, boolean includeLetters, boolean includeNumbers) {
        checkNullValue(value)
        propertyValueMap.put(propertyPredicate, org.prismus.scrambler.property.Random.of(value, count, includeLetters, includeNumbers))
        return this
    }

    ValueDefinition random(Value value, Collection collection, int count = 0) {
        checkNullValue(value)
        checkNullValue(collection)
        typeValueMap.put(new TypePredicate(type: Collection), new ValueCollection(collection, count, value))
        return this
    }

    ValueDefinition random(ValuePredicate propertyPredicate, Value value, Collection collection, int count = 0) {
        checkNullValue(value)
        checkNullValue(collection)
        propertyValueMap.put(propertyPredicate, new ValueCollection(collection, count, value))
        return this
    }

    ValueDefinition of(ValuePredicate valuePredicate, Value value) {
        checkNullValue(valuePredicate)
        checkNullValue(value)
        typeValueMap.put(valuePredicate, value)
        return this
    }

    ValueDefinition of(TypePredicate valuePredicate, Value value) {
        checkNullValue(valuePredicate)
        checkNullValue(value)
        typeValueMap.put(valuePredicate, value)
        return this
    }

    ValueDefinition of(RegexPredicate valuePredicate, Value value) {
        checkNullValue(valuePredicate)
        checkNullValue(value)
        propertyValueMap.put(valuePredicate, value)
        return this
    }

    ValueDefinition of(Class type, Value value) {
        checkNullValue(type)
        checkNullValue(value)
        typeValueMap.put(new TypePredicate(type: type), value)
        return this
    }

    ValueDefinition of(String propertyWildcard, Value value) {
        checkEmpty(propertyWildcard)
        checkNullValue(value)
        propertyValueMap.put(createPropertyPredicate(propertyWildcard), value)
        return this
    }

    ValueDefinition of(Value value) {
        checkNullValue(value)
        return of(new TypePredicate(value.value.class), value)
    }

    ValueDefinition randomAll() {
        throw new UnsupportedOperationException("The randomAll method is not supported for given implementation")
    }

    ValueDefinition incrementalAll() {
        throw new UnsupportedOperationException("The incrementalAll method is not supported for given implementation")
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
    }

}
