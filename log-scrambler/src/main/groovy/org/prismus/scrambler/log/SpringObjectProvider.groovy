package org.prismus.scrambler.log

import groovy.transform.CompileStatic
import org.springframework.context.ApplicationContext
import org.springframework.context.annotation.AnnotationConfigApplicationContext
import org.springframework.context.support.ClassPathXmlApplicationContext

/**
 * @author Serge Pruteanu
 */
@CompileStatic
class SpringObjectProvider implements ObjectProvider {
    ApplicationContext context

    SpringObjectProvider() {
    }

    SpringObjectProvider(ApplicationContext context) {
        this.context = context
    }

    @Override
    Object get(Object objectId, Object... args) {
        Object object = null
        try {
            if (objectId instanceof Class) {
                object = context.getBean(objectId as Class, args)
            } else {
                String strId = objectId.toString()
                if (context.containsBean(strId)) {
                    object = context.getBean(objectId.toString(), args)
                } else if (DefaultObjectProvider.isClassName(strId)) {
                    object = context.getBean(Class.forName(strId), args)
                }
            }
            if (object) {
                context.autowireCapableBeanFactory.autowireBean(object)
            } else {
                throw new IllegalArgumentException("No object: '$objectId'${(args != null) ? '(' + Arrays.asList(args).toString() + ')' : ''} found")
            }
        } catch (Exception e) {
            throw new RuntimeException("Failed to get object: '$objectId'${(args != null) ? '(' + Arrays.asList(args).toString() + ')' : ''}", e)
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
