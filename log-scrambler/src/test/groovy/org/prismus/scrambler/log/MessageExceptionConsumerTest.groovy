package org.prismus.scrambler.log

import spock.lang.Specification

/**
 * @author Serge Pruteanu
 */
class MessageExceptionConsumerTest extends Specification {

    void 'verify message consumer'() {
        given:
        final consumer = new MessageExceptionConsumer('test')
        final logEntry = new LogEntry().putLogValue('test', '''Sample fatal message
java.lang.NullPointerException:
    at com.example.myproject.Book.getTitle(Book.java:16)
    at com.example.myproject.Author.getBookTitles(Author.java:25)
    at com.example.myproject.Bootstrap.main(Bootstrap.java:14)
''')
        consumer.consume(logEntry)

        expect:
        'Sample fatal message' == logEntry.getLogValue(MessageExceptionConsumer.ERROR_MESSAGE)
        '''java.lang.NullPointerException:
    at com.example.myproject.Book.getTitle(Book.java:16)
    at com.example.myproject.Author.getBookTitles(Author.java:25)
    at com.example.myproject.Bootstrap.main(Bootstrap.java:14)
''' == logEntry.getLogValue(MessageExceptionConsumer.EXCEPTION)
    }

    void 'verify message consumer different line breaks /windows/linux/macos/'() {
        given:
        final lines = ['Sample fatal message',
                       'java.lang.NullPointerException:',
                       '    at com.example.myproject.Book.getTitle(Book.java:16)',
                       '    at com.example.myproject.Author.getBookTitles(Author.java:25)',
                       '    at com.example.myproject.Bootstrap.main(Bootstrap.java:14)',]
        final consumer = new MessageExceptionConsumer('test')

        final logEntry = new LogEntry().putLogValue('test', lines.join(lineBreak))
        consumer.consume(logEntry)

        expect: "verify $os line endings"
        'Sample fatal message' == logEntry.getLogValue(MessageExceptionConsumer.ERROR_MESSAGE)
        lines.subList(1, lines.size()).join(lineBreak) == logEntry.getLogValue(MessageExceptionConsumer.EXCEPTION)

        where:
        os << ['Windows', 'Linux', 'Macos']
        lineBreak << ['\r\n', '\n', '\r']
    }

}
