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

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.util.*;

/**
 * InstanceScrambler is a facade class that exposes generation capabilities for instances/classes
 *
 * @author Serge Pruteanu
 */
public class InstanceScrambler {

    //------------------------------------------------------------------------------------------------------------------
    // Parse definition methods
    //------------------------------------------------------------------------------------------------------------------
    public static ValueDefinition parseDefinition(String definition) throws IOException {
        return GroovyValueDefinition.Holder.instance.parseDefinition(definition);
    }

    public static ValueDefinition parseDefinition(String definition, Map<String, Object> contextMap) throws IOException {
        return GroovyValueDefinition.Holder.instance.parseDefinition(definition, contextMap);
    }

    static <T> InstanceValue<T> parseDefinitions(InstanceValue<T> instanceValue, Map<String, Object> contextMap, String... definitions) throws IOException {
        if (definitions != null) {
            for (String definition : definitions) {
                instanceValue.usingDefinitions(parseDefinition(definition, contextMap));
            }
        }
        return instanceValue;
    }

    public static ValueDefinition parseDefinition(File definition) throws IOException {
        return GroovyValueDefinition.Holder.instance.parseDefinition(definition);
    }

    public static ValueDefinition parseDefinition(File definition, Map<String, Object> contextMap) throws IOException {
        return GroovyValueDefinition.Holder.instance.parseDefinition(definition, contextMap);
    }

    static <T> InstanceValue<T> parseDefinitions(InstanceValue<T> instanceValue, Map<String, Object> contextMap, File... definitions) throws IOException {
        if (definitions != null) {
            for (File definition : definitions) {
                instanceValue.usingDefinitions(parseDefinition(definition, contextMap));
            }
        }
        return instanceValue;
    }

    public static ValueDefinition parseDefinition(InputStream inputStream) throws IOException {
        return GroovyValueDefinition.Holder.instance.parseDefinition(inputStream);
    }

    public static ValueDefinition parseDefinition(InputStream inputStream, Map<String, Object> contextMap) throws IOException {
        return GroovyValueDefinition.Holder.instance.parseDefinition(inputStream, contextMap);
    }

    static <T> InstanceValue<T> parseDefinitions(InstanceValue<T> instanceValue, Map<String, Object> contextMap, InputStream... inputStreams) throws IOException {
        if (inputStreams != null) {
            for (InputStream inputStream : inputStreams) {
                try {
                    instanceValue.usingDefinitions(parseDefinition(inputStream, contextMap));
                } finally {
                    try {
                        inputStream.close();
                    } catch (IOException ignore) { }
                }
            }
        }
        return instanceValue;
    }

    public static ValueDefinition parseDefinition(Reader reader, Map<String, Object> contextMap) throws IOException {
        return GroovyValueDefinition.Holder.instance.parseDefinition(reader, contextMap);
    }

    static <T> InstanceValue<T> parseDefinitions(InstanceValue<T> instanceValue, Map<String, Object> contextMap, Reader... readers) throws IOException {
        if (readers != null) {
            for (Reader reader : readers) {
                try {
                    instanceValue.usingDefinitions(parseDefinition(reader, contextMap));
                } finally {
                    try {
                        reader.close();
                    } catch (IOException ignore) { }
                }
            }
        }
        return instanceValue;
    }


    //------------------------------------------------------------------------------------------------------------------
    // InstanceValue methods
    //------------------------------------------------------------------------------------------------------------------
    public static <T> InstanceValue<T> instanceOf(Class<T> clazzType, String... definitions) throws IOException {
        return parseDefinitions(instanceOf(clazzType), null, definitions);
    }

    public static <T> InstanceValue<T> instanceOf(Class<T> clazzType, Map<String, Object> contextMap, String... definitions) throws IOException {
        return parseDefinitions(instanceOf(clazzType), contextMap, definitions);
    }

    @SuppressWarnings("unchecked")
    public static <T> InstanceValue<T> instanceOf(String clazzType, String... definitions) throws IOException {
        return (InstanceValue<T>) parseDefinitions(instanceOf(clazzType), null, definitions);
    }

    @SuppressWarnings("unchecked")
    public static <T> InstanceValue<T> instanceOf(String clazzType, Map<String, Object> contextMap, String... definitions) throws IOException {
        return (InstanceValue<T>) parseDefinitions(instanceOf(clazzType), contextMap, definitions);
    }

    public static <T> InstanceValue<T> instanceOf(Class<T> clazzType, File... definitions) throws IOException {
        return parseDefinitions(instanceOf(clazzType), null, definitions);
    }

