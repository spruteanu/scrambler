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

import java.util.Arrays;

/**
 * Facade class that exposes generation of arrays in various forms
 *
 * @author Serge Pruteanu
 */
public class ArrayScrambler {
    public static <T> Value<T> arrayOf(Class<T> self, Value val) {
        return of(self, val, null);
    }

    public static <T> Value<T> arrayOf(Class<T> self, Value val, Integer count) {
        return of(self, val, count);
    }

    public static <T> Value<T> arrayOf(Object self, Value value) {
        return arrayOf(self, value, null);
    }

    @SuppressWarnings("unchecked")
    public static <T> Value<T> arrayOf(Object self, Value value, Integer count) {
        Util.checkNullValue(self);
        final Class<?> selfClass = self.getClass();
        if (!selfClass.isArray()) {
            throw new IllegalArgumentException(String.format("An array instance must be provided; provided: %s", self));
        }
        final Class<?> componentType = selfClass.getComponentType();
        if (componentType.isPrimitive()) {
            return (Value) Util.createInstance(Types.primitivesArrayTypeMap.get(componentType),
                    new Object[]{self, count, value}, new Class[]{selfClass, Integer.class, Object.class}
            );
        } else {
            return new ArrayValue((T[]) self, count, value);
        }
    }

    @SuppressWarnings("unchecked")
    public static <T> Value<T> incrementArray(Object self, Object step, Integer count) {
        return incrementArray((Class<T>) self.getClass(), self, step, count);
    }

    public static <T> Value<T> randomOf(T[] array) {
        return new ArrayRandomElement<T>(array);
    }

    @SuppressWarnings("unchecked")
    public static <T> Value<T> randomArray(Object value) {
        return (Value<T>) randomArray(value.getClass(), value, null);
    }

    @SuppressWarnings("unchecked")
    public static <T> Value<T> randomArray(Object value, Integer count) {
        return (Value<T>) randomArray(value.getClass(), (Object) value, count);
    }

    @SuppressWarnings({"unchecked"})
    public static <T> Value<T> incrementArray(Class<T> self, Object defaultValue, Object step, Integer count) {
        Util.checkPositiveCount(count);
        final Class<?> componentType = self.isArray() ? self.getComponentType() : self;
        final Value value;
        if (componentType.isPrimitive()) {
            value = (Value) Util.createInstance(Types.incrementTypeMap.get(self), new Object[]{
                            self.isInstance(defaultValue) ? defaultValue : null, count,
                            (Value) Util.createInstance(
                                    Types.incrementTypeMap.get(componentType),
                                    new Object[]{self.isInstance(defaultValue) ? null : defaultValue, step},
                                    new Class[]{Types.primitiveWrapperMap.get(componentType), Types.primitiveWrapperMap.get(componentType)}
                            )}, new Class[]{self, Integer.class, Object.class}
            );
        } else {
            value = new ArrayValue(componentType, count, (Value) Util.createInstance(
                    Types.incrementTypeMap.get(componentType),
                    new Object[]{componentType.isInstance(defaultValue) ? defaultValue : null, step}, new Class[]{componentType, componentType}
            ));
        }
        return value;
    }

    @SuppressWarnings({"unchecked"})
    public static <T> Value<T> randomArray(Class<T> self, Object defaultValue, Integer count) {
        final Class<?> componentType = self.isArray() ? self.getComponentType() : self;
        final Class<?> valueClassType = componentType.isPrimitive() ? Types.primitiveWrapperMap.get(componentType) : componentType;
        final Value valueType;
        if (defaultValue != null && !defaultValue.getClass().isArray()) {
            valueType = (Value) Util.createInstance(Types.randomTypeMap.get(valueClassType), new Object[]{defaultValue,}, new Class[]{valueClassType,});
        } else {
            valueType = (Value) Util.createInstance(Types.randomTypeMap.get(valueClassType), new Object[]{}, new Class[]{});
        }
        if (componentType.isPrimitive()) {
            final Class<? extends Value> arrayValueType = Types.primitivesArrayTypeMap.get(componentType);
            return (Value) Util.createInstance(arrayValueType, new Object[]{defaultValue, count, valueType}, new Class[]{self, Integer.class, Object.class});
        } else {
            return new ArrayValue(componentType, count, valueType);
        }
    }

    @SuppressWarnings("unchecked")
    public static <T> Value<T> of(Class clazzType, Value val, Integer count) {
        if (clazzType.isPrimitive()) {
            final Class<? extends Value> arrayValueType = Types.primitivesArrayTypeMap.get(clazzType);
            return (Value) Util.createInstance(arrayValueType, new Object[]{null, count, val}, new Class[]{Types.arrayTypeMap.get(clazzType), Integer.class, Object.class});
        } else {
            return new ArrayValue(clazzType, count, val);
        }
    }

    public static ArrayContainerValue of(Value... values) {
        return new ArrayContainerValue(Arrays.asList(values));
    }

    // todo Serge: there is no way to select a random element from primitive array. Also, most probably it will be better to expose array creation of primitive arrays

}
