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
 * Interface that matches either {@code property} and/or {@code object}.
 * The predicate is mostly used as a Map.key for object definitions, to match instance fields
 *
 * @author Serge Pruteanu
 */
public interface DataPredicate {

    /**
     * Match either property and/or object, used to identify if {@link Data} is applicable for provided arguments
     *
     * @param property property name to be matched
     * @param data object to be matched
     * @return true if it should be applicable
     */
    boolean matches(String property, Object data);

}
