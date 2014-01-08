package org.prismus.scrambler.builder

import groovy.transform.CompileStatic
import org.prismus.scrambler.Value
import org.prismus.scrambler.property.Constant
import org.prismus.scrambler.property.Incremental
import org.prismus.scrambler.property.ValueCollection

/**
 * todo: add description
 *
 * @author Serge Pruteanu
 */
@CompileStatic
class ValueDefinition extends Script {
    Map<ValuePredicate, Value> propertyValueMap = [:]
    Map<ValuePredicate, Value> typeValueMap = [:]

    @Override
    Object run() {
        return this
    }

//    def methodMissing(String name, def args) {
////        Util.createInstance(Class.forName(name), args as Object[])
////        definitionMap.put(name, args)
//        return this
//    }

    protected void checkNullValue(value) {
        if (value == null) {
            throw new IllegalArgumentException("Value can't be null")
        }
    }

    protected void checkEmptyCollection(Collection values) {
        if (values.size() == 0) {
            throw new IllegalArgumentException("Values collection can't be empty")
        }
    }

    Value constant(Object value) {
        checkNullValue(value)
        final val = new Constant(value)
        typeValueMap.put(new TypePredicate(type: value.class), val)
        return val
    }

    Value incremental(Object value) {
        checkNullValue(value)
        final val = Incremental.of(value)
        typeValueMap.put(new TypePredicate(type: value.class), val)
        return val
    }

    Value incremental(Date value) {
        checkNullValue(value)
        final val = Incremental.of(value)
        typeValueMap.put(new TypePredicate(type: value.class), val)
        return val
    }

    Value incremental(Object value, Number step) {
        checkNullValue(value)
        final val = Incremental.of(value, step)
        typeValueMap.put(new TypePredicate(type: value.class), val)
        return val
    }

    Value incremental(String value, String pattern) {
        checkNullValue(value)
        final val = Incremental.of(value, pattern)
        typeValueMap.put(new TypePredicate(type: value.class), val)
        return val
    }

    Value incremental(Date value, int step) {
        checkNullValue(value)
        final val = Incremental.of(value, step)
        typeValueMap.put(new TypePredicate(type: value.class), val)
        return val
    }

    Value incremental(Date value, int step, int calendarField) {
        checkNullValue(value)
        final val = Incremental.of(value, step, calendarField)
        typeValueMap.put(new TypePredicate(type: value.class), val)
        return val
    }

    Value incremental(String value, String pattern, Integer index) {
        checkNullValue(value)
        final val = Incremental.of(value, pattern, index)
        typeValueMap.put(new TypePredicate(type: String), val)
        return val
    }

    Value random(Object value) {
        checkNullValue(value)
        final val = org.prismus.scrambler.property.Random.of(value)
        typeValueMap.put(new TypePredicate(type: value.class), val)
        return val
    }

    Value random(Object minimum, Object maximum) {
        if (minimum == null && maximum == null) {
            throw new IllegalArgumentException('Either minimum or maximum should be not null')
        }
        Object value = minimum
        if (value == null) {
            value = maximum
        }
        final val = org.prismus.scrambler.property.Random.of(minimum, maximum)
        typeValueMap.put(new TypePredicate(type: value.class), val)
        return val
    }

    Value random(Object value, Object minimum, Object maximum) {
        checkNullValue(value)
        final val = org.prismus.scrambler.property.Random.of(minimum, maximum)
        typeValueMap.put(new TypePredicate(type: value.class), val)
        return val
    }

    Value random(Collection values) {
        checkNullValue(values)
        checkEmptyCollection(values)

        final value = values.iterator().next()
        final val = org.prismus.scrambler.property.Random.of(values)
        typeValueMap.put(new TypePredicate(type: value.class), val)
        return val
    }

    Value random(String value, Integer count) {
        final val = org.prismus.scrambler.property.Random.of(value, count)
        typeValueMap.put(new TypePredicate(type: String), val)
        return val
    }

    Value random(String value) {
        checkNullValue(value)
        final val = org.prismus.scrambler.property.Random.of(value)
        typeValueMap.put(new TypePredicate(type: String), val)
        return val
    }

    Value random(String value, Integer count, boolean includeLetters) {
        checkNullValue(value)
        final val = org.prismus.scrambler.property.Random.of(value, count, includeLetters)
        typeValueMap.put(new TypePredicate(type: String), val)
        return val
    }

    Value random(String value, Integer count, boolean includeLetters, boolean includeNumbers) {
        checkNullValue(value)
        final val = org.prismus.scrambler.property.Random.of(value, count, includeLetters, includeNumbers)
        typeValueMap.put(new TypePredicate(type: String), val)
        return val
    }

    Value collection(Value value, Collection collection, int count = 0) {
        checkNullValue(value)
        checkNullValue(collection)
        final val = new ValueCollection(collection, count, value)
        typeValueMap.put(new TypePredicate(type: Collection), val)
        return val
    }

    Value of(ValuePredicate valuePredicate, Value value) {
        checkNullValue(valuePredicate)
        checkNullValue(value)
        typeValueMap.put(valuePredicate, value)
        return value
    }

    ValueDefinition randomAll() {
        throw new UnsupportedOperationException("The randomAll method is not supported for given implementation")
    }

    ValueDefinition incrementalAll() {
        throw new UnsupportedOperationException("The incrementalAll method is not supported for given implementation")
    }

//    static {
//        String.class.metaClass
//    }
}
