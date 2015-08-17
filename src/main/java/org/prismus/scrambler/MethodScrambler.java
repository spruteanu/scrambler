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

import org.prismus.scrambler.value.ArrayContainerValue;
import org.prismus.scrambler.value.InstanceValue;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.LinkedHashSet;

/**
 * todo: add description
 *
 * @author Serge Pruteanu
 */
public class MethodScrambler {

    static Method lookupMethod(Class<?> clazzType, String methodName, Class... args) throws NoSuchMethodException {
        final Method method;
        if (args == null) {
            final LinkedHashSet<Method> methods = new LinkedHashSet<Method>();
            for (final Method m : clazzType.getMethods()) {
                if (m.getName().equalsIgnoreCase(methodName)) {
                    methods.add(m);
                }
            }
            final int size = methods.size();
            if (size > 1) {
                throw new IllegalArgumentException(String.format("More than one methods: %s found for class: %s", methodName, clazzType));
            } else {
                if (size == 0) {
                    throw new NoSuchMethodException(String.format("Not found method: %s for class: %s", methodName, clazzType));
                }
            }
            method = methods.iterator().next();
        } else {
            method = clazzType.getMethod(methodName, args);
        }
        return method;
    }

    public static Value<Object[]> inspectMethod(InstanceValue instanceValue, String method, Class... args) throws NoSuchMethodException, IllegalArgumentException {
        lookupMethod((Class<?>) instanceValue.lookupType(), method, args);
        return new ArrayContainerValue(Arrays.asList(instanceValue.lookupValues(args)));
    }

    public static Value<Object[]> inspectMethod(Class clazzType, String method, Class... args) throws NoSuchMethodException, IllegalArgumentException {
        return inspectMethod(InstanceScrambler.instanceOf(clazzType), method, args);
    }

    @SuppressWarnings("unchecked")
    public static Value<Object[]> inspectMethod(Object instance, String method, Class... args) throws NoSuchMethodException, IllegalArgumentException {
        final Class clazzType = instance.getClass();
        return inspectMethod(InstanceScrambler.instanceOf(clazzType).usingValue(instance), method, args);
    }

}