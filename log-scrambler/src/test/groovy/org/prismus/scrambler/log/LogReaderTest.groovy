package org.prismus.scrambler.log

import spock.lang.Specification

/**
 * @author Serge Pruteanu
 */
class LogReaderTest extends Specification {

    void 'verify read line/close'() {
        given:
        final entryReader = IoLogReader.of(new LogContext(), new StringReader("""
line 1
line 2
line 3
"""), 'test-read-line')
        expect: 'lines reading check'
        'test-read-line' == entryReader.source
        '' == entryReader.readLine()
        'line 1' == entryReader.readLine()
        'line 2' == entryReader.readLine()
        'line 3' == entryReader.readLine()
        null == entryReader.readLine()

        and: 'check close'
        entryReader.close()
        null == entryReader.reader
        null == entryReader.currentEntry
    }

}
