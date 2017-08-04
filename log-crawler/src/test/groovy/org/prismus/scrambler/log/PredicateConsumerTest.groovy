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

import java.util.function.Predicate

/**
 * @author Serge Pruteanu
 */
class PredicateConsumerTest extends Specification {

    void 'verify predicate consumer'() {
        given:
        final folder = new File(LogCrawlerTest.protectionDomain.codeSource.location.path)
        def exceptionCollector = new ArrayListCollector()
        def npeCollector = new ArrayListCollector()
        def logCrawler = new LogCrawler.Builder()
                .log4jSourceFolder(folder, '%5p | %d | %F | %L | %m%n', '*sample-1.log',).toExceptionConsumer().recurContext()
                .log4jSourceFolder(folder, '%-4r [%t] %-5p %c %x - %m%n', '*sample-2.log',).toExceptionConsumer().recurContext()
                .filterTo({ LogEntry entry -> null != entry.get(ExceptionConsumer.EXCEPTION) }, exceptionCollector)
                .filterTo({ LogEntry entry -> 'java.lang.NullPointerException' == entry.get(ExceptionConsumer.EXCEPTION_CLASS) }, npeCollector)
                .build()
        logCrawler.consume()

        expect: 'verify that 2 exceptions are in logs, one of type NPE'
        2 == exceptionCollector.logEntries.size()
        1 == npeCollector.logEntries.size()

        and: 'another example of predicate using a container of consumers'
        null != (exceptionCollector = new ArrayListCollector())
        null != (npeCollector = new ArrayListCollector())
        null != (logCrawler = new LogCrawler.Builder()
                .log4jSourceFolder(folder, '%5p | %d | %F | %L | %m%n', '*sample-1.log',).toExceptionConsumer().recurContext()
                .log4jSourceFolder(folder, '%-4r [%t] %-5p %c %x - %m%n', '*sample-2.log',).toExceptionConsumer().recurContext()
                .filterTo({ LogEntry entry -> null != entry.get(ExceptionConsumer.EXCEPTION) },
                    ContainerConsumer.of(
                            exceptionCollector, PredicateConsumer.of({ LogEntry entry -> 'java.lang.NullPointerException' == entry.get(ExceptionConsumer.EXCEPTION_CLASS) }, npeCollector))
                    )
                .build())
        logCrawler.consume()
        2 == exceptionCollector.logEntries.size()
        1 == npeCollector.logEntries.size()

        and: 'example of complex predicate: filter by entries of INFO or WARN level'
        null != (exceptionCollector = new ArrayListCollector())
        LogCrawler.builder('/sample-folder-sources-log.groovy').filterTo(
                ({'INFO' == it.get(Log4jConsumer.PRIORITY)} as Predicate<LogEntry>).or({'WARN' == it.get(Log4jConsumer.PRIORITY)}), exceptionCollector
        ).build().consume()
        5 == exceptionCollector.logEntries.size()
    }

}
