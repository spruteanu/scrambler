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

import org.prismus.scrambler.data.*;

/**
 * Facade that exposes object generation for generic types, common to all {@link Object} derived
 *
 * @author Serge Pruteanu
 */
public class ObjectScrambler {
    //------------------------------------------------------------------------------------------------------------------
    // Object methods
    //------------------------------------------------------------------------------------------------------------------
    public static <T> Data<T> constant(T obj) {
        return new ConstantData<T>(obj);
    }

    public static <T> Data<T> random(Class<T> self) {
        return random(self, null);
    }

    @SuppressWarnings({"unchecked"})
    public static <T> Data<T> random(Class<T> self, T defaultObj) {
        if (Types.randomTypeMap.containsKey(self)) {
            if (self.isArray()) {
                return ArrayScrambler.randomArray(self, defaultObj, null);
            } else {
                if (self.isPrimitive()) {
                    return (Data) Util.createInstance(Types.randomTypeMap.get(self), null, null);
                } else {
                    return (Data) Util.createInstance(Types.randomTypeMap.get(self), new Object[]{defaultObj}, new Class[]{self});
                }
            }
        }
        throw new UnsupportedOperationException(String.format("The method is not supported for class type: %s, default object: %s", self, defaultObj));
    }

    public static <T> Data<T> random(Class<T> self, T minimum, T maximum) {
        final Data<T> data = random(self, minimum);
        if (data instanceof AbstractRandomRange) {
            final AbstractRandomRange<T> randomRange = (AbstractRandomRange<T>) data;
            randomRange.between(minimum, maximum);
        } else {
            throw new UnsupportedOperationException(String.format(Types.NOT_SUPPORTED_RANGE_TYPE_MSG, self, minimum, maximum));
        }
        return data;
    }

    //------------------------------------------------------------------------------------------------------------------
    // Boolean methods
    //------------------------------------------------------------------------------------------------------------------
    public static Data<Boolean> random(Boolean obj) {
        return new RandomBoolean(obj);
    }

}
