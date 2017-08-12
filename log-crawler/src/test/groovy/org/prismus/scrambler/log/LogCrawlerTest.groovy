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
class LogCrawlerTest extends Specification {

    void 'verify list files'() {
        final folder = new File(LogCrawlerTest.protectionDomain.codeSource.location.path)
        expect:
        0 < LogCrawler.listFiles(folder).size()
        2 == LogCrawler.listFiles(folder, '*.log').size()
        1 == LogCrawler.listFiles(folder, '*sample-1.log').size()
    }

    void 'verify builders'() {
        final folder = new File(LogCrawlerTest.protectionDomain.codeSource.location.path)
        final stringWriter = new StringWriter()
        final listCollector = new ArrayList<LogEntry>()

        given:
        def logContext = new LogCrawler.Builder()
                .log4j(folder, '%5p | %d | %F | %L | %m%n', '*sample-1.log',)
                .date().exception().crawler()
                .output(stringWriter, Log4jConsumer.PRIORITY, Log4jConsumer.DATE, Log4jConsumer.CALLER_FILE_NAME, Log4jConsumer.CALLER_LINE, Log4jConsumer.MESSAGE)
                .withConsumer({ LogEntry logEntry -> listCollector.add(logEntry) })
                .build()
        logContext.consume()

        expect:
        0 < stringWriter.toString().length()
        21 == listCollector.size()
        null != listCollector[20].get(ExceptionConsumer.LOG_ERROR_MESSAGE)
        null != listCollector[20].get(ExceptionConsumer.EXCEPTION)

        and: 'verify csv collector columns are populated with groups defined in source consumer'
        null != (logContext = new LogCrawler.Builder()
                .log4j(folder, '%5p | %d | %F | %L | %m%n', '*sample-1.log',).crawler()
                .output(stringWriter)
                .build())
        [Log4jConsumer.PRIORITY, Log4jConsumer.DATE,
         Log4jConsumer.CALLER_FILE_NAME, Log4jConsumer.CALLER_LINE,
         Log4jConsumer.MESSAGE] == logContext.consumers.get(0).columns
    }

    void 'log entry iterator from one/multiple source(s)'() {
        final folder = new File(LogCrawlerTest.protectionDomain.codeSource.location.path)
        final stringWriter = new StringWriter()

        given:
        def logContext = new LogCrawler.Builder()
                .log4j(folder, '%5p | %d | %F | %L | %m%n', '*sample-1.log',)
                .date().exception().crawler()
                .output(stringWriter, Log4jConsumer.PRIORITY, Log4jConsumer.DATE, Log4jConsumer.CALLER_FILE_NAME, Log4jConsumer.CALLER_LINE, Log4jConsumer.MESSAGE)
                .build()
        def iterator = logContext.iterator()
        List result = iterator.toList()

        expect:
        0 < stringWriter.toString().length()
        21 == result.size()
        null != result[20].get(ExceptionConsumer.LOG_ERROR_MESSAGE)
        null != result[20].get(ExceptionConsumer.EXCEPTION)

        and: 'verify multiple sources iterator'
        null != (logContext = new LogCrawler.Builder()
                .log4j(folder, '%5p | %d | %F | %L | %m%n', '*sample-1.log',).crawler()
                .output(stringWriter, Log4jConsumer.PRIORITY, Log4jConsumer.DATE, Log4jConsumer.CALLER_FILE_NAME, Log4jConsumer.CALLER_LINE, Log4jConsumer.MESSAGE)
                .log4j(folder, '%-4r [%t] %-5p %c %x - %m%n', '*sample-2.log',).crawler()
                .output(stringWriter, Log4jConsumer.PRIORITY, Log4jConsumer.DATE, Log4jConsumer.CALLER_FILE_NAME, Log4jConsumer.CALLER_LINE, Log4jConsumer.MESSAGE)
                .build())
        ['sample-1.log', 'sample-2.log'] == logContext.sourceConsumerMap.keySet().collect { LineReader.getSourceName(it)}.sort()
        null != (iterator = logContext.iterator())
        null != (result = iterator.toList())
        29 == result.size()
    }

    void 'parse log entries using groovy script definition'() {
        given:
        final logContext = LogCrawler.builder('/sample-folder-sources-log.groovy').build()
        def iterator = logContext.iterator()

        expect:
        29 == iterator.toList().size()
    }

