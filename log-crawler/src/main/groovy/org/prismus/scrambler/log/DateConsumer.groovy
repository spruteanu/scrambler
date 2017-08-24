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

import java.text.SimpleDateFormat

/**
 * @author Serge Pruteanu
 */
class DateConsumer implements LogConsumer {
    static final String DATE = 'DATE'

    String group = DATE
    String target
    SimpleDateFormat dateFormat

    DateConsumer() {
    }

    DateConsumer(SimpleDateFormat dateFormat, String group = DATE, String target = null) {
        this.target = target
        this.dateFormat = dateFormat
        this.group = group
    }

    @Override
    void consume(LogEntry entry) {
        if (entry.logValueMap.containsKey(group)) {
            final dateString = entry.get(group)
            final date = dateFormat.parse(dateString.toString())
            String grField = target
            if (!grField) {
                grField = group
            }
            entry.put(grField, date)
        }
    }

    static DateConsumer of(SimpleDateFormat dateFormat, String group = DATE) {
        return new DateConsumer(dateFormat, group)
    }

    static DateConsumer of(String dateFormat, String group = DATE) {
        return new DateConsumer(new SimpleDateFormat(dateFormat), group)
    }

}
