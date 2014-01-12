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

    static void registerValueMetaClasses() {
        Object.metaClass {
            constant { ->
                return new Constant(delegate)
            }
        }

        Number.metaClass {
            incremental { Number step = null ->
                return Incremental.of((Number) delegate, step)
            }

            random { Number minimum = null, Number maximum = null ->
                return org.prismus.scrambler.property.Random.of((Number) delegate, minimum, maximum)
            }
        }

        Date.metaClass {
            incremental { Integer step = null, Integer calendarField = null ->
                return Incremental.of((Date) delegate, step, calendarField)
            }

            random { Date minimum = null, Date maximum = null ->
                return org.prismus.scrambler.property.Random.of((Date) delegate, minimum, maximum)
            }
        }

        String.metaClass {
            incremental { String pattern = null, Integer index = null ->
                return Incremental.of((String) delegate, pattern, index)
            }

            random { Integer count = null, Boolean includeLetters = null, Boolean includeNumbers = null ->
                return org.prismus.scrambler.property.Random.of((String) delegate, count, includeLetters, includeNumbers)
            }
        }

        Collection.metaClass {
            of { Value val, Integer count = null, Boolean randomCount = null ->
                checkNullValue(val)
                return new ValueCollection((Collection) delegate, count, val, randomCount)
            }

            randomOf { ->
                final collection = (Collection) delegate
                checkEmptyCollection(collection)
                return org.prismus.scrambler.property.Random.randomOf(collection)
            }
        }

        Map.metaClass {
            of { Value entryKey, Value entryValue, Integer count = null, Boolean randomCount = null ->
                checkNullValue(entryKey)
                checkNullValue(entryValue)
                return new ValueMap((Map) delegate, entryKey, entryValue, count, randomCount)
            }
        }

        Class.metaClass {
            of { Closure defCl ->
                return new InstanceValue(
                        instanceType: (Class) delegate,
                        predicate: new TypePredicate(type: (Class) delegate),
                        definitionClosure: defCl
                )
            }

            of { Map<Object, Value> propertyValueMap, Closure defCl = null ->
                return new InstanceValue(
                        instanceType: (Class) delegate,
                        propertyValueMap: propertyValueMap,
                        predicate: new TypePredicate(type: (Class) delegate),
                        definitionClosure: defCl
                )
            }

            of { String propertyName, Closure defCl ->
                return new InstanceValue(
                        instanceType: (Class) delegate,
                        predicate: createPropertyPredicate(propertyName),
                        definitionClosure: defCl
                )
            }

            of { Collection constructorArgs, Closure defCl ->
                return new InstanceValue(
                        instanceType: (Class) delegate,
                        constructorArguments: constructorArgs,
                        predicate: new TypePredicate(type: (Class) delegate),
                        definitionClosure: defCl
                )
            }

            of { String propertyName, Collection constructorArgs, Closure defCl ->
                return new InstanceValue(
                        instanceType: (Class) delegate,
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
