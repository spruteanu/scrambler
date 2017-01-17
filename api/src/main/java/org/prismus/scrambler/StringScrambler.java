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

import org.prismus.scrambler.value.ArrayData;
import org.prismus.scrambler.value.IncrementalString;
import org.prismus.scrambler.value.RandomString;
import org.prismus.scrambler.value.RandomUuid;

/**
 * {@link String} value methods, exposes all possible ways to generate {@link String} objects
 *
 * @author Serge Pruteanu
 */
public class StringScrambler {

    //------------------------------------------------------------------------------------------------------------------
    // String methods
    //------------------------------------------------------------------------------------------------------------------
    public static IncrementalString increment(String self) {
        return new IncrementalString(self);
    }

    public static IncrementalString increment(String self, String pattern) {
        return new IncrementalString(self, pattern);
    }

    public static IncrementalString increment(String self, Integer index) {
        return new IncrementalString(self, index);
    }

    public static IncrementalString increment(String self, String pattern, Integer index) {
        return new IncrementalString(self, pattern, index);
    }

    public static ArrayData<String> incrementArray(String self, Integer count) {
        return new ArrayData<String>(String.class, count, new IncrementalString(self));
    }

    public static ArrayData<String> incrementArray(String self, String pattern, Integer count) {
        return new ArrayData<String>(String.class, count, new IncrementalString(self, pattern));
    }

    public static ArrayData<String> incrementArray(String value, Integer index, Integer count) {
        return new ArrayData<String>(String.class, count, new IncrementalString(value, index));
    }

    public static ArrayData<String> incrementArray(String value, String pattern, Integer index, Integer count) {
        return new ArrayData<String>(String.class, count, new IncrementalString(value, pattern, index));
    }

    public static RandomString random(String value) {
        return new RandomString(value);
    }

    public static RandomString random(String value, Integer count) {
        return new RandomString(value, count);
    }

    public static RandomUuid randomUuid() {
        return new RandomUuid();
    }

    public static ArrayData<String> randomArray(String value, Integer arrayCount) {
        return new ArrayData<String>(String.class, arrayCount, random(value));
    }

    public static ArrayData<String> randomArray(String value, Integer count, Integer arrayCount) {
        return new ArrayData<String>(String.class, count, random(value, arrayCount));
    }

}
