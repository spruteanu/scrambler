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

import groovy.transform.CompileStatic
import groovy.transform.PackageScope

/**
 * @author Serge Pruteanu
 */
@CompileStatic
@PackageScope
class ClosureConsumer implements LogConsumer {
    private final Closure closure

    ClosureConsumer(@DelegatesTo(LogEntry) Closure closure) {
        this.closure = closure
    }

    @Override
    void consume(LogEntry entry) {
        entry.with closure
    }

}
