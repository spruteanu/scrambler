package org.prismus.scrambler.log

import spock.lang.Specification

/**
 * @author Serge Pruteanu
 */
class LogContextTest extends Specification {

    void 'verify list files'() {
        final folder = new File(LogContextTest.protectionDomain.codeSource.location.path)
        expect:
        0 < LogContext.listFolderFiles(folder).size()
        2 == LogContext.listFolderFiles(folder, '*.log').size()
        1 == LogContext.listFolderFiles(folder, '*sample-1.log').size()
    }

    void 'verify builders'() {
        final folder = new File(LogContextTest.protectionDomain.codeSource.location.path)
        final stringWriter = new StringWriter()
        final listCollector = new ArrayList<LogEntry>()

        given:
        def logContext = new LogContext.Builder()
                .log4jSourceFolder(folder, '%5p | %d | %F | %L | %m%n', '*sample-1.log',)
                .dateFormatGroup().messageGroup().endBuilder()
                .csvCollector(stringWriter, Log4jConsumer.PRIORITY, Log4jConsumer.TIMESTAMP, Log4jConsumer.CALLER_FILE_NAME, Log4jConsumer.CALLER_LINE, Log4jConsumer.MESSAGE)
                .withConsumer({ LogEntry logEntry -> listCollector.add(logEntry) })
                .build()
        logContext.consume()

        expect:
        0 < stringWriter.toString().length()
        21 == listCollector.size()
        null != listCollector[20].getLogValue(MessageConsumer.ERROR_MESSAGE)
        null != listCollector[20].getLogValue(MessageConsumer.EXCEPTION)

        and: 'verify csv collector columns are populated with groups defined in source consumer'
        null != (logContext = new LogContext.Builder()
                .log4jSourceFolder(folder, '%5p | %d | %F | %L | %m%n', '*sample-1.log',).endBuilder()
                .csvCollector(stringWriter)
                .build())
        [Log4jConsumer.PRIORITY, Log4jConsumer.TIMESTAMP,
         Log4jConsumer.CALLER_FILE_NAME, Log4jConsumer.CALLER_LINE,
         Log4jConsumer.MESSAGE] == logContext.consumers.get(0).columns
    }

}
