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
import org.prismus.scrambler.value.ValueDefinition;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.List;

/**
 * Facade class helping to generate values for inquired method
 *
 * @author Serge Pruteanu
 */
public class BoxTestScrambler {

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

    public static Value<Object[]> methodValues(InstanceValue instanceValue, String method, Class... args) throws NoSuchMethodException, IllegalArgumentException {
        lookupMethod((Class<?>) instanceValue.lookupType(), method, args);
        instanceValue.scanDefinitions(instanceValue.lookupType().toString() + "#" + method);
        return new ArrayContainerValue(Arrays.asList(instanceValue.lookupValues(args)));
    }

    public static Value<Object[]> methodValues(Class clazzType, String method, Class... args) throws NoSuchMethodException, IllegalArgumentException {
        return methodValues(InstanceScrambler.instanceOf(clazzType), method, args);
    }

    public static Value<Object[]> methodValues(List<String> definitions, String method, Class... args) throws NoSuchMethodException, IllegalArgumentException {
        final ValueDefinition valueDefinition = new ValueDefinition().scanDefinitions(definitions);
        return new ArrayContainerValue(valueDefinition.lookupValues(Arrays.asList(args)));
    }

    @SuppressWarnings("unchecked")
    public static Value<Object[]> methodValues(Object instance, String method, Class... args) throws NoSuchMethodException, IllegalArgumentException {
        final Class clazzType = instance.getClass();
        return methodValues(InstanceScrambler.instanceOf(clazzType).usingValue(instance), method, args);
    }

}
