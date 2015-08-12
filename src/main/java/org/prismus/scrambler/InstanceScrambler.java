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
 * DataScrambler is a facade class with exposed capabilities on data generation
 *
 * @author Serge Pruteanu
 */
public class InstanceScrambler {

    //------------------------------------------------------------------------------------------------------------------
    // Parse definition methods
    //------------------------------------------------------------------------------------------------------------------
    public static ValueDefinition parseDefinition(String definition) throws IOException {
        return Holder.groovyValueDefinition.parseDefinition(definition);
    }

    public static ValueDefinition parseDefinition(String definition, Map<String, Object> contextMap) throws IOException {
        return Holder.groovyValueDefinition.parseDefinition(definition, contextMap);
    }

    static <T> InstanceValue<T> parseDefinitions(InstanceValue<T> instanceValue, Map<String, Object> contextMap, String... definitions) throws IOException {
        if (definitions != null) {
            for (String definition : definitions) {
                instanceValue.usingDefinitions(parseDefinition(definition, contextMap));
            }
        }
        return instanceValue;
    }

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

    public static ValueDefinition parseDefinition(File definition) throws IOException {
        return Holder.groovyValueDefinition.parseDefinition(definition);
    }

    public static ValueDefinition parseDefinition(File definition, Map<String, Object> contextMap) throws IOException {
        return Holder.groovyValueDefinition.parseDefinition(definition, contextMap);
    }

    static <T> InstanceValue<T> parseDefinitions(InstanceValue<T> instanceValue, Map<String, Object> contextMap, File... definitions) throws IOException {
        if (definitions != null) {
            for (File definition : definitions) {
                instanceValue.usingDefinitions(parseDefinition(definition, contextMap));
            }
        }
        return instanceValue;
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

    public static ValueDefinition parseDefinition(InputStream inputStream) throws IOException {
        return Holder.groovyValueDefinition.parseDefinition(inputStream);
    }

    public static ValueDefinition parseDefinition(InputStream inputStream, Map<String, Object> contextMap) throws IOException {
        return Holder.groovyValueDefinition.parseDefinition(inputStream, contextMap);
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

    public static ValueDefinition parseDefinition(Reader reader, Map<String, Object> contextMap) throws IOException {
        return Holder.groovyValueDefinition.parseDefinition(reader, contextMap);
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


    //------------------------------------------------------------------------------------------------------------------
    // Value definition parse methods
    //------------------------------------------------------------------------------------------------------------------
    @SuppressWarnings("unchecked")
    public static <T> Value<T> valueOf(Class<T> clazzType, String definitionResource) throws IOException {
        return Holder.groovyValueDefinition.parseValue(definitionResource);
    }

    @SuppressWarnings("unchecked")
    public static <T> Value<T> valueOf(String clazzType, String definitionResource) throws IOException {
        return Holder.groovyValueDefinition.parseValue(definitionResource);
    }

    @SuppressWarnings("unchecked")
    public static <T> Value<T> valueOf(Class<T> clazzType, File definition) throws IOException {
        return Holder.groovyValueDefinition.parseValue(definition);
    }

    @SuppressWarnings("unchecked")
    public static <T> Value<T> valueOf(String clazzType, File definition) throws IOException {
        return Holder.groovyValueDefinition.parseValue(definition);
    }

    @SuppressWarnings("unchecked")
    public static <T> Value<T> valueOf(Class<T> clazzType, InputStream inputStream) throws IOException {
        return Holder.groovyValueDefinition.parseValue(inputStream);
    }

    @SuppressWarnings("unchecked")
    public static <T> Value<T> valueOf(String clazzType, InputStream inputStream) throws IOException {
        return Holder.groovyValueDefinition.parseValue(inputStream);
    }

    @SuppressWarnings("unchecked")
    public static <T> Value<T> valueOf(Class<T> clazzType, Reader reader) throws IOException {
        return Holder.groovyValueDefinition.parseValue(reader);
    }

    @SuppressWarnings("unchecked")
    public static <T> Value<T> valueOf(Reader reader) throws IOException {
        return Holder.groovyValueDefinition.parseValue(reader);
    }


    //------------------------------------------------------------------------------------------------------------------
    // InstanceValue methods
    //------------------------------------------------------------------------------------------------------------------
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
        return new InstanceValue<T>(self).withPredicate(ValuePredicates.predicateOf(self)).withDefinitionClosure(defCl);
    }

    public static <T> InstanceValue<T> instanceOf(Class<T> self, Map<Object, Object> propertyValueMap, AbstractDefinitionCallable defCl) {
        return new InstanceValue<T>(self).withPredicate(ValuePredicates.predicateOf(self)).withDefinitionClosure(defCl).usingDefinitions(propertyValueMap);
    }

    public static <T> InstanceValue<T> instanceOf(Class<T> self, String propertyName, AbstractDefinitionCallable defCl) {
        return new InstanceValue<T>(self).withPredicate(ValuePredicates.predicateOf(propertyName)).withDefinitionClosure(defCl);
    }

    public static <T> InstanceValue<T> instanceOf(Class<T> self, Collection constructorArgs, AbstractDefinitionCallable defCl) {
        return new InstanceValue<T>(self).withPredicate(ValuePredicates.predicateOf(self)).withDefinitionClosure(defCl).withConstructorArguments(constructorArgs);
    }

    public static <T> InstanceValue<T> instanceOf(Class<T> self, String propertyName, Collection constructorArgs, AbstractDefinitionCallable defCl) {
        return new InstanceValue<T>(self).withPredicate(ValuePredicates.predicateOf(propertyName)).withDefinitionClosure(defCl).withConstructorArguments(constructorArgs);
    }

    public static ReferenceValue reference(Class self) {
        return reference(self, null);
    }

    public static ReferenceValue reference(Class self, String propertyPredicate) {
        return new ReferenceValue(ValuePredicates.predicateOf(self), propertyPredicate != null ? ValuePredicates.predicateOf(propertyPredicate) : null);
    }

    private static class Holder {
        private static GroovyValueDefinition groovyValueDefinition = new GroovyValueDefinition();
    }

}
