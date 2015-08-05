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
class DoubleValueArray extends Constant<double[]> {
    private Integer count;
    private DoubleArray instance;
    private boolean useInitialArray;

    public DoubleValueArray() {
    }

    public DoubleValueArray(double[] array, DoubleArray value) {
        this(array, null, value);
    }

    public DoubleValueArray(double[] array, Integer count, Object value) {
        this(array, count, (DoubleArray)value);
    }

    public DoubleValueArray(double[] array, Integer count, DoubleArray value1) {
        super(array);
        this.count = count != null ? count : array != null ? array.length : null;
        this.instance = value1;
        useInitialArray = array != null && array.length == this.count;
    }

    protected double[] doNext() {
        Integer count = this.count;
        if (count == null) {
            count = new RandomInteger(1).between(1, 20).next();
        }

        double[] value = useInitialArray ? this.value : new double[count];
        instance.next(value);

        useInitialArray = false;
        return value;
    }

    public void setCount(Integer count) {
        this.count = count;
    }

}
