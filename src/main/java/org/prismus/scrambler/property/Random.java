package org.prismus.scrambler.property;

import org.prismus.scrambler.Property;

import java.util.*;

/**
 * @author Serge Pruteanu
 */
public class Random extends RandomRange {
    private static final String NOT_SUPPORTED_RANGE_TYPE_MSG = "Not supported range method for provided class type: %s, range of [%s, %s]";
    private static Map<Class, Class<? extends Property>> propertyTypeMap /*=
            ImmutableMap.<Class, Class<? extends Property>>builder()
                    .put(Byte.class, RandomByte.class)
                    .put(Short.class, RandomShort.class)
                    .put(Boolean.class, RandomBoolean.class)
                    .put(Double.class, RandomDouble.class)
                    .put(BigDecimal.class, RandomBigDecimal.class)
                    .put(Float.class, RandomFloat.class)
                    .put(Integer.class, RandomInteger.class)
                    .put(Long.class, RandomLong.class)
                    .put(String.class, RandomString.class)
                    .put(Date.class, RandomDate.class)
                    .put(java.sql.Date.class, RandomDate.class)
                    .put(Timestamp.class, RandomDate.class)
                    .build()*/;

    @Override
    @SuppressWarnings({"unchecked"})
    public Object value() {
        Object value = super.value();
        if (value == null) {
            throw new IllegalStateException("Value object can't be null");
        }
        final Class valueClassType;
        if (value instanceof Class) {
            valueClassType = ((Class) value);
            value = null;
        } else {
            valueClassType = value.getClass();
        }
        if (minimum != null || maximum != null) {
            value = of(getName(), valueClassType, minimum, maximum).value();
        } else {
            value = of(getName(), valueClassType, value).value();
        }
        return value;
    }

    @SuppressWarnings({"unchecked"})
    public static <T> Property<T> of(String propertyName, T value) {
        return of(propertyName, (Class<T>) value.getClass(), value);
    }

    @SuppressWarnings({"unchecked"})
    public static <T> Property<T> of(String propertyName, T minimum, T maximum) {
        return of(propertyName, (Class<T>) minimum.getClass(), minimum, maximum);
    }

    public static <T> Property<T> of(String propertyName, Class<T> clazzType) {
        return of(propertyName, clazzType, null);
    }

    @SuppressWarnings({"unchecked"})
    public static <T> Property<T> of(String propertyName, Class<T> clazzType, T minimum, T maximum) {
        final Property<T> property = of(propertyName, clazzType, null);
        if (property instanceof RandomRange) {
            final RandomRange<T> randomRangeValue = (RandomRange<T>) property;
            randomRangeValue.minimumBound(minimum).maximumBound(maximum);
        } else {
            throw new UnsupportedOperationException(String.format(NOT_SUPPORTED_RANGE_TYPE_MSG, clazzType, minimum, maximum));
        }
        return property;
    }

    @SuppressWarnings({"unchecked"})
    public static <T> Property<T> of(String propertyName, Class<T> clazzType, T defaultValue) {
//        if (propertyTypeMap.containsKey(clazzType)) {
//            return (Property) Util.createInstance(
//                    propertyTypeMap.get(clazzType),
//                    new Object[]{propertyName, defaultValue},
//                    new Class[]{String.class, clazzType}
//            );
//        }
        throw new UnsupportedOperationException(String.format("The of method is not supported for property: %s, class type: %s, default value: %s",
                propertyName, clazzType, defaultValue));
    }

    public static <T> Property<T> of(String propertyName, Collection<T> collection) {
        return new RandomListElement<T>(propertyName, new ArrayList<T>(collection));
    }

    public static <T> Property<T> of(String propertyName, List<T> values) {
        return new RandomListElement<T>(propertyName, values);
    }
}
