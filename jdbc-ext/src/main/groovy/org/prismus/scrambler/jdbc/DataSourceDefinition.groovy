package org.prismus.scrambler.jdbc

import groovy.sql.GroovyRowResult
import groovy.sql.Sql
import groovy.transform.CompileStatic
import groovy.transform.PackageScope
import org.prismus.scrambler.MapScrambler
import org.prismus.scrambler.Value
import org.prismus.scrambler.value.MapValue
import org.prismus.scrambler.value.ValueDefinition

import javax.sql.DataSource
import java.sql.Connection
import java.sql.ResultSet
import java.sql.Time
import java.sql.Timestamp
import java.sql.Types

/**
 * todo: add description
 *
 * @author Serge Pruteanu
 */
@CompileStatic
class DataSourceDefinition extends ValueDefinition {

    private Map<Integer, Class> typeClassMap = [
            (Types.BIT)          : Boolean,
            (Types.TINYINT)      : Byte,
            (Types.SMALLINT)     : Short,
            (Types.INTEGER)      : Integer,
            (Types.BIGINT)       : Long,
            (Types.FLOAT)        : Float,
            (Types.REAL)         : Float,
            (Types.DOUBLE)       : Double,
            (Types.NUMERIC)      : BigDecimal,
            (Types.DECIMAL)      : BigDecimal,
            (Types.CHAR)         : Character,
            (Types.VARCHAR)      : String,
            (Types.LONGVARCHAR)  : String,
            (Types.DATE)         : java.sql.Date,
            (Types.TIME)         : Time,
            (Types.TIMESTAMP)    : Timestamp,
            (Types.BINARY)       : (byte[]),
            (Types.VARBINARY)    : (byte[]),
            (Types.LONGVARBINARY): (byte[]),
            (Types.BLOB)         : (byte[]),
            (Types.CLOB)         : String,
            (Types.BOOLEAN)      : Boolean,
            (Types.NCHAR)        : String,
            (Types.NVARCHAR)     : String,
            (Types.LONGNVARCHAR) : String,
            (Types.NCLOB)        : String,
    ] as Map<Integer, Class>

    protected Map<String, TableMeta> tableMap

    private final DataSource dataSource

    DataSourceDefinition(DataSource dataSource) {
        this.dataSource = dataSource
    }

    DataSourceDefinition registerTypeClass(int type, Class clazzType) {
        typeClassMap.put(type, clazzType)
        return this
    }

    Map<Integer, Class> getTypeClassMap() {
        return typeClassMap
    }

    DataSource getDataSource() {
        return dataSource
    }

    @Override
    protected DataSourceDefinition build() {
        tableMap = listTableMap()
        super.build()
        return this
    }

    MapValue<String> toMapValue(TableMeta tableMeta, boolean generateNullable) {
        final columnMap = tableMeta.columnMap
        final List<String> keys = new ArrayList<String>(columnMap.size())
        final valueMap = new LinkedHashMap<String, Value>()
        for (Map.Entry<String, ColumnMeta> entry : columnMap.entrySet()) {
            final column = entry.value
            if (column.isAutoIncrement()) {
                continue
            }
            final columnName = entry.key
            Value value = lookupValue(columnName, column.classType)
            if (!generateNullable && column.isNullable()) {
                continue
            }
            if (value != null) {
                keys.add(columnName)
                valueMap.put(columnName, value)
            } else {
                // todo Serge: resolve foreign key values
            }
        }

        Collections.sort(keys)
//    String insertStatement
//    Set<String> sortedKeys
//        sortedKeys = new LinkedHashSet<String>(keys)
//        insertStatement = buildInsertStatement(tableMeta.name, sortedKeys)

        final map = new LinkedHashMap<String, Value>()
        for (String column : keys) {
            map.put(column, valueMap.get(column))
        }
        return MapScrambler.of(map)
    }

    protected List<String> listMssqlTables() {
        return new Sql(dataSource)
                .rows('SELECT table_name FROM information_schema.tables WHERE table_type = \'base table\'')
                .collect { GroovyRowResult it -> it.getAt(0) } as List<String>
    }

    protected List<String> listH2Tables() {
        return new Sql(dataSource)
                .rows('SELECT DISTINCT table_name FROM information_schema.columns')
                .collect { GroovyRowResult it -> it.getAt(0) } as List<String>
    }

