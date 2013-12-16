package org.prismus.scrambler.property

import groovy.transform.CompileStatic
import org.prismus.scrambler.Property

/**
 * @author Serge Pruteanu
 */
@CompileStatic
class Incremental extends Generic {
    private static Map<Class, Class<? extends Property>> propertyTypeMap /*=
            ImmutableMap.<Class, Class<? extends Property>>builder()
                    .put(Byte.class, IncrementalByte.class)
                    .put(Short.class, IncrementalShort.class)
                    .put(Double.class, IncrementalDouble.class)
                    .put(BigDecimal.class, IncrementalBigDecimal.class)
                    .put(Float.class, IncrementalFloat.class)
                    .put(Integer.class, IncrementalInteger.class)
                    .put(Long.class, IncrementalLong.class)
                    .put(String.class, IncrementalString.class)
                    .put(Date.class, IncrementalDate.class)
                    .put(java.sql.Date.class, IncrementalDate.class)
                    .put(Timestamp.class, IncrementalDate.class)
                    .build()*/

    private static Map<Class, Number> defaultTypeStep
    /* = ImmutableMap.<Class, Number>builder()
            .put(Byte.class, Integer.valueOf(1).byteValue())
            .put(Short.class, Integer.valueOf(1).shortValue())
            .put(Double.class, Integer.valueOf(1).doubleValue())
            .put(BigDecimal.class, BigDecimal.valueOf(1))
            .put(Float.class, Integer.valueOf(1).floatValue())
            .put(Integer.class, 1)
            .put(Long.class, Integer.valueOf(1).longValue())
            .put(String.class, 1)
            .put(Date.class, DateUtils.MILLIS_PER_DAY)
            .put(java.sql.Date.class, DateUtils.MILLIS_PER_DAY)
            .put(Timestamp.class, DateUtils.MILLIS_PER_DAY)
            .build()*/

    @Override
    Object value() {
        Object value = super.value()
        if (value == null) {
            throw new IllegalStateException("Value object can't be null")
        }
        final Class valueClassType
        if (value instanceof Class) {
            valueClassType = ((Class) value)
            value = null
        } else {
            valueClassType = value.getClass()
        }
        return of(getName(), valueClassType, value).value()
    }

    static <T> Property<T> of(String propertyName, T value) {
        return of(propertyName, (Class<T>) value.getClass(), value, null)
    }

    static <T> Property<T> of(String propertyName, T value, Number step) {
        return of(propertyName, (Class<T>) value.getClass(), value, step)
    }

    static <T> Property<T> of(String propertyName, Class<T> clazzType) {
        return of(propertyName, clazzType, null, null)
    }

    static <T> Property<T> of(String propertyName, Class<T> clazzType, T defaultValue) {
        return of(propertyName, clazzType, defaultValue, null)
    }

    static <T> Property<T> of(String propertyName, Class<T> clazzType, Number step) {
        return of(propertyName, clazzType, null, step)
    }

    static <T> Property<T> of(String propertyName,
                              Class<T> clazzType,
                              T defaultValue,
                              Number step) {
//        if (propertyTypeMap.containsKey(clazzType)) {
//            if (step == null) {
//                step = defaultTypeStep.get(clazzType)
//            }
//            return (Property<T>) Util.createInstance(
//                    propertyTypeMap.get(clazzType),
//                    new Object[]{propertyName, defaultValue, step},
//                    new Class[]{String.class, clazzType, clazzType}
//            )
//        }
        throw new UnsupportedOperationException(String.format("The of method is not supported for property: %s, class type: %s, default value: %s",
                propertyName, clazzType, defaultValue))
    }
}
