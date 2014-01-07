package org.prismus.scrambler.builder

import groovy.transform.CompileStatic
import org.prismus.scrambler.Value
import org.prismus.scrambler.property.Constant
import org.prismus.scrambler.property.Incremental
import org.prismus.scrambler.property.Util
import org.prismus.scrambler.property.ValueCollection

/**
 * todo: add description
 *
 * @author Serge Pruteanu
 */
@CompileStatic
class ValueDefinition extends Script {
    Map<ValuePredicate, Value> predicateValueMap = [:]

    @Override
    Object run() {
        return this
    }

    def methodMissing(String name, def args) {
        // todo Serge: implement dynamic method lookup
//        Util.createInstance(Class.forName(name), args as Object[])
//        definitionMap.put(name, args)
        return this
    }

    protected void checkNullValue(value) {
        if (value == null) {
            throw new IllegalArgumentException("Value can't be null")
        }
    }

    ValueDefinition constant(Object value) {
        checkNullValue(value)
        predicateValueMap.put(new TypePredicate(type: value.class), new Constant(value))
        return this
    }

    ValueDefinition incremental(Object value) {
        checkNullValue(value)
        predicateValueMap.put(new TypePredicate(type: value.class), Incremental.of(value))
        return this
    }

    ValueDefinition incremental(Date value) {
        checkNullValue(value)
        predicateValueMap.put(new TypePredicate(type: value.class), Incremental.of(value))
        return this
    }

    ValueDefinition incremental(Object value, Number step) {
        checkNullValue(value)
        predicateValueMap.put(new TypePredicate(type: value.class), Incremental.of(value, step))
        return this
    }

    ValueDefinition incremental(String value, String pattern) {
        checkNullValue(value)
        predicateValueMap.put(new TypePredicate(type: value.class), Incremental.of(value, pattern))
        return this
    }

    ValueDefinition incremental(Date value, int step) {
        checkNullValue(value)
        predicateValueMap.put(new TypePredicate(type: value.class), Incremental.of(value, step))
        return this
    }

    ValueDefinition incremental(Date value, int step, int calendarField) {
        checkNullValue(value)
        predicateValueMap.put(new TypePredicate(type: value.class), Incremental.of(value, step, calendarField))
        return this
    }

    ValueDefinition incremental(String value, String pattern, Integer index) {
        checkNullValue(value)
        predicateValueMap.put(new TypePredicate(type: String), Incremental.of(value, pattern, index))
        return this
    }

    ValueDefinition random(Object value) {
        checkNullValue(value)
        predicateValueMap.put(new TypePredicate(type: value.class), org.prismus.scrambler.property.Random.of(value))
        return this
    }

    ValueDefinition random(Object minimum, Object maximum) {
        if (minimum == null && maximum == null) {
            throw new IllegalArgumentException('Either minimum or maximum should be not null')
        }
        Object value = minimum
        if (value == null) {
            value = maximum
        }
        predicateValueMap.put(new TypePredicate(type: value.class), org.prismus.scrambler.property.Random.of(minimum, maximum))
        return this
    }

    ValueDefinition random(Object value, Object minimum, Object maximum) {
        checkNullValue(value)
        predicateValueMap.put(new TypePredicate(type: value.class), org.prismus.scrambler.property.Random.of(minimum, maximum))
        return this
    }

    ValueDefinition random(Collection values) {
        checkNullValue(values)
        checkEmptyCollection(values)

        final value = values.iterator().next()
        predicateValueMap.put(new TypePredicate(type: value.class), org.prismus.scrambler.property.Random.of(values))
        return this
    }

    protected void checkEmptyCollection(Collection values) {
        if (values.size() == 0) {
            throw new IllegalArgumentException("Values collection can't be empty")
        }
    }

    ValueDefinition random(String value, Integer count) {
        predicateValueMap.put(new TypePredicate(type: String), org.prismus.scrambler.property.Random.of(value, count))
        return this
    }

    ValueDefinition random(String value) {
        checkNullValue(value)
        predicateValueMap.put(new TypePredicate(type: String), org.prismus.scrambler.property.Random.of(value))
        return this
    }

    ValueDefinition random(String value, Integer count, boolean includeLetters) {
        checkNullValue(value)
        predicateValueMap.put(new TypePredicate(type: String), org.prismus.scrambler.property.Random.of(value, count, includeLetters))
        return this
    }

    ValueDefinition random(String value, Integer count, boolean includeLetters, boolean includeNumbers) {
        checkNullValue(value)
        predicateValueMap.put(new TypePredicate(type: String), org.prismus.scrambler.property.Random.of(value, count, includeLetters, includeNumbers))
        return this
    }

    ValueDefinition collection(Value value, Collection collection, int count = 0) {
        checkNullValue(value)
        checkNullValue(collection)
        predicateValueMap.put(new TypePredicate(type: Collection), new ValueCollection(collection, count, value))
        return this
    }

    ValueDefinition of(ValuePredicate valuePredicate, Value value) {
        checkNullValue(valuePredicate)
        checkNullValue(value)
        predicateValueMap.put(valuePredicate, value)
        return this
    }

    ValueDefinition randomAll() {
        throw new UnsupportedOperationException("The randomAll method is not supported for given implementation")
    }

    ValueDefinition incrementalAll() {
        throw new UnsupportedOperationException("The incrementalAll method is not supported for given implementation")
    }
}
