package org.prismus.scrambler.value

import org.apache.commons.lang.ArrayUtils
import org.prismus.scrambler.Value

import java.lang.reflect.Array

/**
 * @author Serge Pruteanu
 */
class ValueArray extends Constant {
    Integer count
    Value value
    Boolean randomCount
    boolean primitiveArray
    Class valueType

    ValueArray() {
    }

    ValueArray(def array, Value value) {
        this(array, null, value)
    }

    ValueArray(def array, Integer count, Value value) {
        this(array, count, value, null)
    }

    ValueArray(def array, Integer count, Value value1, Boolean randomCount) {
        super(array)
        this.count = count
        this.value = value1
        this.randomCount = randomCount
    }

    ValueArray asType(Class valueType) {
        this.valueType = valueType
        if (valueType != null) {
            if (!primitiveArray) {
                primitiveArray = valueType.isPrimitive()
            }
        }
        return this
    }

    ValueArray asPrimitive() {
        primitiveArray = true
        return this
    }

    ValueArray asPrimitive(Boolean primitiveArray) {
        this.primitiveArray = primitiveArray != null && primitiveArray
        return this
    }

    void setValue(Value value) {
        this.value = value
    }

    @Override
    Object next() {
        def value = super.next()
        final Value valueInstance = this.value
        validateArguments(value, valueInstance)
        int count = this.count != null ? this.count : 0
        if (count == 0) {
            count = 20
        }
        if (randomCount != null && randomCount) {
            count = new RandomInteger(count).between(0, count).next()
        }
        checkCreate(value, count)
        for (int i = 0; i < count; i++) {
            value[i] = valueInstance.next()
        }

        if (primitiveArray) {
            value = ArrayUtils.toPrimitive(value)
        }
        setValue(value)
        return value
    }

    @SuppressWarnings("unchecked")
    Object checkCreate(def array, int count) {
        Class type = valueType
        if (type == null) {
            if (array.length != count || (array.length > 0 && array[0] != null)) {
                type = array.getClass()
            } else {
                return array
            }
        }
        array = Array.newInstance(type, count)
        return array
    }

    void validateArguments(def array, Value property) {
        if (array == null) {
            if (valueType == null) {
                throw new IllegalArgumentException("Array instance or array type should not be null")
            }
        }
        if (property == null) {
            throw new IllegalArgumentException("Collection/property instances should not be null")
        }
    }

}
