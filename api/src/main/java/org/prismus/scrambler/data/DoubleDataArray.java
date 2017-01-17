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

package org.prismus.scrambler.data;

/**
 * @author Serge Pruteanu
 */
class DoubleDataArray extends ConstantData<double[]> {
    private Integer count;
    private DoubleArray instance;
    private boolean useInitialArray;

    public DoubleDataArray() {
    }

    public DoubleDataArray(double[] array, DoubleArray array1) {
        this(array, null, array1);
    }

    public DoubleDataArray(double[] array, Integer count, Object array1) {
        this(array, count, (DoubleArray)array1);
    }

    public DoubleDataArray(double[] array, Integer count, DoubleArray array1) {
        super(array);
        this.count = count != null ? count : array != null ? array.length : null;
        this.instance = array1;
        useInitialArray = array != null && array.length == this.count;
    }

    protected double[] doNext() {
        Integer count = this.count;
        if (count == null) {
            count = new RandomInteger(1).between(1, 20).next();
        }

        double[] array = useInitialArray ? this.object : new double[count];
        instance.next(array);

        useInitialArray = false;
        return array;
    }

    public void setCount(Integer count) {
        this.count = count;
    }

}
