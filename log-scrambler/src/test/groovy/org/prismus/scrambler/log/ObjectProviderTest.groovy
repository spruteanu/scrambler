package org.prismus.scrambler.log

import org.springframework.context.annotation.AnnotationConfigApplicationContext
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import spock.lang.Specification

/**
 * @author Serge Pruteanu
 */
class ObjectProviderTest extends Specification {

    void 'verify default definition provider'() {
        expect:
        true == DefaultObjectProvider.isClassName(DefaultObjectProvider.class.name)
        null != new DefaultObjectProvider().get(CsvOutputProcessor.name)
        null != new DefaultObjectProvider().get(CsvOutputProcessor.name, new StringWriter(), ['col1', 'col2', 'col3',])

        null == new DefaultObjectProvider().get('mumu')
        null != new DefaultObjectProvider([mumu: CsvOutputProcessor.name]).get('mumu')
        null != new DefaultObjectProvider([mumu: CsvOutputProcessor]).get('mumu')
    }

    void 'verify spring definition provider'() {
        final provider = new SpringObjectProvider(new AnnotationConfigApplicationContext(SpringConfig))

        expect:
        null != provider.get(CsvOutputProcessor.name)
        null != provider.get(CsvOutputProcessor.name, new StringWriter(), ['col1', 'col2', 'col3',])
        null != provider.get('mumu')
        null == provider.get('cucu')

        null != SpringObjectProvider.of()
        null != SpringObjectProvider.of(SpringConfig)
        null != SpringObjectProvider.of(new AnnotationConfigApplicationContext(SpringConfig))
    }

    @Configuration
    static class SpringConfig {

        @Bean(name = 'mumu')
        CsvOutputProcessor csvBean() {
            return new CsvOutputProcessor()
        }

    }

}
