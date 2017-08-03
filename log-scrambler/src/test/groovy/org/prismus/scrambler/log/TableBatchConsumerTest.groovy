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

import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseBuilder
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseType
import spock.lang.Specification

import javax.sql.DataSource

/**
 * @author Serge Pruteanu
 */
class TableBatchConsumerTest extends Specification {
    private static DataSource dataSource

    void setupSpec() {
        dataSource = new EmbeddedDatabaseBuilder()
                .setType(EmbeddedDatabaseType.H2)
//                .setSeparator(';').addScript('/h2-sample-schema.sql')
                .build()
    }

    void 'log entry batch insertion consumer'() {
        given:
        final tableConsumer = TableBatchConsumer.of(dataSource, 'LogEntry')
                .withCreateTableScript('/h2-sample-schema.sql')
        final logContext = LogCrawler.builder('/sample-folder-sources-log.groovy').withConsumer(tableConsumer).build()

        expect: 'verify table check/creation'
        false == tableConsumer.lookupExistingTables(dataSource).collect {it.toLowerCase()}.contains('logentry')

        and: 'execute creation script'
        tableConsumer.executeTableScript(tableConsumer.sql, tableConsumer.createTableScript, tableConsumer.statementSeparator)
        tableConsumer.readTableMeta()

        and: 'lookup table created, metadata resolved'
        tableConsumer.tableMeta
        tableConsumer.tableMeta.columnMap
        tableConsumer.tableMeta.columnTypeMap
        tableConsumer.tableMeta.ids
        tableConsumer.tableMeta.idIdentity

        and: 'check log entries inserted'
        29 == logContext.iterator().toList().size()
        29L == tableConsumer.count()

        and: 'queue is empty, all entries inserted'
        tableConsumer.batchQueue.isEmpty()
    }

}
