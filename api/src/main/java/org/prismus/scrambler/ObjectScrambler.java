/*
 * Data Scrambler, Data Generation API
 * Copyright (c) 2015, Sergiu Prutean. All rights reserved.
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
 *
 */

package org.prismus.scrambler;

import org.prismus.scrambler.value.*;

/**
 * Facade that exposes data generation for generic types, common to all {@link Object} derived
 *
 * @author Serge Pruteanu
 */
public class ObjectScrambler {
    //------------------------------------------------------------------------------------------------------------------
    // Object methods
    //------------------------------------------------------------------------------------------------------------------
    public static <T> Value<T> constant(T value) {
        return new Constant<T>(value);
    }

    public static <T> Value<T> random(Class<T> self) {
        return random(self, null);
    }

    @SuppressWarnings({"unchecked"})
    public static <T> Value<T> random(Class<T> self, T defaultValue) {
        if (Types.randomTypeMap.containsKey(self)) {
            if (self.isArray()) {
                return ArrayScrambler.randomArray(self, defaultValue, null);
            } else {
                if (self.isPrimitive()) {
                    return (Value) Util.createInstance(Types.randomTypeMap.get(self), null, null);
                } else {
                    return (Value) Util.createInstance(Types.randomTypeMap.get(self), new Object[]{defaultValue}, new Class[]{self});
                }
            }
        }
        throw new UnsupportedOperationException(String.format("The method is not supported for class type: %s, default value: %s", self, defaultValue));
    }

    public static <T> Value<T> random(Class<T> self, T minimum, T maximum) {
        final Value<T> value = random(self, minimum);
        if (value instanceof AbstractRandomRange) {
            final AbstractRandomRange<T> randomRangeValue = (AbstractRandomRange<T>) value;
            randomRangeValue.between(minimum, maximum);
        } else {
            throw new UnsupportedOperationException(String.format(Types.NOT_SUPPORTED_RANGE_TYPE_MSG, self, minimum, maximum));
        }
        return value;
    }

    //------------------------------------------------------------------------------------------------------------------
    // Boolean methods
    //------------------------------------------------------------------------------------------------------------------
    public static Value<Boolean> random(Boolean value) {
        return new RandomBoolean(value);
    }

}
