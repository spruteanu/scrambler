package org.prismus.scrambler.log

import org.springframework.context.annotation.AnnotationConfigApplicationContext
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import spock.lang.Specification

/**
 * @author Serge Pruteanu
 */
class ProcessorProviderTest extends Specification {

    void 'verify default definition provider'() {
        expect:
        true == DefaultProcessorProvider.isClassName(DefaultProcessorProvider.class.name)
        null != new DefaultProcessorProvider().get(CsvOutputProcessor.name)
        null != new DefaultProcessorProvider().get(CsvOutputProcessor.name, new StringWriter(), ['col1', 'col2', 'col3',])

        null == new DefaultProcessorProvider().get('mumu')
        null != new DefaultProcessorProvider([mumu: CsvOutputProcessor.name]).get('mumu')
        null != new DefaultProcessorProvider([mumu: CsvOutputProcessor]).get('mumu')
    }

    void 'verify spring definition provider'() {
        final provider = new SpringProcessorProvider(new AnnotationConfigApplicationContext(SpringConfig))

        expect:
        null != provider.get(CsvOutputProcessor.name)
        null != provider.get(CsvOutputProcessor.name, new StringWriter(), ['col1', 'col2', 'col3',])
        null != provider.get('mumu')
        null == provider.get('cucu')

        null != SpringProcessorProvider.of()
        null != SpringProcessorProvider.of(SpringConfig)
        null != SpringProcessorProvider.of(new AnnotationConfigApplicationContext(SpringConfig))
    }

    @Configuration
    static class SpringConfig {

        @Bean(name = 'mumu')
        CsvOutputProcessor csvBean() {
            return new CsvOutputProcessor()
        }

    }

}
