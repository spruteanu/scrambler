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

/**
 * todo: add description
 *
 * @author Serge Pruteanu
 */
public class MethodScrambler {

    public static Value<Object[]> inspectMethod(Class clazzType, String method, Class... args) {
        clazzType.getMethods()[0].getParameterTypes();
        return null;
    }

    public static Value<Object[]> inspectMethod(Object instance, String method, Class... args) {
        return null;
    }

}
