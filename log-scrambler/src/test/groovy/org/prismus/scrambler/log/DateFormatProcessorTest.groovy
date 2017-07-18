package org.prismus.scrambler.log

import spock.lang.Specification

/**
 * @author Serge Pruteanu
 */
class DateFormatProcessorTest extends Specification {

    void 'verify date value processor'() {
        final processor = Log4jConsumer.forPattern('%5p | %d | %F | %L | %m%n')
                .timestampProcessor(Log4jConsumer.ISO8601_DATE_FORMAT)

        final entry = new LogEntry('ERROR | 2008-09-06 10:51:45,473 | SQLErrorCodesFactory.java | 128 | OMG, Something bad happened')
        processor.process(entry)

        expect:
        Date.isInstance(entry.getLogValue(Log4jConsumer.TIMESTAMP))
    }

}
