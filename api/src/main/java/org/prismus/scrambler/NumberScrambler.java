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

import org.prismus.scrambler.value.*;

import java.math.BigDecimal;
import java.math.BigInteger;

/**
 * {@link Number} value methods, exposes all possible ways to generate {@link Number} objects
 *
 * @author Serge Pruteanu
 */
public class NumberScrambler {

    public static Value<Byte> increment(byte self) {
        return new IncrementalByte(self);
    }

    public static Value<Byte> increment(byte self, byte step) {
        return new IncrementalByte(self, step);
    }

    public static Value<Byte> random(byte seed) {
        return new RandomByte(seed);
    }

    public static Value<Byte> random(byte minimum, byte maximum) {
        return new RandomByte(minimum, maximum);
    }

    public static Value<Byte> random(byte seed, byte minimum, byte maximum) {
        return new RandomByte(seed, minimum, maximum);
    }

    public static Value<Short> increment(short self) {
        return new IncrementalShort(self);
    }

    public static Value<Short> increment(short self, short step) {
        return new IncrementalShort(self, step);
    }

    public static Value<Short> random(short seed) {
        return new RandomShort(seed);
    }

    public static Value<Short> random(short minimum, short maximum) {
        return new RandomShort(minimum, maximum);
    }

    public static Value<Short> random(short seed, short minimum, short maximum) {
        return new RandomShort(seed, minimum, maximum);
    }

    public static Value<Integer> increment(int self) {
        return new IncrementalInteger(self);
    }

    public static Value<Integer> increment(int self, int step) {
        return new IncrementalInteger(self, step);
    }

    public static Value<Integer> random(int seed) {
        return new RandomInteger(seed);
    }

    public static Value<Integer> random(int minimum, int maximum) {
        return new RandomInteger(minimum, maximum);
    }

    public static Value<Integer> random(int seed, int minimum, int maximum) {
        return new RandomInteger(seed, minimum, maximum);
    }

    public static Value<Long> increment(long self) {
        return new IncrementalLong(self);
    }

    public static Value<Long> increment(long self, long step) {
        return new IncrementalLong(self, step);
    }

    public static Value<Long> random(long seed) {
        return new RandomLong(seed);
    }

    public static Value<Long> random(long minimum, long maximum) {
        return new RandomLong(minimum, maximum);
    }

    public static Value<Long> random(long seed, long minimum, long maximum) {
        return new RandomLong(seed, minimum, maximum);
    }

    public static Value<Float> increment(float self) {
        return new IncrementalFloat(self);
    }

    public static Value<Float> increment(float self, float step) {
        return new IncrementalFloat(self, step);
    }

    public static Value<Float> random(float seed) {
        return new RandomFloat(seed);
    }

    public static Value<Float> random(float minimum, float maximum) {
        return new RandomFloat(minimum, maximum);
    }

    public static Value<Float> random(float seed, float minimum, float maximum) {
        return new RandomFloat(seed, minimum, maximum);
    }

    public static Value<Double> increment(Double self) {
        return new IncrementalDouble(self);
    }

    public static Value<Double> increment(double self, double step) {
        return new IncrementalDouble(self, step);
    }

    public static Value<Double> random(double seed) {
        return new RandomDouble(seed);
    }

    public static Value<Double> random(double minimum, double maximum) {
        return new RandomDouble(minimum, maximum);
    }

    public static Value<Double> random(double seed, double minimum, double maximum) {
        return new RandomDouble(seed, minimum, maximum);
    }

    public static Value<BigInteger> increment(BigInteger self) {
        return new IncrementalBigInteger(self);
    }

    public static Value<BigInteger> increment(BigInteger self, BigInteger step) {
        return new IncrementalBigInteger(self, step);
    }

    public static Value<BigInteger> random(BigInteger seed) {
        return new RandomBigInteger(seed);
    }

    public static Value<BigInteger> random(BigInteger minimum, BigInteger maximum) {
        return new RandomBigInteger(minimum, maximum);
    }

    public static Value<BigInteger> random(BigInteger seed, BigInteger minimum, BigInteger maximum) {
        return new RandomBigInteger(seed, minimum, maximum);
    }

