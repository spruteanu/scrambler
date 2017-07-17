package org.prismus.scrambler.log

import spock.lang.Specification

/**
 * @author Serge Pruteanu
 */
class LogReaderConsumerTest extends Specification {

    void 'verify line reader'() {
        given:
        LineReader lineReader = LogReaderConsumer.toLineReader("""
line 1
line 2
line 3
""")
        expect: 'lines reading check'
        '' == lineReader.readLine()
        'line 1' == lineReader.readLine()
        'line 2' == lineReader.readLine()
        'line 3' == lineReader.readLine()
        null == lineReader.readLine()
        lineReader.close()

        and: 'verify file read'
        null != (lineReader = LogReaderConsumer.toLineReader(new File(LogReaderConsumerTest.getResource('/sample-2.log').toURI().toURL().file)))
        null != lineReader.readLine()
        lineReader.close()

        and: 'verify stream read'
        null != (lineReader = LogReaderConsumer.toLineReader(LogReaderConsumerTest.getResource('/sample-2.log').openStream()))
        null != lineReader.readLine()
        lineReader.close()

        and: 'verify Reader read'
        null != (lineReader = LogReaderConsumer.toLineReader(new InputStreamReader(LogReaderConsumerTest.getResource('/sample-2.log').openStream())))
        null != lineReader.readLine()
        lineReader.close()
        try {
            null != lineReader.readLine()
            throw new RuntimeException('An exception should be thrown, reader is closed')
        } catch (IOException ignore) { }

        and: 'verify unknown source'
        try {
            LogReaderConsumer.toLineReader(new LogEntry(source: new Object()))
            throw new RuntimeException('An exception should be thrown on unknown source')
        } catch (Exception ignore) { }

        and: 'verify log entry reader'
        null != (lineReader = LogReaderConsumer.toLineReader(new LogEntry(source: new InputStreamReader(LogReaderConsumerTest.getResource('/sample-2.log').openStream()))))
        null != lineReader.readLine()
        lineReader.close()
    }

    void 'verify log reader consumer'() {
        expect:
        // todo Serge: implement me
        true
    }
}
