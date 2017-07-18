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
    private static final Logger logger = Logger.getLogger(DefaultObjectProvider.class.getName())
    private static final Pattern CLASS_PATTERN = ~/([a-zA-Z_$][a-zA-Z\d_$]*\.)*[a-zA-Z_$][a-zA-Z\d_$]*/

    Map<String, Object> objectIdClassMap = [:]

    DefaultObjectProvider() {
    }

    DefaultObjectProvider(Map<String, Object> objectIdClassMap) {
        this.objectIdClassMap = objectIdClassMap
    }

    @Override
    Object get(String objectId, Object... args) {
        Class clazz = null
        def clazzObj = objectIdClassMap.get(objectId)
        if (clazzObj instanceof Class) {
            clazz = clazzObj
        } else {
            if (clazzObj) {
                if (isClassName(clazzObj.toString())) {
                    clazz = resolveClass(clazzObj.toString())
                }
            }
        }
        if (clazz == null && isClassName(objectId)) {
            clazz = resolveClass(objectId)
        }

        Object object = null
        if (clazz) {
            try {
                object = DefaultGroovyMethods.newInstance(clazz, args)
            } catch (Exception ignore) {
                logger.log(Level.SEVERE, "Failed to get object: '$objectId'${(args != null) ? '(' + Arrays.asList(args).toString() + ')' : ''}; null is returned", ignore)
            }
        } else {
            if (logger.isLoggable(Level.FINE)) {
                logger.log(Level.FINE, "No object: '$objectId'${(args != null) ? '(' + Arrays.asList(args).toString() + ')' : ''} found; null is returned")
            }
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

    static Class resolveClass(String className) {
        Class clazz = null
        try {
            clazz = Class.forName(className)
        } catch (Exception ignore) {
            if (logger.isLoggable(Level.FINE)) {
                logger.log(Level.FINE, "Failed to resolve class: '$className'", ignore)
            }
        }
        return clazz
    }

}
