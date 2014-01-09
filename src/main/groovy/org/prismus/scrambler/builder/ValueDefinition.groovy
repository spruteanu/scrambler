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

//    def methodMissing(String name, def args) {
////        Util.createInstance(Class.forName(name), args as Object[])
////        definitionMap.put(name, args)
//        return this
//    }

    protected static void checkNullValue(value) {
        if (value == null) {
            throw new IllegalArgumentException("Value can't be null")
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

    ValueDefinition constant(Object value) {
        checkNullValue(value)
        final val = new Constant(value)
        typeValueMap.put(new TypePredicate(type: value.class), val)
        return this
    }

    ValueDefinition incremental(Number value) {
        checkNullValue(value)
        final val = Incremental.of(value)
        typeValueMap.put(new TypePredicate(type: value.class), val)
        return this
    }

    ValueDefinition incremental(String value) {
        checkNullValue(value)
        final val = Incremental.of(value)
        typeValueMap.put(new TypePredicate(type: value.class), val)
        return this
    }

    ValueDefinition incremental(Date value) {
        checkNullValue(value)
        final val = Incremental.of(value)
        typeValueMap.put(new TypePredicate(type: value.class), val)
        return this
    }

    ValueDefinition incremental(Number value, Number step) {
        checkNullValue(value)
        final val = Incremental.of(value, step)
        typeValueMap.put(new TypePredicate(type: value.class), val)
        return this
    }

    ValueDefinition incremental(String value, int index) {
        checkNullValue(value)
        final val = Incremental.of(value, index)
        typeValueMap.put(new TypePredicate(type: value.class), val)
        return this
    }

    ValueDefinition incremental(String value, String pattern) {
        checkNullValue(value)
        final val = Incremental.of(value, pattern)
        typeValueMap.put(new TypePredicate(type: value.class), val)
        return this
    }

    ValueDefinition incremental(Date value, int step) {
        checkNullValue(value)
        final val = Incremental.of(value, step)
        typeValueMap.put(new TypePredicate(type: value.class), val)
        return this
    }

    ValueDefinition incremental(Date value, int step, int calendarField) {
        checkNullValue(value)
        final val = Incremental.of(value, step, calendarField)
        typeValueMap.put(new TypePredicate(type: value.class), val)
        return this
    }

    ValueDefinition incremental(String value, String pattern, Integer index) {
        checkNullValue(value)
        final val = Incremental.of(value, pattern, index)
        typeValueMap.put(new TypePredicate(type: String), val)
        return this
    }

    ValueDefinition random(Number value) {
        checkNullValue(value)
        final val = org.prismus.scrambler.property.Random.of(value)
        typeValueMap.put(new TypePredicate(type: value.class), val)
        return this
    }

    ValueDefinition random(Date value) {
        checkNullValue(value)
        final val = org.prismus.scrambler.property.Random.of(value)
        typeValueMap.put(new TypePredicate(type: value.class), val)
        return this
    }

    ValueDefinition random(String value) {
        checkNullValue(value)
        final val = org.prismus.scrambler.property.Random.of(value)
        typeValueMap.put(new TypePredicate(type: String), val)
        return this
    }

    ValueDefinition random(Number minimum, Number maximum) {
        checkNullValue(minimum, maximum)
        Object value = minimum
        if (value == null) {
            value = maximum
        }
        final val = org.prismus.scrambler.property.Random.of(minimum, maximum)
        typeValueMap.put(new TypePredicate(type: value.class), val)
        return this
    }

    ValueDefinition random(Date minimum, Date maximum) {
        checkNullValue(minimum, maximum)
        final val = org.prismus.scrambler.property.Random.of(minimum, maximum)
        typeValueMap.put(new TypePredicate(type: Date), val)
        return this
    }

    ValueDefinition random(Number value, Number minimum, Number maximum) {
        checkNullValue(value)
        final val = org.prismus.scrambler.property.Random.of(value, minimum, maximum)
        typeValueMap.put(new TypePredicate(type: value.class), val)
        return this
    }

    ValueDefinition random(Date value, Date minimum, Date maximum) {
        checkNullValue(value)
        final val = org.prismus.scrambler.property.Random.of(value, minimum, maximum)
        typeValueMap.put(new TypePredicate(type: value.class), val)
        return this
    }

    ValueDefinition randomOf(Collection values) {
        checkNullValue(values)
        checkEmptyCollection(values)

        final value = values.iterator().next()
        final val = org.prismus.scrambler.property.Random.randomOf(values)
        typeValueMap.put(new TypePredicate(type: value.class), val)
        return this
    }

    ValueDefinition random(String value, Integer count) {
        final val = org.prismus.scrambler.property.Random.of(value, count)
        typeValueMap.put(new TypePredicate(type: String), val)
        return this
    }

    ValueDefinition random(String value, Integer count, boolean includeLetters) {
        checkNullValue(value)
        final val = org.prismus.scrambler.property.Random.of(value, count, includeLetters)
        typeValueMap.put(new TypePredicate(type: String), val)
        return this
    }

    ValueDefinition random(String value, Integer count, boolean includeLetters, boolean includeNumbers) {
        checkNullValue(value)
        final val = org.prismus.scrambler.property.Random.of(value, count, includeLetters, includeNumbers)
        typeValueMap.put(new TypePredicate(type: String), val)
        return this
    }

    ValueDefinition random(Value value, Collection collection, int count = 0) {
        checkNullValue(value)
        checkNullValue(collection)
        final val = new ValueCollection(collection, count, value)
        typeValueMap.put(new TypePredicate(type: Collection), val)
        return this
    }

    ValueDefinition of(ValuePredicate valuePredicate, Value value) {
        checkNullValue(valuePredicate)
        checkNullValue(value)
        typeValueMap.put(valuePredicate, value)
        return this
    }

    ValueDefinition of(ValueDefinitionEntry entry) {
        checkNullValue(entry)
        checkNullValue(entry.predicate)
        checkNullValue(entry.value)
        typeValueMap.put(entry.predicate, entry.value)
        return this
    }

    ValueDefinition randomAll() {
        throw new UnsupportedOperationException("The randomAll method is not supported for given implementation")
    }

    ValueDefinition incrementalAll() {
        throw new UnsupportedOperationException("The incrementalAll method is not supported for given implementation")
    }

    static {
        Object.metaClass.constant { ->
            return new ValueDefinitionEntry(predicate: new TypePredicate(type: delegate.class), value: new Constant(delegate))
        }

        Number.metaClass {
            incremental { ->
                return new ValueDefinitionEntry(predicate: new TypePredicate(type: delegate.class), value: Incremental.of(delegate))
            }

            incremental { Number step ->
                return new ValueDefinitionEntry(predicate: new TypePredicate(type: delegate.class), value: Incremental.of(delegate, step))
            }

            random { ->
                final val = org.prismus.scrambler.property.Random.of((Number)delegate)
                return new ValueDefinitionEntry(predicate: new TypePredicate(type: delegate.class), value: val)
            }

            random { Number minimum, Number maximum ->
                final val = org.prismus.scrambler.property.Random.of((Number)delegate, minimum, maximum)
                return new ValueDefinitionEntry(predicate: new TypePredicate(type: delegate.class), value: val)
            }
        }

        Date.metaClass {
            incremental { ->
                return new ValueDefinitionEntry(predicate: new TypePredicate(type: delegate.class), value: Incremental.of((Date) delegate))
            }

            incremental { Integer step ->
                return new ValueDefinitionEntry(predicate: new TypePredicate(type: delegate.class), value: Incremental.of((Date) delegate, step))
            }

            incremental { int step, int calendarField ->
                return new ValueDefinitionEntry(predicate: new TypePredicate(type: delegate.class), value: Incremental.of((Date) delegate, step, calendarField))
            }

            random { ->
                final val = org.prismus.scrambler.property.Random.of((Date)delegate)
                return new ValueDefinitionEntry(predicate: new TypePredicate(type: delegate.class), value: val)
            }

            random { Date minimum, Date maximum ->
                final val = org.prismus.scrambler.property.Random.of((Date)delegate, minimum, maximum)
                return new ValueDefinitionEntry(predicate: new TypePredicate(type: delegate.class), value: val)
            }
        }

        String.metaClass {
            incremental { ->
                return new ValueDefinitionEntry(predicate: new TypePredicate(type: String), value: Incremental.of((Date) delegate))
            }

            incremental { Integer index ->
                final val = Incremental.of((String) delegate, index)
                return new ValueDefinitionEntry(predicate: new TypePredicate(type: String), value: val)
            }

            incremental { String pattern ->
                final val = Incremental.of((String) delegate, pattern)
                return new ValueDefinitionEntry(predicate: new TypePredicate(type: String), value: val)
            }

            incremental { String pattern, Integer index ->
                final val = Incremental.of((String) delegate, pattern, index)
                return new ValueDefinitionEntry(predicate: new TypePredicate(type: String), value: val)
            }

            random { ->
                final val = org.prismus.scrambler.property.Random.of((String)delegate)
                return new ValueDefinitionEntry(predicate: new TypePredicate(type: String), value: val)
            }

            random { Integer count ->
                final val = org.prismus.scrambler.property.Random.of((String) delegate, count)
                return new ValueDefinitionEntry(predicate: new TypePredicate(type: String), value: val)
            }

            random { ->
                final val = org.prismus.scrambler.property.Random.of((String)delegate)
                return new ValueDefinitionEntry(predicate: new TypePredicate(type: String), value: val)
            }

            random { Integer count, boolean includeLetters ->
                final val = org.prismus.scrambler.property.Random.of((String)delegate, count, includeLetters)
                return new ValueDefinitionEntry(predicate: new TypePredicate(type: String), value: val)
            }

            random { Integer count, boolean includeLetters, boolean includeNumbers ->
                final val = org.prismus.scrambler.property.Random.of((String)delegate, count, includeLetters, includeNumbers)
                return new ValueDefinitionEntry(predicate: new TypePredicate(type: String), value: val)
            }
        }

        Collection.metaClass {

            random { Value val ->
                final collection = (Collection) delegate
                return new ValueDefinitionEntry(predicate: new TypePredicate(type: Collection), value: new ValueCollection(collection, val))
            }

            random { Value val, int count ->
                final collection = (Collection) delegate
                return new ValueDefinitionEntry(predicate: new TypePredicate(type: Collection), value: new ValueCollection(collection, count, val))
            }

            randomOf { ->
                final collection = (Collection) delegate
                checkEmptyCollection(collection)

                final val = org.prismus.scrambler.property.Random.randomOf(collection)
                return new ValueDefinitionEntry(predicate: new TypePredicate(type: Collection), value: new ValueCollection((Collection)delegate, val))
            }
        }

    }

}
