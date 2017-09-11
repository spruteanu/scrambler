/*
 * Log crawler, tool that allows to extract/crawl log files for further analysis
 *
 * Copyright (c) 2015, Sergiu Prutean. All rights reserved.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3.0 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library.
 */

package org.prismus.scrambler.log

import spock.lang.Specification

/**
 * @author Serge Pruteanu
 */
class Log4jConsumerTest extends Specification {

    void 'verify conversion of non specifier char'() {
        final sb = new StringBuilder()
        Log4jConsumer.appendNonSpecifierChar(sb, ch as char)

        expect:
        expected == sb.toString()

        where:
        ch << ['[', ']', '{', '}', '\\', '^', '$', '|', '?', '*', '+', '(', ')', '#', 'a', 'f', 'c', 's']
        expected << ['\\[', '\\]', '\\{', '\\}', '\\\\', '\\^', '\\$', '\\|', '\\?', '\\*', '\\+', '\\(', '\\)', '#', 'a', 'f', 'c', 's']
    }

    void 'verify precision specifier to regex'() {
        final sb = new StringBuilder()
        int idx = Log4jConsumer.specifierToRegex(new Log4jConsumer(), sb, '%' as char, 0, specString)

        expect:
        expected == sb.toString()
        idx == specString.length() - 1

        where:
        specString << ['%20c', '%-20c', '%.30c', '%20.30c', '%-20.30c', '%r',
                       '%d', '%d{dd MMM yyyy HH:mm:ss,SSS}',
                       '%d{ABSOLUTE}', '%d{DATE}',
                       '%d{ISO8601}',
        ]
        expected << ['([a-zA-Z$_\\\\.\\d\\\\/ ]{20,})', '(\\s*[a-zA-Z$_\\\\.\\d\\\\/ ]{20,})', '([a-zA-Z$_\\\\.\\d\\\\/ ]{1,30})', '([a-zA-Z$_\\\\.\\d\\\\/ ]{20,30})', '(\\s*[a-zA-Z$_\\\\.\\d\\\\/ ]{20,30})', '([\\d^ ]+)',
                     '(\\w+-\\w+-\\w+ \\w+:\\w+:\\w+.\\w+)', '(\\w+ \\w+ \\w+ \\w+:\\w+:\\w+.\\w+)',
                     '(\\w+:\\w+:\\w+.\\w+)', '(\\w+ \\w+ \\w+ \\w+:\\w+:\\w+.\\w+)',
                     '(\\w+-\\w+-\\w+ \\w+:\\w+:\\w+.\\w+)',
        ]
    }

    void 'verify conversionPatternToRegEx'() {
        final consumer = new Log4jConsumer()
        String patternRegEx = Log4jConsumer.conversionPatternToRegex(consumer, specString)

        expect:
        expected == patternRegEx

        where:
        specString << [
                '%-6r [%15.15t] %-5p %30.30c %x - %m%n',
                '%r [%t] %-5p %c %x - %m%n',
                '%5p | %d | %F | %L | %m%n',
        ]
        expected << [
                '(\\s*[\\d^ ]{6,}) \\[(.{15,15})\\] (\\s*[\\w ]{5,}) ([a-zA-Z$_\\\\.\\d\\\\/ ]{30,30}) ([\\w\\W]*) - (.+)',
                '([\\d^ ]+) \\[(.+)\\] (\\s*[\\w ]{5,}) ([a-zA-Z$_\\\\.\\d\\\\/ ]+) ([\\w\\W]*) - (.+)',
                '([\\w ]{5,}) \\| (\\w+-\\w+-\\w+ \\w+:\\w+:\\w+.\\w+) \\| ([\\w\\W]+) \\| ([\\d^ ]+) \\| (.+)',
        ]
    }

