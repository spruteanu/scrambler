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

import org.prismus.scrambler.log.LogCrawlerTest

/**
 * @author Serge Pruteanu
 */
final folder = new File(LogCrawlerTest.protectionDomain.codeSource.location.path)
log4j(folder, '%5p | %d | %F | %L | %m%n', '*sample-1.log',).date()
log4j(folder, '%-4r [%t] %-5p %c %x - %m%n', '*sample-2.log',)
