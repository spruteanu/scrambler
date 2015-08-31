package org.prismus.scrambler.jdbc

import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseBuilder
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseType
import spock.lang.Specification

import javax.sql.DataSource

/**
 * @author Serge Pruteanu
 */
class DatabaseTest extends Specification {
    private static DataSource dataSource

    void setupSpec() {
        dataSource = new EmbeddedDatabaseBuilder()
                .setType(EmbeddedDatabaseType.H2)
                .setSeparator('/')
                .addScript('/schema.sql')
                .build()
    }

    void 'verify database population'() {
        given:
        final dbValue = new DatabaseValue(dataSource)

        expect:
        dbValue != null
        dbValue.tableMap.size() > 0
    }

}
