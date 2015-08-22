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

import java.io.Serializable;

/**
 * An interface used to generate data.
 *
 * @author Serge Pruteanu
 */
public interface Value<T> extends Serializable, Cloneable {

    /**
     * Generates value.
     *
     * @return an instance of object
     */
    T next();

    /**
     * Gets current generated value
     *
     * @return current value
     */
    T get();

    Object clone() throws CloneNotSupportedException;

}