    void 'verify conversionPatternToRegEx with log entry parsing'() {
        final conversionPattern = '%-4r [%t] %-5p %C %x - %m%n'
        final patternRegEx = Log4jConsumer.conversionPatternToRegex(new Log4jConsumer(), conversionPattern)
        LogEntry logEntry = new LogEntry('0    [main] DEBUG com.vaannila.helloworld.HelloWorld  - Sample debug message')
        Log4jConsumer.of(~/${patternRegEx}/)
                .group('logTime', 1)
                .group('ThreadName', 2)
                .group('LogLevel', 3)
                .group('CallerClass', 4)
                .group('NDC', 5)
                .group('Message', 6)
                .consume(logEntry)

        expect:
        '0' == logEntry.get('logTime')
        'main' == logEntry.get('ThreadName')
        'DEBUG' == logEntry.get('LogLevel')
        'com.vaannila.helloworld.HelloWorld' == logEntry.get('CallerClass')
        null == logEntry.get('NDC')
        'Sample debug message' == logEntry.get('Message')

        and: 'with exception'
        null != (logEntry = new LogEntry("""ERROR | 2008-09-06 10:51:45,473 | SQLErrorCodesFactory.java | 128 | OMG, Something bad happened
javax.servlet.ServletException: Something bad happened
    at com.example.myproject.OpenSessionInViewFilter.doFilter(OpenSessionInViewFilter.java:60)
    at com.example.myproject.ExceptionHandlerFilter.doFilter(ExceptionHandlerFilter.java:28)
    at com.example.myproject.OutputBufferFilter.doFilter(OutputBufferFilter.java:33)
    at org.mortbay.jetty.servlet.ServletHandler.consume(ServletHandler.java:388)
    at org.mortbay.jetty.security.SecurityHandler.consume(SecurityHandler.java:216)
    at org.mortbay.jetty.servlet.SessionHandler.consume(SessionHandler.java:182)
    at org.mortbay.jetty.handler.ContextHandler.consume(ContextHandler.java:765)
    at org.mortbay.jetty.webapp.WebAppContext.consume(WebAppContext.java:418)
    at org.mortbay.jetty.handler.HandlerWrapper.consume(HandlerWrapper.java:152)
    at org.mortbay.jetty.Server.consume(Server.java:326)
    at org.mortbay.jetty.HttpConnection.handleRequest(HttpConnection.java:542)
    at org.mortbay.jetty.HttpParser.parseNext(HttpParser.java:756)
    at org.mortbay.jetty.HttpParser.parseAvailable(HttpParser.java:218)
    at org.mortbay.jetty.HttpConnection.consume(HttpConnection.java:404)
"""))
        Log4jConsumer.of(~/(?ms)${Log4jConsumer.conversionPatternToRegex(new Log4jConsumer(), '%5p | %d | %F | %L | %m%n')}/)
                .groups('LogLevel', 'Timestamp', 'CallerFileName', 'Line', 'Message')
                .consume(logEntry)
        false == logEntry.isEmpty()
        'ERROR' == logEntry.get('LogLevel')
        '2008-09-06 10:51:45,473' == logEntry.get('Timestamp')
        'SQLErrorCodesFactory.java' == logEntry.get('CallerFileName')
        '128' == logEntry.get('Line')

        """OMG, Something bad happened
javax.servlet.ServletException: Something bad happened
    at com.example.myproject.OpenSessionInViewFilter.doFilter(OpenSessionInViewFilter.java:60)
    at com.example.myproject.ExceptionHandlerFilter.doFilter(ExceptionHandlerFilter.java:28)
    at com.example.myproject.OutputBufferFilter.doFilter(OutputBufferFilter.java:33)
    at org.mortbay.jetty.servlet.ServletHandler.consume(ServletHandler.java:388)
    at org.mortbay.jetty.security.SecurityHandler.consume(SecurityHandler.java:216)
    at org.mortbay.jetty.servlet.SessionHandler.consume(SessionHandler.java:182)
    at org.mortbay.jetty.handler.ContextHandler.consume(ContextHandler.java:765)
    at org.mortbay.jetty.webapp.WebAppContext.consume(WebAppContext.java:418)
    at org.mortbay.jetty.handler.HandlerWrapper.consume(HandlerWrapper.java:152)
    at org.mortbay.jetty.Server.consume(Server.java:326)
    at org.mortbay.jetty.HttpConnection.handleRequest(HttpConnection.java:542)
    at org.mortbay.jetty.HttpParser.parseNext(HttpParser.java:756)
    at org.mortbay.jetty.HttpParser.parseAvailable(HttpParser.java:218)
    at org.mortbay.jetty.HttpConnection.consume(HttpConnection.java:404)""" == logEntry.get('Message')
    }

