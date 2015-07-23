package org.prismus.scrambler.value;

import org.prismus.scrambler.Value;

/**
 * todo: add description
 *
 * @author Serge Pruteanu
 */
public class BooleanValue {
    public static Value<Boolean> random(Boolean value) {
        return new RandomBoolean(value);
    }

    @SuppressWarnings("unchecked") // todo Serge: split in 2-3 methods where arrays are defined explicitly
    public static Value random(Boolean value, Integer count, Class clazzType) {
        Util.checkPositiveCount(count);

        if (clazzType == null) {
            clazzType = value != null ? value.getClass() : null;
        }
        if (clazzType == null) {
            throw new IllegalArgumentException(String.format("Either classType: %s or value: %s should be not null", null, value));
        }
        boolean primitive = false;
        Class<?> componentType;
        if (clazzType.isArray()) {
            componentType = clazzType.getComponentType();
            if (componentType.isPrimitive()) {
                primitive = true;
                componentType = Types.primitiveWrapperMap.get(componentType);
            }
        } else {
            componentType = clazzType;
        }

        Value instance = (Value) Util.createInstance(
                Types.randomTypeMap.get(componentType),
                new Object[]{value},
                new Class[]{componentType}
        );
        final Value valueArray;
        if (primitive) {
            valueArray = (Value) Util.createInstance(
                    Types.randomTypeMap.get(clazzType),
                    new Object[]{clazzType.isInstance(value) ? value : null, count, instance},
                    new Class[]{clazzType, Integer.class, Object.class}
            );
        } else {
            valueArray = new ArrayValue(clazzType, count, instance);
        }
        return valueArray;
    }
}
