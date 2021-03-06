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

package org.prismus.scrambler.data;

import org.prismus.scrambler.Data;

import java.util.List;

/**
 * Container of objects, that will generate an array of provided {@link Data} instances
 *
 * @author Serge Pruteanu
 */
public class ArrayContainerData extends ConstantData<Object[]> {
    private List<Data> argumentTypes;

    public ArrayContainerData(List<Data> argumentTypes) {
        this.argumentTypes = argumentTypes;
    }

    @Override
    protected Object[] doNext() {
        final Object[] results = new Object[argumentTypes.size()];
        for (int i = 0; i < argumentTypes.size(); i++) {
            results[i] = argumentTypes.get(i).next();
        }
        return results;
    }

}