    void 'verify log4j entry consumer'() {
        final consumer = Log4jConsumer.of('%-4r [%t] %-5p %C %x - %m%n')
        final logEntry = new LogEntry('0    [main] DEBUG com.vaannila.helloworld.HelloWorld  - Sample debug message')

        expect:
        consumer.consume(logEntry)
        [
                (Log4jConsumer.LOGGING_DURATION): '0',
                (Log4jConsumer.THREAD_NAME)     : 'main',
                (Log4jConsumer.PRIORITY)        : 'DEBUG',
                (Log4jConsumer.CALLER_CLASS)    : 'com.vaannila.helloworld.HelloWorld',
                (Log4jConsumer.MESSAGE)         : 'Sample debug message',
        ] == logEntry.logValueMap
    }

    void 'verify extract log4j consumer properties from log4j configuration'() {
        given:
        final log4jConsumerProperties = Log4jConsumer.extractLog4jConsumerProperties("""# Root logger option
log4j.rootLogger=INFO, file, stdout

# Direct log messages to a log file
log4j.appender.file=org.apache.log4j.RollingFileAppender
log4j.appender.file.File=log-file.log
log4j.appender.file.MaxFileSize=10MB
log4j.appender.file.MaxBackupIndex=10
log4j.appender.file.layout=org.apache.log4j.PatternLayout
log4j.appender.file.layout.ConversionPattern=%d{yyyy-MM-dd HH:mm:ss} %-5p %c{1}:%L - %m%n

# Direct log messages to stdout
log4j.appender.stdout=org.apache.log4j.ConsoleAppender
log4j.appender.stdout.Target=System.out
log4j.appender.stdout.layout=org.apache.log4j.PatternLayout
log4j.appender.stdout.layout.ConversionPattern=%d{yyyy-MM-dd HH:mm:ss} %-5p %c{1}:%L - %m%n

log4j.appender.file1=org.apache.log4j.RollingFileAppender
log4j.appender.file1.File=log-file1.log
log4j.appender.file1.MaxFileSize=10MB
log4j.appender.file1.MaxBackupIndex=10
log4j.appender.file1.layout=org.apache.log4j.PatternLayout
log4j.appender.file1.layout.ConversionPattern=%d %5p %37c - %m%n

log4j.appender.file2=org.apache.log4j.RollingFileAppender
log4j.appender.file2.File=log-file2.log
log4j.appender.file2.MaxFileSize=10MB
log4j.appender.file2.MaxBackupIndex=10
log4j.appender.file2.layout=org.apache.log4j.PatternLayout
log4j.appender.file2.layout.ConversionPattern=%d %5p %37c - %m%n

log4j.appender.file3=org.apache.log4j.RollingFileAppender
log4j.appender.file3.File=log-file3.log
log4j.appender.file3.MaxFileSize=10MB
log4j.appender.file3.MaxBackupIndex=10
log4j.appender.file3.layout=org.apache.log4j.PatternLayout
log4j.appender.file3.layout.ConversionPattern=%d - %p - %m%n

log4j.logger.sample3=TRACE, sample3
log4j.appender.sample3=org.apache.log4j.RollingFileAppender
log4j.appender.sample3.File=target/sample-3.log
log4j.appender.sample3.MaxFileSize=10MB
log4j.appender.sample3.MaxBackupIndex=10
log4j.appender.sample3.layout=org.apache.log4j.PatternLayout
log4j.appender.sample3.layout.ConversionPattern=%d %5p %c [%t] - %m%n
""".readLines())
        final filterConversionMap = Log4jConsumer.toLog4jFileConversionPattern(log4jConsumerProperties)

        expect:
        ["log-file.log*" : "%d{yyyy-MM-dd HH:mm:ss} %-5p %c{1}:%L - %m%n",
         "log-file1.log*": "%d %5p %37c - %m%n",
         "log-file2.log*": "%d %5p %37c - %m%n",
         "log-file3.log*": "%d - %p - %m%n",
         "sample-3.log*" : "%d %5p %c [%t] - %m%n",
        ] == filterConversionMap
    }

}
