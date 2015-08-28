package org.prismus.scrambler.jdbc

import groovy.sql.BatchingPreparedStatementWrapper
import groovy.sql.GroovyRowResult
import groovy.sql.Sql
import groovy.transform.CompileStatic
import groovy.transform.PackageScope
import org.prismus.scrambler.Value
import org.prismus.scrambler.value.ValueDefinition

import javax.sql.DataSource
import java.sql.Connection
import java.sql.ResultSet

/**
 * todo: add description
 *
 * @author Serge Pruteanu
 */
@CompileStatic
class DatabaseValue implements Value {
    private final DataSource dataSource
    private ValueDefinition definition

    DatabaseValue(DataSource dataSource) {
        this.dataSource = dataSource
        this.definition = new ValueDefinition()
    }

    DatabaseValue usingDefinition(ValueDefinition definition) {
        this.definition = definition
        return this
    }

    DatabaseValue usingDefinition(String... definitions) {
        this.definition.usingDefinitions(definitions)
        return this
    }

    DatabaseValue scanDefinition(String definition, String... definitions) {
        this.definition.scanDefinitions(definition, definitions)
        return this
    }

    DatabaseValue scanLibraryDefinition(String definitionMatcher) {
        this.definition.usingLibraryDefinitions(definitionMatcher)
        return this
    }

    @Override
    Object next() {
        throw new RuntimeException('Implement me')
    }

    @Override
    Object get() {
        throw new RuntimeException('Implement me')
    }

    protected List<String> getMssqlDbTables(DataSource dataSource) {
        return new Sql(dataSource)
                .rows("SELECT table_name FROM information_schema.tables WHERE table_type = 'base table'")
                .collect { GroovyRowResult it -> it.getAt(0) } as List<String>
    }

    protected List<String> getDbTables(DataSource dataSource) {
        Connection connection = null
        ResultSet rs = null
        final result = new ArrayList<String>()
        try {
            connection = dataSource.connection
            final databaseMetaData = connection.metaData
            final databaseProductName = connection.metaData.databaseProductName
            if (databaseProductName.contains('Microsoft')) {
                return getMssqlDbTables(dataSource)
            }
            final String[] types = { 'TABLE' }
            rs = databaseMetaData.getTables(connection.catalog, null, null, types)
            while (rs.next()) {
                result.add(rs.getString("TABLE_NAME"))
            }
        } finally {
            closeQuietly(rs)
            closeQuietly(connection)
        }
        return result
    }

    protected void insertData(String table, Collection<String> sortedKeys, List<Map> rows) {
        final String insertStatement = buildInsertStatement(table, sortedKeys)
        final sql = new Sql(dataSource)
        try {
            sql.withTransaction {
                final counts = sql.withBatch(rows.size(), insertStatement) {
                    final BatchingPreparedStatementWrapper statement ->
                        for (final rowMap : rows) {
                            statement.addBatch(new LinkedHashMap(rowMap))
                        }
                }
                sql.commit()
                if (counts == null || counts.length == 0) {
                    throw new RuntimeException("Data for table $table are not inserted")
                }
            }
        } finally {
            sql.close()
        }
    }

    @Override
    Object clone() throws CloneNotSupportedException {
        return super.clone()
    }

    @PackageScope
    static String buildInsertStatement(String table, Collection<String> sortedKeys) {
        return "INSERT INTO $table (${sortedKeys.join(',')}) VALUES (${':' + sortedKeys.join(', :')})"
    }

    @PackageScope
    static void closeQuietly(Connection connection) {
        try {
            connection?.close()
        } catch (Exception ignore) { }
    }

    @PackageScope
    static void closeQuietly(ResultSet rs) {
        try {
            rs?.close()
        } catch (Exception ignore) { }
    }

}
