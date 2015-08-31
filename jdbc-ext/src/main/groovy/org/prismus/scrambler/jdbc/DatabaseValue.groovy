package org.prismus.scrambler.jdbc

import groovy.sql.BatchingPreparedStatementWrapper
import groovy.sql.GroovyRowResult
import groovy.sql.Sql
import groovy.transform.CompileStatic
import groovy.transform.PackageScope
import org.prismus.scrambler.value.Constant
import org.prismus.scrambler.value.ValueDefinition

import javax.sql.DataSource
import java.sql.Connection
import java.sql.ResultSet
import java.sql.ResultSetMetaData

/**
 * todo: add description
 *
 * @author Serge Pruteanu
 */
@CompileStatic
class DatabaseValue extends Constant<List<Map<String, Object>>> {
    private final DataSource dataSource
    private final Map<String, Table> tableMap
    private boolean generateNullable = true

    private List<Table> tables = new ArrayList<Table>()

    private ValueDefinition definition

    DatabaseValue(DataSource dataSource) {
        this.dataSource = dataSource
        this.definition = new ValueDefinition()
        this.tableMap = listTableMap()
    }

    DatabaseValue generateAll() {
        this.generateNullable = true
        return this
    }

    DatabaseValue generateStrict() {
        this.generateNullable = false
        return this
    }

    DatabaseValue forTable(String table) {
        if (!tableMap.containsKey(table)) {
            throw new IllegalArgumentException("'$table' is not found in provided datasource")
        }
        tables.add(tableMap.get(table))
        return this
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
    protected List<Map<String, Object>> doNext() {
        // todo: implement me
        return new ArrayList<Map<String,Object>>()
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

    protected List<String> listMssqlTables() {
        return new Sql(dataSource)
                .rows("SELECT table_name FROM information_schema.tables WHERE table_type = 'base table'")
                .collect { GroovyRowResult it -> it.getAt(0) } as List<String>
    }

    protected Map<String, Table> listTableMap() {
        final List<String> tables = listTables()
        final tableMap = new LinkedHashMap<String, Table>(tables.size())
        for (final String table : tables) {
            tableMap.put(table, getTableMeta(table))
        }
        return tableMap
    }

    protected Table getTableMeta(String table) {
        Connection connection = null
        ResultSet rs = null
        final result = new Table(name: table)
        try {
            connection = dataSource.connection
            final databaseMetaData = connection.metaData
            rs = databaseMetaData.getColumns(connection.catalog, null, table, null)
            while (rs.next()) {
                final String columnName = rs.getString(4)
                final int columnType = rs.getInt(5)
                result.columnMap.put(columnName, new Column(name: columnName, type: columnType,
                        columnProperties: listProperties(rs)))
            }
            result.idFields = getPrimaryKeys(table)
            result.fkMap = getForeignKeys(table)
        } finally {
            closeQuietly(rs)
            closeQuietly(connection)
        }
        return result
    }

    protected List<String> getPrimaryKeys(String table) {
        Connection connection = null
        ResultSet rs = null
        final List<String> result = new ArrayList<String>()
        try {
            connection = dataSource.connection
            rs = connection.metaData.getPrimaryKeys(connection.catalog, null, table)
            while (rs.next()) {
                result.add(rs.getString(4))
            }
        } finally {
            closeQuietly(rs)
            closeQuietly(connection)
        }
        return result
    }

    protected Map<String, Map<String, Object>> getForeignKeys(String table) {
        Connection connection = null
        ResultSet rs = null
        final result = [:]
        try {
            connection = dataSource.connection
            rs = connection.metaData.getExportedKeys(connection.getCatalog(), null, table)
            while (rs.next()) {
                final String columnName = rs.getString("FK_NAME")
                result.put(columnName, listProperties(rs))
            }
        } finally {
            closeQuietly(rs)
            closeQuietly(connection)
        }
        return result
    }

    protected Map<String, Object> listProperties(ResultSet rs) {
        final props = [:]
        final ResultSetMetaData rsmd = rs.getMetaData()
        final int columnCount = rsmd.getColumnCount()
        for (int i = 0; i < columnCount; i++) {
            props.put(rsmd.getColumnName(i), rs.getObject(i))
        }
        return props
    }

    protected List<String> listTables() {
        Connection connection = null
        ResultSet rs = null
        List<String> result = new ArrayList<String>()
        try {
            connection = dataSource.connection
            final databaseMetaData = connection.metaData
            final databaseProductName = connection.metaData.databaseProductName
            if (databaseProductName.contains('Microsoft')) {
                result = listMssqlTables()
            } else {
                final String[] types = { 'TABLE' }
                rs = databaseMetaData.getTables(connection.catalog, null, null, types)
                while (rs.next()) {
                    result.add(rs.getString("TABLE_NAME"))
                }
            }
        } finally {
            closeQuietly(rs)
            closeQuietly(connection)
        }
        return result
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

    private static class Table {
        private String name
        private List<String> idFields = []
        private Map<String, Column> columnMap = [:]
        private Map<String, Map<String, Object>> fkMap = [:]
    }

    private static class Column {
        private String name
        private int type

        private boolean nullable
        private boolean increment

        private Class clazzType
        private Map<String, Object> columnProperties

    }

}
