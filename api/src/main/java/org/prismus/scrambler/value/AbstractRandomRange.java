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

/**
 * @author Serge Pruteanu
 */
public abstract class AbstractRandomRange<T> extends ConstantData<T> {
    protected T defaultMinimum;
    protected T defaultMaximum;
    protected T minimum;
    protected T maximum;

    public AbstractRandomRange() {
        super();
    }

    public AbstractRandomRange(T value) {
        super(value);
    }

    public AbstractRandomRange(T minimum, T maximum) {
        this(null, minimum, maximum);
    }

    public AbstractRandomRange(T value, T minimum, T maximum) {
        super(value);
        between(minimum, maximum);
    }

    public AbstractRandomRange<T> usingDefaults(T minimum, T maximum) {
        this.defaultMinimum = min(minimum, maximum);
        this.defaultMaximum = max(minimum, maximum);
        return this;
    }

    public AbstractRandomRange<T> between(T minimum, T maximum) {
        this.minimum = min(minimum, maximum);
        this.maximum = max(minimum, maximum);
        checkBoundaries();
        return this;
    }

    protected abstract T min(T val1, T val2);

    protected abstract T max(T val1, T val2);

    protected void checkBoundaries() {
        if (minimum == null && maximum == null) {
            minimum = defaultMinimum;
            maximum = defaultMaximum;
        } else if (minimum != null && maximum == null) {
            maximum = defaultMaximum;
        } else if (minimum == null) {
            minimum = defaultMinimum;
        }
    }
}
