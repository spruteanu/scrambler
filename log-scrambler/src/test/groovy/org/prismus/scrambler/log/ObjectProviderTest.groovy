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
        null != new DefaultObjectProvider().get(CsvWriterConsumer.name)
        null != new DefaultObjectProvider().get(CsvWriterConsumer.name, new StringWriter(), ['col1', 'col2', 'col3',])

        try {
            null == new DefaultObjectProvider().get('mumu')
            throw new RuntimeException('An exception should be thrown, unknown class')
        } catch (Exception ignore) { }
        null != new DefaultObjectProvider([mumu: CsvWriterConsumer.name]).get('mumu')
        null != new DefaultObjectProvider([mumu: CsvWriterConsumer]).get('mumu')
    }

    void 'verify set instance properties'() {
        final instance = new DefaultObjectProvider().get(CsvWriterConsumer.name) as CsvWriterConsumer
        DefaultObjectProvider.setInstanceProperties(instance, [
                flushAt       : 100,
                columns       : ['t1', 't2', 't3'],
                fieldSeparator: '"'
        ])
        expect:
        100 == instance.flushAt
        ['t1', 't2', 't3'] == instance.columns
        '"' == instance.fieldSeparator

        and: 'verify unknown property setting'
        try {
            DefaultObjectProvider.setInstanceProperties(instance, [mumu: 'is alive'])
            throw new RuntimeException('An exception should be thrown, wrong field is defined')
        } catch (Exception ignore) { }
    }

    void 'verify spring definition provider'() {
        final provider = new SpringObjectProvider(new AnnotationConfigApplicationContext(SpringConfig))

        expect:
        null != provider.get(CsvWriterConsumer.name)
        null != provider.get(CsvWriterConsumer.name, new StringWriter(), ['col1', 'col2', 'col3',])
        null != provider.get('mumu')
        try {
            null == provider.get('cucu')
            throw new RuntimeException('An exception should be thrown, unknown class')
        } catch (Exception ignore) { }

        null != SpringObjectProvider.of()
        null != SpringObjectProvider.of(SpringConfig)
        null != SpringObjectProvider.of(new AnnotationConfigApplicationContext(SpringConfig))
    }

    @Configuration
    static class SpringConfig {
        @Bean(name = 'mumu')
        CsvWriterConsumer csvBean() {
            return new CsvWriterConsumer()
        }
    }

}
