package org.prismus.scrambler.jdbc

import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseBuilder
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseType
import spock.lang.Specification

import javax.sql.DataSource

/**
 * @author Serge Pruteanu
 */
class DataSourceDefinitionTest extends Specification {
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
        final dataSourceDefinition = new DataSourceDefinition(dataSource).build()

        expect:
        dataSourceDefinition != null
        dataSourceDefinition.tableMap.size() > 0
        dataSourceDefinition.fkTableMap.size() > 0
        'H2' == dataSourceDefinition.dbName
    }

}
