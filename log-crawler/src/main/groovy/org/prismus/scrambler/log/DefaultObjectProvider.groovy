/*
 * Log crawler, tool that allows to extract/crawl log files for further analysis
 *
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

package org.prismus.scrambler.log

import groovy.transform.CompileStatic
import org.codehaus.groovy.runtime.DefaultGroovyMethods
import org.codehaus.groovy.runtime.InvokerHelper

import java.util.logging.Level
import java.util.logging.Logger
import java.util.regex.Pattern

/**
 * @author Serge Pruteanu
 */
@CompileStatic
class DefaultObjectProvider implements ObjectProvider {
    private static final Pattern CLASS_PATTERN = ~/([a-zA-Z_$][a-zA-Z\d_$]*\.)*[a-zA-Z_$][a-zA-Z\d_$]*/

    Map<String, Object> objectIdClassMap = [:]

    DefaultObjectProvider() {
    }

    DefaultObjectProvider(Map<String, Object> objectIdClassMap) {
        this.objectIdClassMap = objectIdClassMap
    }

    @Override
    Object get(Object objectId, Object... args) {
        Class clazz = null
        def clazzObj = objectIdClassMap.get(objectId)
        if (clazzObj instanceof Class) {
            clazz = clazzObj
        } else {
            if (clazzObj) {
                if (isClassName(clazzObj.toString())) {
                    clazz = Class.forName(clazzObj.toString())
                }
            }
        }
        if (clazz == null && isClassName(objectId.toString())) {
            clazz = Class.forName(objectId.toString())
        }

        Object object
        if (clazz) {
            try {
                object = DefaultGroovyMethods.newInstance(clazz, args)
            } catch (Exception ignore) {
                throw new RuntimeException("Failed to get object: '$objectId'${(args != null) ? '(' + Arrays.asList(args).toString() + ')' : ''}; null is returned", ignore)
            }
        } else {
            throw new RuntimeException("No object: '$objectId'${(args != null) ? '(' + Arrays.asList(args).toString() + ')' : ''} found; null is returned")
        }
        return object
    }

    static void setInstanceProperties(Object instance, Map<String, Object> instanceProperties) {
        List<String> errors =  new ArrayList<>()
        for (Map.Entry<String, Object> entry : instanceProperties.entrySet()) {
            final name = entry.key
            final value = entry.value
            try {
                InvokerHelper.setProperty(instance, name, value)
            } catch (Exception e) {
                final String message = "Failed to set property: '$name'; value: '$value', error: ${e.message}"
                errors.add(message)
                Logger.getLogger(DefaultObjectProvider.name).log(Level.SEVERE, message, e)
            }
        }
        if (errors.size() > 0) {
            throw new RuntimeException(errors.join(LineReader.LINE_BREAK))
        }
    }

    static boolean isClassName(String className) {
        return CLASS_PATTERN.matcher(className).matches()
    }

}