    public static <T> InstanceValue<T> instanceOf(Class<T> clazzType, Map<String, Object> contextMap, File... definitions) throws IOException {
        return parseDefinitions(instanceOf(clazzType), contextMap, definitions);
    }

    @SuppressWarnings("unchecked")
    public static <T> InstanceValue<T> instanceOf(String clazzType, File... definitions) throws IOException {
        return (InstanceValue<T>) parseDefinitions(instanceOf(clazzType), null, definitions);
    }

    @SuppressWarnings("unchecked")
    public static <T> InstanceValue<T> instanceOf(String clazzType, Map<String, Object> contextMap, File... definitions) throws IOException {
        return (InstanceValue<T>) parseDefinitions(instanceOf(clazzType), contextMap, definitions);
    }

    public static <T> InstanceValue<T> instanceOf(Class<T> clazzType, InputStream... inputStreams) throws IOException {
        return parseDefinitions(instanceOf(clazzType), null, inputStreams);
    }

    public static <T> InstanceValue<T> instanceOf(Class<T> clazzType, Map<String, Object> contextMap, InputStream... inputStreams) throws IOException {
        return parseDefinitions(instanceOf(clazzType), contextMap, inputStreams);
    }

    @SuppressWarnings("unchecked")
    public static <T> InstanceValue<T> instanceOf(String clazzType, InputStream... inputStreams) throws IOException {
        return (InstanceValue<T>) parseDefinitions(instanceOf(clazzType), null, inputStreams);
    }

    @SuppressWarnings("unchecked")
    public static <T> InstanceValue<T> instanceOf(String clazzType, Map<String, Object> contextMap, InputStream... inputStreams) throws IOException {
        return (InstanceValue<T>) parseDefinitions(instanceOf(clazzType), contextMap, inputStreams);
    }

    public static <T> InstanceValue<T> instanceOf(Class<T> clazzType, Reader... readers) throws IOException {
        return parseDefinitions(instanceOf(clazzType), null, readers);
    }

    public static <T> InstanceValue<T> instanceOf(Class<T> clazzType, Map<String, Object> contextMap, Reader... readers) throws IOException {
        return parseDefinitions(instanceOf(clazzType), contextMap, readers);
    }

    @SuppressWarnings("unchecked")
    public static <T> InstanceValue<T> instanceOf(String clazzType, Reader... readers) throws IOException {
        return (InstanceValue<T>) parseDefinitions(instanceOf(clazzType), null, readers);
    }

    @SuppressWarnings("unchecked")
    public static <T> InstanceValue<T> instanceOf(String clazzType, Map<String, Object> contextMap, Reader... readers) throws IOException {
        return (InstanceValue<T>) parseDefinitions(instanceOf(clazzType), contextMap, readers);
    }

    public static <T> InstanceValue<T> instanceOf(String type) {
        return instanceOf(type, (Map<Object, Object>) null);
    }

    public static <T> InstanceValue<T> instanceOf(Class<T> clazzType) {
        return instanceOf(clazzType, (Map<Object, Object>) null);
    }

    public static <T> InstanceValue<T> instanceOf(Class<T> clazzType, Map<Object, Object> fieldMap) {
        return new InstanceValue<T>(clazzType).usingDefinitions(fieldMap);
    }

    public static <T> InstanceValue<T> instanceOf(String type, Map<Object, Object> fieldMap) {
        return new InstanceValue<T>(type).usingDefinitions(fieldMap);
    }

    public static <T> InstanceValue<T> instanceOf(Class<T> self, AbstractDefinitionCallable defCl) {
        return new InstanceValue<T>(self).withDefinitionClosure(defCl);
    }

    public static <T> InstanceValue<T> instanceOf(Class<T> self, Map<Object, Object> propertyValueMap, AbstractDefinitionCallable defCl) {
        return new InstanceValue<T>(self).withDefinitionClosure(defCl).usingDefinitions(propertyValueMap);
    }

    public static <T> InstanceValue<T> instanceOf(Class<T> self, Collection constructorArgs, AbstractDefinitionCallable defCl) {
        return new InstanceValue<T>(self).withDefinitionClosure(defCl).withConstructorArguments(constructorArgs);
    }

    public static ReferenceValue reference(Class self) {
        return reference(self, null);
    }

    public static ReferenceValue reference(Class self, String propertyPredicate) {
        return new ReferenceValue(ValuePredicates.isTypeOf(self), propertyPredicate != null ? ValuePredicates.matchProperty(propertyPredicate) : null);
    }

}
