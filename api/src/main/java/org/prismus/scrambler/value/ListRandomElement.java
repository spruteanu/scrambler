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

package org.prismus.scrambler.value;

import java.util.List;
import java.util.Random;

/**
 * @author Serge Pruteanu
 */
public class ListRandomElement<T> extends ConstantData<T> {
    private final Random random;
    private List<T> values;

    public ListRandomElement(List<T> values) {
        super();
        this.values = values;
        setValue(values.get(0));
        random = new Random();
    }

    public void setValues(List<T> values) {
        this.values = values;
    }

    protected T doNext() {
        return values.get(random.nextInt(values.size()));
    }

}
