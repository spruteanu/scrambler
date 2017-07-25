package org.prismus.scrambler.log

import spock.lang.Specification

/**
 * @author Serge Pruteanu
 */
class ContextBuilderTest extends Specification {

    void 'verify list files'() {
        final folder = new File(ContextBuilderTest.protectionDomain.codeSource.location.path)
        expect:
        0 < ContextBuilder.listFolderFiles(folder).size()
        2 == ContextBuilder.listFolderFiles(folder, '*.log').size()
        1 == ContextBuilder.listFolderFiles(folder, '*sample-1.log').size()
    }

    void 'verify builders'() {
        final folder = new File(ContextBuilderTest.protectionDomain.codeSource.location.path)
        final stringWriter = new StringWriter()
        final listCollector = new ArrayList<LogEntry>()

        given:
        def logContext = new ContextBuilder()
                .log4jSourceFolder(folder, '%5p | %d | %F | %L | %m%n', '*sample-1.log',)
                .dateFormatGroup().messageGroup().endBuilder()
                .csvCollector(stringWriter, Log4jConsumer.PRIORITY, Log4jConsumer.TIMESTAMP, Log4jConsumer.CALLER_FILE_NAME, Log4jConsumer.CALLER_LINE, Log4jConsumer.MESSAGE)
                .withConsumer({ LogEntry logEntry -> listCollector.add(logEntry) })
                .build()
        logContext.consume()

        expect:
        0 < stringWriter.toString().length()
        21 == listCollector.size()

        and: 'verify csv collector columns are populated with groups defined in source consumer'
        null != (logContext = new ContextBuilder()
                .log4jSourceFolder(folder, '%5p | %d | %F | %L | %m%n', '*sample-1.log',).endBuilder()
                .csvCollector(stringWriter)
                .build())
        [Log4jConsumer.PRIORITY, Log4jConsumer.TIMESTAMP,
         Log4jConsumer.CALLER_FILE_NAME, Log4jConsumer.CALLER_LINE,
         Log4jConsumer.MESSAGE] == logContext.consumers.get(0).columns
    }

}
