package org.prismus.scrambler.value

import org.apache.commons.lang.ArrayUtils
import org.prismus.scrambler.Value

import java.lang.reflect.Array

/**
 * @author Serge Pruteanu
 */
class ValueArray extends Constant {
    Integer count
    Value instance
    Boolean randomCount
    Boolean primitiveArray
    Class valueType

    ValueArray() {
    }

    ValueArray(def array, Value value) {
        this(array, null, value)
    }

    ValueArray(Class valueType, Value value) {
        this(valueType, null, value)
    }

    ValueArray(def array, Integer count, Value value) {
        this(array, count, value, null)
    }

    ValueArray(Class valueType, Integer count, Value value) {
        this(valueType, count, value, null)
    }

    ValueArray(def array, Integer count, Value value1, Boolean randomCount) {
        super(array)
        this.count = count
        this.instance = value1
        this.randomCount = randomCount
    }

    ValueArray(Class valueType, Integer count, Value value1, Boolean randomCount) {
        super(null)
        this.valueType = valueType
        this.count = count
        this.instance = value1
        this.randomCount = randomCount
    }

    ValueArray asType(Class valueType) {
        this.valueType = valueType.isArray() ? valueType.componentType : valueType
        if (!primitiveArray) {
            primitiveArray = valueType.isPrimitive()
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

    @Override
    Object next() {
        def value = super.next()
        validateArguments(value, instance)
        int count = this.count != null ? this.count : 0
        if (count == 0) {
            count = 20
        }
        if (randomCount != null && randomCount) {
            count = new RandomInteger(count).between(0, count).next()
        }
        value = checkCreate(value, count)
        for (int i = 0; i < count; i++) {
            value[i] = instance.next()
        }

        if (primitiveArray && !value.class.componentType.primitive) {
            // todo Serge: it is not optimal, faster would be to have separate value set directly into array for all coerced types
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
                type = array.class
            } else {
                return array
            }
        }
        if (type.isArray()) {
            type = type.componentType
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
            throw new IllegalArgumentException("Value instance should not be null")
        }
    }

}
