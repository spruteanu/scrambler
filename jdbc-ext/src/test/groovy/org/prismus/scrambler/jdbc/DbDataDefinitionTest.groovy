package org.prismus.scrambler.jdbc

import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseBuilder
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseType
import spock.lang.Specification

import javax.sql.DataSource

/**
 * @author Serge Pruteanu
 */
class DbDataDefinitionTest extends Specification {
    private static DataSource dataSource

    void setupSpec() {
        dataSource = new EmbeddedDatabaseBuilder()
                .setType(EmbeddedDatabaseType.H2)
                .setSeparator('/')
                .addScript('/schema.sql')
                .build()
    }

    @SuppressWarnings("GroovyPointlessBoolean")
    void 'verify database population'() {
        given:
        final dataSourceDefinition = new DbDataDefinition(dataSource).build()

        expect:
        dataSourceDefinition != null
        dataSourceDefinition.tableMap.size() > 0
        'H2' == dataSourceDefinition.dbName

        and: 'verify fk meta resolved'
        final fkTableMeta = dataSourceDefinition.tableMap.get('RENTALORDERS')
        fkTableMeta.fkColumns.size() > 0
        fkTableMeta.columnMap.get('EMPLOYEEID').isFk()
        fkTableMeta.columnMap.get('employeeid').isFk()
        fkTableMeta.columnMap.get('EMPLOYEEID').getPrimaryTableName() == 'EMPLOYEES'
        fkTableMeta.columnMap.get('employeeid').getPrimaryTableName() == 'EMPLOYEES'
        fkTableMeta.columnMap.get('EMPLOYEEID').getPrimaryColumnName() == 'EMPLOYEEID'

        and: 'verify relationship'
        final relationTableMeta = dataSourceDefinition.tableMap.get('employees')
        relationTableMeta.relationshipTables == ['RENTALORDERS'] as Set
        true == relationTableMeta.hasRelationship('RENTALORDERS')
    }

    void 'verify tables sorted by fks'() {
        given:
        final dataSourceDefinition = new DbDataDefinition(dataSource).build()
        final builder = new DbDataBuilder(dataSourceDefinition)

        final tables = dataSourceDefinition.tableMap.subMap('cars', 'rentalRates', 'employees', 'customers', 'rentalOrders').values().toList()
        builder.sortTablesByFkDependency(tables)

        expect:
        tables.size() == 5
        'CARS' == tables[0].name // first table is the one with most number of dependents, so that table records creation will start first
        'RENTALORDERS' == tables[4].name // last table should be the one with most dependencies
    }

}
