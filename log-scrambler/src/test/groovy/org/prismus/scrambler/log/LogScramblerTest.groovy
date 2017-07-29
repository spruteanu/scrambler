package org.prismus.scrambler.log

import spock.lang.Specification

/**
 * @author Serge Pruteanu
 */
class LogScramblerTest extends Specification {

    void 'verify list files'() {
        final folder = new File(LogScramblerTest.protectionDomain.codeSource.location.path)
        expect:
        0 < LogScrambler.listFolderFiles(folder).size()
        2 == LogScrambler.listFolderFiles(folder, '*.log').size()
        1 == LogScrambler.listFolderFiles(folder, '*sample-1.log').size()
    }

    void 'verify builders'() {
        final folder = new File(LogScramblerTest.protectionDomain.codeSource.location.path)
        final stringWriter = new StringWriter()
        final listCollector = new ArrayList<LogEntry>()

        given:
        def logContext = new LogScrambler.Builder()
                .log4jSourceFolder(folder, '%5p | %d | %F | %L | %m%n', '*sample-1.log',)
                .withDateConsumer().withMessageExceptionConsumer().recurContext()
                .csvWriter(stringWriter, Log4jConsumer.PRIORITY, Log4jConsumer.DATE, Log4jConsumer.CALLER_FILE_NAME, Log4jConsumer.CALLER_LINE, Log4jConsumer.MESSAGE)
                .withConsumer({ LogEntry logEntry -> listCollector.add(logEntry) })
                .build()
        logContext.consume()

        expect:
        0 < stringWriter.toString().length()
        21 == listCollector.size()
        null != listCollector[20].getLogValue(MessageExceptionConsumer.ERROR_MESSAGE)
        null != listCollector[20].getLogValue(MessageExceptionConsumer.EXCEPTION)

        and: 'verify csv collector columns are populated with groups defined in source consumer'
        null != (logContext = new LogScrambler.Builder()
                .log4jSourceFolder(folder, '%5p | %d | %F | %L | %m%n', '*sample-1.log',).recurContext()
                .csvWriter(stringWriter)
                .build())
        [Log4jConsumer.PRIORITY, Log4jConsumer.DATE,
         Log4jConsumer.CALLER_FILE_NAME, Log4jConsumer.CALLER_LINE,
         Log4jConsumer.MESSAGE] == logContext.consumers.get(0).columns
    }

    void 'log entry iterator from one/multiple source(s)'() {
        final folder = new File(LogScramblerTest.protectionDomain.codeSource.location.path)
        final stringWriter = new StringWriter()

        given:
        def logContext = new LogScrambler.Builder()
                .log4jSourceFolder(folder, '%5p | %d | %F | %L | %m%n', '*sample-1.log',)
                .withDateConsumer().withMessageExceptionConsumer().recurContext()
                .csvWriter(stringWriter, Log4jConsumer.PRIORITY, Log4jConsumer.DATE, Log4jConsumer.CALLER_FILE_NAME, Log4jConsumer.CALLER_LINE, Log4jConsumer.MESSAGE)
                .build()
        def iterator = logContext.iterator()
        List result = iterator.toList()

        expect:
        0 < stringWriter.toString().length()
        21 == result.size()
        null != result[20].getLogValue(MessageExceptionConsumer.ERROR_MESSAGE)
        null != result[20].getLogValue(MessageExceptionConsumer.EXCEPTION)

        and: 'verify multiple sources iterator'
        null != (logContext = new LogScrambler.Builder()
                .log4jSourceFolder(folder, '%5p | %d | %F | %L | %m%n', '*sample-1.log',).recurContext()
                .csvWriter(stringWriter, Log4jConsumer.PRIORITY, Log4jConsumer.DATE, Log4jConsumer.CALLER_FILE_NAME, Log4jConsumer.CALLER_LINE, Log4jConsumer.MESSAGE)
                .log4jSourceFolder(folder, '%-4r [%t] %-5p %c %x - %m%n', '*sample-2.log',).recurContext()
                .csvWriter(stringWriter, Log4jConsumer.PRIORITY, Log4jConsumer.DATE, Log4jConsumer.CALLER_FILE_NAME, Log4jConsumer.CALLER_LINE, Log4jConsumer.MESSAGE)
                .build())
        ['sample-1.log', 'sample-2.log'] == logContext.sourceConsumerMap.keySet().collect { LineReader.getSourceName(it)}.sort()
        null != (iterator = logContext.iterator())
        null != (result = iterator.toList())
        29 == result.size()
    }

    void 'parse log entries using groovy script definition'() {
        given:
        final logContext = LogScrambler.builder('/sample-log.groovy').build()
        def iterator = logContext.iterator()

        expect:
        29 == iterator.toList().size()
    }

    void 'parse log entries using log4j config file'() {
        given:
        final folder = new File(LogScramblerTest.protectionDomain.codeSource.location.path)
        final builder = LogScrambler.builder().log4jConfigSource(folder, '/log4j.properties')

        expect: 'verify registered builders'
        null != builder.getLog4jBuilder('sample1')
        null != builder.getLog4jBuilder('%5p | %d | %F | %L | %m%n')
        null != builder.getLog4jBuilder('sample-1.log*')

        null != builder.getLog4jBuilder('sample2')
        null != builder.getLog4jBuilder('%-4r [%t] %-5p %c %x - %m%n')
        null != builder.getLog4jBuilder('sample-2.log*')

        and: 'verify no sources found cause there is no such log source file'
        null == builder.getLog4jBuilder('sample3')
        null == builder.getLog4jBuilder('%d %5p %c [%t] - %m%n')
        null == builder.getLog4jBuilder('sample-3.log*')

        and: 'verify context is consumed properly'
        29 == builder.build().iterator().toList().size()
    }

}
