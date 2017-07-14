package org.prismus.scrambler.log

import spock.lang.Specification

/**
 * @author Serge Pruteanu
 */
class DateValueProcessorTest extends Specification {

    void 'verify date value processor'() {
        final processor = Log4jEntryProcessor.ofPattern('%5p | %d | %F | %L | %m%n')
                .timestampProcessor(Log4jEntryProcessor.ISO8601_DATE_FORMAT)

        final entry = new LogEntry('ERROR | 2008-09-06 10:51:45,473 | SQLErrorCodesFactory.java | 128 | OMG, Something bad happened')
        processor.process(entry)

        expect:
        Date.isInstance(entry.getEntryValue(Log4jEntryProcessor.TIMESTAMP))
    }

}
