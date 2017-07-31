package org.prismus.scrambler.log

import spock.lang.Specification

/**
 * @author Serge Pruteanu
 */
class DateConsumerTest extends Specification {

    void 'verify date value consumer'() {
        final processor = Log4jConsumer.of('%5p | %d | %F | %L | %m%n')
                .withDateConsumer(Log4jConsumer.ISO8601_DATE_FORMAT)

        final entry = new LogEntry('ERROR | 2008-09-06 10:51:45,473 | SQLErrorCodesFactory.java | 128 | OMG, Something bad happened')
        processor.consume(entry)

        expect:
        Date.isInstance(entry.get(Log4jConsumer.DATE))
    }

}