    protected Map<String, TableMeta> listTableMap() {
        final List<String> tables = listTables()
        final tableMap = new LinkedHashMap<String, TableMeta>(tables.size())
        final Map<String, String> fkTableMap = [:] as Map<String, String>
        for (final String table : tables) {
            tableMap.put(table, getTableMeta(table, fkTableMap))
        }

        resolveForeignKeysMeta(fkTableMap, tableMap)
        return tableMap
    }

    protected void resolveForeignKeysMeta(Map<String, String> fkTableMap, Map<String, TableMeta> tableMap) {
        for (final Map.Entry<String, String> entry : fkTableMap.entrySet()) {
            final fkName = entry.key
            final TableMeta parentMeta = tableMap.get(entry.value)
            final fkPropertiesMap = parentMeta.relationshipMap.get(fkName)
            final fkTableName = fkPropertiesMap.get('FKTABLE_NAME').toString()
            final fkColumnName = fkPropertiesMap.get('FKCOLUMN_NAME').toString()
            final fkTableMeta = tableMap.get(fkTableName)
            final fkColumnMeta = fkTableMeta.columnMap.get(fkColumnName)
            fkTableMeta.fkColumns.add(fkColumnName)
            fkColumnMeta.fkName = fkName
            fkColumnMeta.columnProperties.put(fkName, fkPropertiesMap)
        }
    }

    protected TableMeta getTableMeta(String table, Map<String, String> fkTableMap) {
        Connection connection = null
        ResultSet rs = null
        final result = new TableMeta(name: table)
        try {
            connection = dataSource.connection
            final databaseMetaData = connection.metaData
            rs = databaseMetaData.getColumns(connection.catalog, null, table, null)
            while (rs.next()) {
                final String columnName = rs.getString(4)
                final int columnType = rs.getInt(5)
                result.columnMap.put(columnName, new ColumnMeta(name: columnName, type: columnType,
                        columnProperties: Util.asMap(rs), classType: typeClassMap.get(columnType)))
            }
            result.ids = getPrimaryKeys(table)
            result.relationshipMap = getForeignKeys(table, fkTableMap)
        } finally {
            Util.closeQuietly(rs)
            Util.closeQuietly(connection)
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
            Util.closeQuietly(rs)
            Util.closeQuietly(connection)
        }
        return result
    }

    protected Map<String, Map<String, Object>> getForeignKeys(String table, Map<String, String> fkTableMap) {
        Connection connection = null
        ResultSet rs = null
        final result = [:]
        try {
            connection = dataSource.connection
            rs = connection.metaData.getExportedKeys(connection.getCatalog(), null, table)
            while (rs.next()) {
                final String fkName = rs.getString("FK_NAME")
                final fkProps = Util.asMap(rs)
                result.put(fkName, fkProps)
                fkTableMap.put(fkName, table)
            }
        } finally {
            Util.closeQuietly(rs)
            Util.closeQuietly(connection)
        }
        return result ? result : null
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
            } else if (databaseProductName.contains('H2')) {
                result = listH2Tables()
            } else {
                final String[] types = { 'TABLE' }
                rs = databaseMetaData.getTables(connection.catalog, null, null, types)
                while (rs.next()) {
                    result.add(rs.getString("TABLE_NAME"))
                }
            }
        } finally {
            Util.closeQuietly(rs)
            Util.closeQuietly(connection)
        }
        return result
    }

    String getDbName() {
        Connection connection = null
        try {
            connection = dataSource.connection
            return connection.metaData.databaseProductName
        } finally {
            Util.closeQuietly(connection)
        }
    }

    @PackageScope
    static String buildInsertStatement(String table, Collection<String> sortedKeys) {
        return "INSERT INTO $table (${sortedKeys.join(',')}) VALUES (${':' + sortedKeys.join(', :')})"
    }

    @PackageScope
    String generateSelectStatement(TableMeta tableMeta) {
        String select = "SELECT ${tableMeta.ids.join(', ')} FROM $tableMeta.name ORDER BY ${tableMeta.ids.join('DESC, ')}"
        select += ' DESC'
        return select
    }

}