    void 'parse log entries using log4j config file'() {
        given:
        final folder = new File(LogCrawlerTest.protectionDomain.codeSource.location.path)
        final builder = LogCrawler.builder().log4jSource(folder, '/log4j.properties')

        expect: 'verify registered builders'
        null != builder.log4jBuilder('sample1')
        null != builder.log4jBuilder('%5p | %d | %F | %L | %m%n')
        null != builder.log4jBuilder('sample-1.log*')

        null != builder.log4jBuilder('sample2')
        null != builder.log4jBuilder('%-4r [%t] %-5p %c %x - %m%n')
        null != builder.log4jBuilder('sample-2.log*')

        and: 'verify no sources found cause there is no such log source file'
        null == builder.log4jBuilder('sample3')
        null == builder.log4jBuilder('%d %5p %c [%t] - %m%n')
        null == builder.log4jBuilder('sample-3.log*')

        and: 'verify context is consumed properly'
        29 == builder.build().iterator().toList().size()
    }

    void 'parse log entries using configuration option arguments'() {
        given:
        final folder = new File(LogCrawlerTest.protectionDomain.codeSource.location.path)

        expect: 'parse log files using log4j conversion pattern for each file'
        29 == LogCrawler.builder(
                '-log4j', '%5p | %d | %F | %L | %m%n', new File(folder, 'sample-1.log').path,
                '-log4j', '%-4r [%t] %-5p %c %x - %m%n', new File(folder, 'sample-2.log').path,
                '/sample-define-consumers-log.groovy'
        ).build().iterator().toList().size()

        and: 'parse log folder using log4j configuration file'
        29 == LogCrawler.builder(
                '-log4j', new File(folder, 'log4j.properties').path, folder.path,
                '/sample-define-consumers-log.groovy'
        ).build().iterator().toList().size()

        and: 'parse log folder no builder scripts provided'
        29 == LogCrawler.builder(
                '-log4j', new File(folder, 'log4j.properties').path, folder.path,
        ).build().iterator().toList().size()

        and: 'parse log folder and file'
        37 == LogCrawler.builder(
                '-log4j', new File(folder, 'log4j.properties').path, folder.path,
                '-log4j', '%-4r [%t] %-5p %c %x - %m%n', new File(folder, 'sample-2.log').path,
        ).build().iterator().toList().size()

        when: 'illegal log4j config provided for a file when conversion pattern is required'
        LogCrawler.builder(
                '-log4j', new File(folder, 'log4j.properties').path, new File(folder, 'sample-1.log').path,
                '/sample-define-consumers-log.groovy'
        ).build()
        then:
        thrown(RuntimeException)

        when: 'no log4j when conversion pattern provided before file'
        LogCrawler.builder(
                new File(folder, 'sample-1.log').path, '-log4j', '%-4r [%t] %-5p %c %x - %m%n',
                '/sample-define-consumers-log.groovy'
        ).build()
        then:
        thrown(RuntimeException)

        when: 'unknown argument provided'
        LogCrawler.builder(
                '-wrongArg', new File(folder, 'log4j.properties').path, folder.path,
                '/sample-define-consumers-log.groovy'
        ).build()
        then:
        thrown(RuntimeException)

        when: 'not existing file provided'
        LogCrawler.builder(
                '-log4j', new File(folder, 'log4j.properties').path, folder.path + '1',
                '/sample-define-consumers-log.groovy'
        ).build()
        then:
        thrown(RuntimeException)

        when: 'log4j config provided but no files for parsing'
        LogCrawler.builder(
                '-log4j', new File(folder, 'log4j.properties').path, '/sample-define-consumers-log.groovy'
        ).build()
        then:
        thrown(RuntimeException)
    }

    void 'asynchronous sources consumption'() {
        given:
        final folder = new File(LogCrawlerTest.protectionDomain.codeSource.location.path)
        final logEntries = Collections.synchronizedList(new ArrayList())
        final logCrawler = LogCrawler.builder()
            .log4j(folder, '%5p | %d | %F | %L | %m%n', '*sample-1.log',).crawler()
            .log4j(folder, '%-4r [%t] %-5p %c %x - %m%n', '*sample-2.log',).crawler()
            .withConsumer(new ArrayListCollector(logEntries))
            .parallel()
            .build()
        logCrawler.consume()

        expect:
        29 == logEntries.size()
    }

}
