package org.prismus.scrambler.log

import spock.lang.Specification

/**
 * @author Serge Pruteanu
 */
class LineReaderTest extends Specification {

    void 'verify line reader'() {
        given:
        LineReader lineReader = LineReader.toLineReader("""
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
        null != (lineReader = LineReader.toLineReader(new File(LineReaderTest.getResource('/sample-2.log').toURI().toURL().file)))
        null != lineReader.readLine()
        lineReader.close()

        and: 'verify stream read'
        null != (lineReader = LineReader.toLineReader(LineReaderTest.getResource('/sample-2.log').openStream()))
        null != lineReader.readLine()
        lineReader.close()

        and: 'verify Reader read'
        null != (lineReader = LineReader.toLineReader(new InputStreamReader(LineReaderTest.getResource('/sample-2.log').openStream())))
        null != lineReader.readLine()
        lineReader.close()
        try {
            null != lineReader.readLine()
            throw new RuntimeException('An exception should be thrown, reader is closed')
        } catch (IOException ignore) { }

        and: 'verify unknown source'
        try {
            LineReader.toLineReader(new LogEntry(source: new Object()))
            throw new RuntimeException('An exception should be thrown on unknown source')
        } catch (Exception ignore) { }

        and: 'verify log entry reader'
        null != (lineReader = LineReader.toLineReader(new LogEntry(source: new InputStreamReader(LineReaderTest.getResource('/sample-2.log').openStream()))))
        null != lineReader.readLine()
        lineReader.close()
    }

}
