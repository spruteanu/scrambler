package org.prismus.scrambler.builder

import org.prismus.scrambler.Value
import org.prismus.scrambler.property.Constant
import org.prismus.scrambler.property.Incremental
import org.prismus.scrambler.property.ValueCollection
import org.prismus.scrambler.property.ValueMap

/**
 * todo: add description
 *
 * @author Serge Pruteanu
 */
class ValueCategory {

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
        return new PropertyPredicate(propertyWildcard)
    }

    protected static Number getNotNullValue(Number minimum, Number maximum) {
        Object value = minimum
        if (value == null) {
            value = maximum
        }
        return value
    }

    static void decorateMetaClasses() {
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
            of { Value val ->
                checkNullValue(val)
                return new ValueCollection((Collection) delegate, val)
            }

            of { Value val, int count ->
                checkNullValue(val)
                return new ValueCollection((Collection) delegate, count, val)
            }

            randomOf { ->
                final collection = (Collection) delegate
                checkEmptyCollection(collection)
                return org.prismus.scrambler.property.Random.randomOf(collection)
            }
        }

        Map.metaClass {

            of { Value entryKey, Value entryValue ->
                checkNullValue(entryKey)
                checkNullValue(entryValue)
                return new ValueMap((Map) delegate, entryKey, entryValue)
            }

            of { Value entryKey, Value entryValue, int count ->
                checkNullValue(entryKey)
                checkNullValue(entryValue)
                return new ValueMap((Map) delegate, count, entryKey, entryValue)
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

            constant { Map props -> // todo: implement me
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
