package org.prismus.scrambler.log

import groovy.transform.CompileStatic
import org.codehaus.groovy.runtime.DefaultGroovyMethods

import java.util.logging.Level
import java.util.logging.Logger
import java.util.regex.Pattern

/**
 * @author Serge Pruteanu
 */
@CompileStatic
class DefaultProcessorProvider implements ProcessorProvider {
    private static final Logger logger = Logger.getLogger(DefaultProcessorProvider.class.getName())
    private static final Pattern CLASS_PATTERN = ~/([a-zA-Z_$][a-zA-Z\d_$]*\.)*[a-zA-Z_$][a-zA-Z\d_$]*/

    Map<String, Object> processorIdClassMap = [:]

    DefaultProcessorProvider() {
    }

    DefaultProcessorProvider(Map<String, Object> processorIdClassMap) {
        this.processorIdClassMap = processorIdClassMap
    }

    @Override
    EntryProcessor get(String processorId, Object... args) {
        Class clazz = null
        def clazzObj = processorIdClassMap.get(processorId)
        if (clazzObj instanceof Class) {
            clazz = clazzObj
        } else {
            if (clazzObj) {
                if (isClassName(clazzObj.toString())) {
                    clazz = resolveClass(clazzObj.toString())
                }
            }
        }
        if (clazz == null && isClassName(processorId)) {
            clazz = resolveClass(processorId)
        }

        EntryProcessor processor = null
        if (clazz) {
            try {
                processor = DefaultGroovyMethods.newInstance(clazz, args) as EntryProcessor
            } catch (Exception ignore) {
                logger.log(Level.SEVERE, "Failed to get processor: '$processorId'${(args != null) ? '(' + Arrays.asList(args).toString() + ')' : ''}; null is returned", ignore)
            }
        } else {
            if (logger.isLoggable(Level.FINE)) {
                logger.log(Level.FINE, "No processor: '$processorId'${(args != null) ? '(' + Arrays.asList(args).toString() + ')' : ''} found; null is returned")
            }
        }
        return processor
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
