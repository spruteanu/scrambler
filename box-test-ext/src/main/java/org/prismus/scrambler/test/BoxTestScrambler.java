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

package org.prismus.scrambler.test;

import org.prismus.scrambler.InstanceScrambler;
import org.prismus.scrambler.Value;
import org.prismus.scrambler.value.ArrayContainerValue;
import org.prismus.scrambler.value.InstanceValue;
import org.prismus.scrambler.value.ValueDefinition;

import java.util.Arrays;
import java.util.List;

/**
 * Facade class helping to generate values for inquired method
 *
 * @author Serge Pruteanu
 */
public class BoxTestScrambler {

    public static Value<Object[]> methodValues(InstanceValue instanceValue, String method, Class... args) throws NoSuchMethodException, IllegalArgumentException {
        BoxTestSuite.lookupMethod((Class<?>) instanceValue.lookupType(), method, args);
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