    public static Value<BigDecimal> increment(BigDecimal self) {
        return new IncrementalBigDecimal(self);
    }

    public static Value<BigDecimal> increment(BigDecimal self, BigDecimal step) {
        return new IncrementalBigDecimal(self, step);
    }

    public static Value<BigDecimal> random(BigDecimal seed) {
        return new RandomBigDecimal(seed);
    }

    public static Value<BigDecimal> random(BigDecimal minimum, BigDecimal maximum) {
        return new RandomBigDecimal(minimum, maximum);
    }

    public static Value<BigDecimal> random(BigDecimal seed, BigDecimal minimum, BigDecimal maximum) {
        return new RandomBigDecimal(seed, minimum, maximum);
    }

    @SuppressWarnings({"unchecked"})
    public static <T extends Number> Value<T> increment(T self) {
        return increment((Class<T>) self.getClass(), self, null);
    }

    @SuppressWarnings({"unchecked"})
    public static <T extends Number> Value<T> increment(T self, T step) {
        return increment((Class<T>) self.getClass(), self, step);
    }

    @SuppressWarnings({"unchecked"})
    public static <T extends Number> Value<T> increment(Class<T> self, T defaultValue, T step) {
        if (Types.incrementTypeMap.containsKey(self)) {
            final Value value;
            if (self.isArray()) {
                value = ArrayScrambler.incrementArray(self, defaultValue, step, null);
            } else {
                value = (Value) Util.createInstance(Types.incrementTypeMap.get(self), new Object[]{defaultValue, step}, new Class[]{self, self});
            }
            return value;
        }
        throw new UnsupportedOperationException(String.format("The method is not supported for class type: %s, default value: %s", self, self));
    }

    @SuppressWarnings({"unchecked"})
    public static <T extends Number> Value<T> random(T value) {
        return ObjectScrambler.random((Class<T>) value.getClass(), value);
    }

    @SuppressWarnings({"unchecked"})
    public static <T extends Number> Value<T> random(T minimum, T maximum) {
        return ObjectScrambler.random((Class<T>) minimum.getClass(), minimum, maximum);
    }

    @SuppressWarnings({"unchecked"})
    public static <T extends Number> Value<T> random(T val, T minimum, T maximum) {
        final Value<T> value = ObjectScrambler.random((Class<T>) val.getClass(), val);
        if (value instanceof AbstractRandomRange) {
            final AbstractRandomRange<Number> randomRangeValue = (AbstractRandomRange<Number>) value;
            randomRangeValue.between(minimum, maximum);
        } else {
            throw new UnsupportedOperationException(String.format(Types.NOT_SUPPORTED_RANGE_TYPE_MSG, val.getClass(), minimum, maximum));
        }
        return value;
    }

    @SuppressWarnings("unchecked")
    public static <T extends Number> Value<T> randomArray(Class self, T minimum, T maximum, Integer count) {
        Util.checkPositiveCount(count);

        Object defaultValue = minimum != null ? minimum : maximum != null ? maximum : null;
        if (self == null) {
            self = defaultValue != null ? defaultValue.getClass() : null;
        }
        if (self == null) {
            throw new IllegalArgumentException(String.format("Either minimum: %s or maximum: %s should be not null", minimum, maximum));
        }
        boolean primitive = false;
        Class<?> componentType;
        if (self.isArray()) {
            componentType = self.getComponentType();
            if (componentType.isPrimitive()) {
                primitive = true;
                componentType = Types.primitiveWrapperMap.get(componentType);
            }
        } else {
            componentType = self;
        }

        Value instance = (Value) Util.createInstance(Types.randomTypeMap.get(componentType),
                new Object[]{minimum, maximum}, new Class[]{componentType, componentType}
        );
        final Value<T> value;
        if (primitive) {
            value = (Value<T>) Util.createInstance(Types.randomTypeMap.get(self),
                    new Object[]{self.isInstance(defaultValue) ? defaultValue : null, count, instance},
                    new Class[]{self, Integer.class, Object.class}
            );
        } else {
            value = new ArrayValue(self, count, instance);
        }
        return value;
    }

}
