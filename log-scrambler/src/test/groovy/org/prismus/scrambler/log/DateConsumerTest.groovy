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
class DateConsumerTest extends Specification {

    void 'verify date value consumer'() {
        final processor = Log4jConsumer.of('%5p | %d | %F | %L | %m%n')
                .withDateConsumer(Log4jConsumer.ISO8601_DATE_FORMAT)

        final entry = new LogEntry('ERROR | 2008-09-06 10:51:45,473 | SQLErrorCodesFactory.java | 128 | OMG, Something bad happened')
        processor.consume(entry)

        expect:
        Date.isInstance(entry.get(Log4jConsumer.DATE))
    }

}
