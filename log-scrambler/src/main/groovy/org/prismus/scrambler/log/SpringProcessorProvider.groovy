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
class SpringProcessorProvider implements ProcessorProvider {
    private  static final Logger logger = Logger.getLogger(SpringProcessorProvider.class.getName())

    ApplicationContext context

    SpringProcessorProvider() {
    }

    SpringProcessorProvider(ApplicationContext context) {
        this.context = context
    }

    @Override
    EntryProcessor get(String processorId, Object... args) {
        EntryProcessor processor = null
        try {
            if (context.containsBean(processorId)) {
                processor = context.getBean(processorId, args) as EntryProcessor
            } else {
                if (DefaultProcessorProvider.isClassName(processorId)) {
                    processor = context.getBean(DefaultProcessorProvider.resolveClass(processorId), args) as EntryProcessor
                }
            }
        } catch (Exception ignore) {
            logger.log(Level.SEVERE, "Failed to get processor: '$processorId'${(args != null) ? '(' + Arrays.asList(args).toString() + ')' : ''}; null is returned", ignore)
        }
        return processor
    }

    static SpringProcessorProvider of(ApplicationContext context) {
        return new SpringProcessorProvider(context)
    }

    static SpringProcessorProvider of(Class... contextClass) {
        return contextClass != null ? new SpringProcessorProvider(new AnnotationConfigApplicationContext(contextClass)) : new SpringProcessorProvider()
    }

    static SpringProcessorProvider of(String... contextXml) {
        return new SpringProcessorProvider(new ClassPathXmlApplicationContext(contextXml))
    }

}
