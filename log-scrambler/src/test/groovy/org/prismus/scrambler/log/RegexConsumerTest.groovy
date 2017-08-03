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
class RegexConsumerTest extends Specification {

    void 'verify date format to regex'() {
        expect:
        '\\w+/\\w+/\\w+ \\w+:\\w+:\\w+.\\w+' == RegexConsumer.dateFormatToRegEx('yyyy/MM/dd HH:mm:ss.SSS')
        '\\w+-\\w+-\\w+ \\w+:\\w+:\\w+.\\w+' == RegexConsumer.dateFormatToRegEx('yyyy-MM-dd HH:mm:ss.SSS')

        '\\w+:\\w+:\\w+.\\w+' == RegexConsumer.dateFormatToRegEx('HH:mm:ss,SSS')
        '\\w+ \\w+ \\w+ \\w+:\\w+:\\w+.\\w+' == RegexConsumer.dateFormatToRegEx('dd MMM yyyy HH:mm:ss,SSS')

        and: 'check converted regex matches date'
        '2008-09-06 10:51:45,473' =~ /${RegexConsumer.dateFormatToRegEx('yyyy-MM-dd HH:mm:ss.SSS')}/
        !('2008-09-06 wrong time' =~ /${RegexConsumer.dateFormatToRegEx('yyyy-MM-dd HH:mm:ss.SSS')}/)

        '15:49:37,459' =~ /${RegexConsumer.dateFormatToRegEx('HH:mm:ss,SSS')}/
        '06 Nov 1994 08:49:37,459' =~ /${RegexConsumer.dateFormatToRegEx('dd MMM yyyy HH:mm:ss,SSS')}/
    }

    void 'verify regex consumer'() {
        LogEntry logEntry = new LogEntry('DEBUG | 2008-09-06 10:51:44,817 | DefaultBeanDefinitionDocumentReader.java | 86 | Loading bean definitions')
        expect: 'verify log line parsed properly'
        RegexConsumer.of(~/(\w+) \| (\w+-\w+-\w+ \w+:\w+:\w+.\w+) \| (\w+\.\w+) \| (\d+) \| (.+)/)
                .group('LogLevel', 1)
                .group('Timestamp', 2)
                .group('Caller', 3)
                .group('Line', 4)
                .group('Message', 5)
                .consume(logEntry)
        false == logEntry.isEmpty()
        'DEBUG' == logEntry.get('LogLevel')
        '2008-09-06 10:51:44,817' == logEntry.get('Timestamp')
        'DefaultBeanDefinitionDocumentReader.java' == logEntry.get('Caller')
        '86' == logEntry.get('Line')
        'Loading bean definitions' == logEntry.get('Message')

        and: 'verify extracted message from log line is processed to extract SQLErrorCodes'
        null != (logEntry = new LogEntry('INFO | 2008-09-06 10:51:45,473 | SQLErrorCodesFactory.java | 128 | SQLErrorCodes loaded: [DB2, Derby, H2, HSQL, Informix, MS-SQL, MySQL, Oracle, PostgreSQL, Sybase]'))
        RegexConsumer.of(~/(\w+) \| (\w+-\w+-\w+ \w+:\w+:\w+.\w+) \| (\w+\.\w+) \| (\d+) \| (.+)/)
                .group('LogLevel', 1)
                .group('Timestamp', 2)
                .group('Caller', 3)
                .group('Line', 4)
                .group('Message', 5)
                .withGroupConsumer('Message', new RegexConsumer(~/.+\[(.+)\]/, 'Message').group('SQLErrorCodes', 1))
                .consume(logEntry)
        false == logEntry.isEmpty()
        'SQLErrorCodes loaded: [DB2, Derby, H2, HSQL, Informix, MS-SQL, MySQL, Oracle, PostgreSQL, Sybase]' == logEntry.get('Message')
        'DB2, Derby, H2, HSQL, Informix, MS-SQL, MySQL, Oracle, PostgreSQL, Sybase' == logEntry.get('SQLErrorCodes')

        and: 'verify parsing log entry with exception'
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
    at org.mortbay.jetty.HttpConnection.consume(HttpConnection.java:404)"""))
        RegexConsumer.of(~/(?ms)(\w+) \| (\w+-\w+-\w+ \w+:\w+:\w+.\w+) \| (\w+\.\w+) \| (\d+) \| (.+)/)
                .group('LogLevel', 1)
                .group('Timestamp', 2)
                .group('Caller', 3)
                .group('Line', 4)
                .group('Message', 5)
                .withGroupConsumer('Message', new RegexConsumer(~/(?ms)(${ExceptionConsumer.EXCEPTION_REGEX})/, 'Message').group('Exception', 1))
                .consume(logEntry)
        false == logEntry.isEmpty()
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
    at org.mortbay.jetty.HttpConnection.consume(HttpConnection.java:404)""" == logEntry.get('Exception')
    }

}
