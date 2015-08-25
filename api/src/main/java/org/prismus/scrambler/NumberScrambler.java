/*
 * Data Scrambler, Data Generation API
 * Copyright (c) 2015, Sergiu Prutean. All rights reserved.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3.0 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library.
 */

package org.prismus.scrambler;

import org.prismus.scrambler.value.AbstractRandomRange;
import org.prismus.scrambler.value.ArrayValue;
import org.prismus.scrambler.value.Types;
import org.prismus.scrambler.value.Util;

/**
 * {@link Number} value methods, exposes all possible ways to generate {@link Number} objects
 *
 * @author Serge Pruteanu
 */
public class NumberScrambler {
    //------------------------------------------------------------------------------------------------------------------
    // Number methods
    //------------------------------------------------------------------------------------------------------------------
    @SuppressWarnings({"unchecked"})
    public static <T extends Number> Value<T> increment(T self) {
        return increment((Class<T>) self.getClass(), self, null);
    }

    @SuppressWarnings({"unchecked"})
    public static <T extends Number> Value<T> increment(T self, T step) {
        return increment((Class<T>) self.getClass(), self, step);
    }

    @SuppressWarnings({"unchecked"})
    public static <T extends Number> Value<T> random(T value) {
        return ObjectScrambler.random((Class<T>) value.getClass(), value);
    }

    @SuppressWarnings({"unchecked"})
    public static <T extends Number> Value<T> random(T minimum, T maximum) {
        return ObjectScrambler.random((Class<T>) minimum.getClass(), minimum, maximum);
    }

    @SuppressWarnings({"unchecked"})
    public static <T extends Number> Value<T> random(T val, T minimum, T maximum) {
        final Value<T> value = ObjectScrambler.random((Class<T>) val.getClass(), val);
        if (value instanceof AbstractRandomRange) {
            final AbstractRandomRange<Number> randomRangeValue = (AbstractRandomRange<Number>) value;
            randomRangeValue.between(minimum, maximum);
        } else {
            throw new UnsupportedOperationException(String.format(Types.NOT_SUPPORTED_RANGE_TYPE_MSG, val.getClass(), minimum, maximum));
        }
        return value;
    }

    @SuppressWarnings({"unchecked"})
    public static <T extends Number> Value<T> increment(Class<T> self, T defaultValue, T step) {
        if (Types.incrementTypeMap.containsKey(self)) {
            final Value value;
            if (self.isArray()) {
                value = ArrayScrambler.incrementArray(self, defaultValue, step, null);
            } else {
                value = (Value) Util.createInstance(Types.incrementTypeMap.get(self), new Object[]{defaultValue, step}, new Class[]{self, self});
            }
            return value;
        }
        throw new UnsupportedOperationException(String.format("The method is not supported for class type: %s, default value: %s", self, self));
    }

    @SuppressWarnings("unchecked")
    public static <T extends Number> Value<T> randomArray(Class self, T minimum, T maximum, Integer count) {
        Util.checkPositiveCount(count);

        Object defaultValue = minimum != null ? minimum : maximum != null ? maximum : null;
        if (self == null) {
            self = defaultValue != null ? defaultValue.getClass() : null;
        }
        if (self == null) {
            throw new IllegalArgumentException(String.format("Either minimum: %s or maximum: %s should be not null", minimum, maximum));
        }
        boolean primitive = false;
        Class<?> componentType;
        if (self.isArray()) {
            componentType = self.getComponentType();
            if (componentType.isPrimitive()) {
                primitive = true;
                componentType = Types.primitiveWrapperMap.get(componentType);
            }
        } else {
            componentType = self;
        }

        Value instance = (Value) Util.createInstance(Types.randomTypeMap.get(componentType),
                new Object[]{minimum, maximum}, new Class[]{componentType, componentType}
        );
        final Value<T> value;
        if (primitive) {
            value = (Value<T>) Util.createInstance(Types.randomTypeMap.get(self),
                    new Object[]{self.isInstance(defaultValue) ? defaultValue : null, count, instance},
                    new Class[]{self, Integer.class, Object.class}
            );
        } else {
            value = new ArrayValue(self, count, instance);
        }
        return value;
    }

}
