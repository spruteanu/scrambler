package org.prismus.scrambler.log

import groovy.transform.CompileStatic
import org.springframework.context.ApplicationContext
import org.springframework.context.annotation.AnnotationConfigApplicationContext
import org.springframework.context.support.ClassPathXmlApplicationContext

import java.util.logging.Level
import java.util.logging.Logger

/**
 * @author Serge Pruteanu
 */
@CompileStatic
class SpringObjectProvider implements ObjectProvider {
    private  static final Logger logger = Logger.getLogger(SpringObjectProvider.class.getName())

    ApplicationContext context

    SpringObjectProvider() {
    }

    SpringObjectProvider(ApplicationContext context) {
        this.context = context
    }

    @Override
    Object get(String objectId, Object... args) {
        Object object = null
        try {
            if (context.containsBean(objectId)) {
                object = context.getBean(objectId, args)
            } else {
                if (DefaultObjectProvider.isClassName(objectId)) {
                    object = context.getBean(DefaultObjectProvider.resolveClass(objectId), args)
                }
            }
            if (object) {
                context.autowireCapableBeanFactory.autowireBean(object)
            }
        } catch (Exception ignore) {
            logger.log(Level.SEVERE, "Failed to get object: '$objectId'${(args != null) ? '(' + Arrays.asList(args).toString() + ')' : ''}; null is returned", ignore)
        }
        return object
    }

    static SpringObjectProvider of(ApplicationContext context) {
        return new SpringObjectProvider(context)
    }

    static SpringObjectProvider of(Class... contextClass) {
        return contextClass != null ? new SpringObjectProvider(new AnnotationConfigApplicationContext(contextClass)) : new SpringObjectProvider()
    }

    static SpringObjectProvider of(String... contextXml) {
        return new SpringObjectProvider(new ClassPathXmlApplicationContext(contextXml))
    }

}
