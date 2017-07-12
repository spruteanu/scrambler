package org.prismus.scrambler.log

import spock.lang.Specification

/**
 * Created by SergeP on 7/11/2017.
 */
class EntryReaderTest extends Specification {

    void 'verify read line/close'() {
        given:
        final entryReader = IoEntryReader.of(new LogContext(), new StringReader("""
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
