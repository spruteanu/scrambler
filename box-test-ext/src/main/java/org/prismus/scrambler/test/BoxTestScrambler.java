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

import org.prismus.scrambler.Data;
import org.prismus.scrambler.value.ArrayContainerData;
import org.prismus.scrambler.value.DataDefinition;

import java.util.Arrays;

/**
 * Facade class helping to generate values for inquired method
 *
 * @author Serge Pruteanu
 */
public class BoxTestScrambler {

    public static Data<Object[]> methodValues(Class clazzType, String method, Class... args) throws NoSuchMethodException, IllegalArgumentException {
        return BoxTestSuite.of(clazzType).methodValues(method, args);
    }

    public static Data<Object[]> methodValues(String method, Class[] args, String... definitions) throws NoSuchMethodException, IllegalArgumentException {
        final DataDefinition dataDefinition = new DataDefinition().scanDefinitions(Arrays.asList(definitions));
        return new ArrayContainerData(dataDefinition.lookupValues(Arrays.asList(args)));
    }

    public static Data<Object[]> methodValues(Object instance, String method, Class... args) throws NoSuchMethodException, IllegalArgumentException {
        return BoxTestSuite.of(instance).methodValues(method, args);
    }

    public static BoxTestSuite inspect(Object inspected) {
        return BoxTestSuite.of(inspected);
    }

}
