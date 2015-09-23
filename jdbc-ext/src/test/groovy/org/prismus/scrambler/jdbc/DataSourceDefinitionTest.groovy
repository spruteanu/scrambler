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
        'H2' == dataSourceDefinition.dbName

        and: 'verify fk meta resolved'
        final fkTableMeta = dataSourceDefinition.tableMap.get('RENTALORDERS')
        fkTableMeta.fkColumns.size() > 0
        fkTableMeta.columnMap.get('EMPLOYEEID').isFk()
        fkTableMeta.columnMap.get('EMPLOYEEID').getPrimaryTableName() == 'EMPLOYEES'
        fkTableMeta.columnMap.get('EMPLOYEEID').getPrimaryColumnName() == 'EMPLOYEEID'

        and: 'verify relationship'
        final relationTableMeta = dataSourceDefinition.tableMap.get('EMPLOYEES')
        relationTableMeta.relationshipTables == ['RENTALORDERS'] as Set
        true == relationTableMeta.hasRelationship('RENTALORDERS')
    }

}
