package org.prismus.scrambler.jdbc

import groovy.sql.GroovyRowResult
import groovy.sql.Sql
import groovy.transform.CompileStatic
import groovy.transform.PackageScope
import org.prismus.scrambler.Data
import org.prismus.scrambler.DataPredicate
import org.prismus.scrambler.data.DataDefinition

import javax.sql.DataSource
import java.sql.*

/**
 * todo: add description
 *
 * @author Serge Pruteanu
 */
@CompileStatic
class DbDataDefinition extends DataDefinition {

    private static Map<Integer, Class> defaultTypeClassMap = [
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

    private Map<Integer, Class> typeMap = new LinkedHashMap<Integer, Class>(defaultTypeClassMap)

    protected Map<String, TableMeta> tableMap
    private Map<String, DataDefinition> tableDefinitionMap = [:]

    private boolean generateAutoFields
    private boolean generateNullable

    private final DataSource dataSource

    DbDataDefinition(DataSource dataSource) {
        this.dataSource = dataSource
    }

    @PackageScope
    void setTableMap(Map<String, TableMeta> tableMap) {
        this.tableMap = tableMap
    }

    DbDataDefinition generateNullable() {
        generateNullable = true
        return this
    }

    DbDataDefinition generateAutoFields() {
        generateAutoFields = true
        return this
    }

    DbDataDefinition registerType(int type, Class clazzType) {
        typeMap.put(type, clazzType)
        return this
    }

    DbDataDefinition withTypeMap(int type, Class clazzType) {
        typeMap.put(type, clazzType)
        return this
    }

    DataSource getDataSource() {
        return dataSource
    }

    @Override
    protected DbDataDefinition build() {
        tableMap = listTableMap()
        for (final String table : tableMap.keySet()) {
            if (!tableDefinitionMap.containsKey(table)) {
                tableDefinitionMap.put(table, new DataDefinition().usingLibraryDefinitions(table))
            }
        }
        super.build()
        return this
    }

    DbDataDefinition usingDefinition(String table, DataDefinition definition) {
        tableDefinitionMap.put(table, definition)
        return this
    }

    DbDataDefinition usingDefinition(String table, String definition, String... definitions) {
        tableDefinitionMap.put(table, new DataDefinition().usingDefinitions((definitions != null
                ? (Arrays.asList(definition) + Arrays.asList(definitions)).toArray(new String[1 + definitions.length])
                : [definition] as String[]
        )))
        return this
    }

    Data lookupData(String tableName, DataPredicate predicate) {
        return tableDefinitionMap.containsKey(tableName) ? tableDefinitionMap.get(tableName).lookupData(predicate) : null
    }

    Data lookupData(String tableName, String property, Class type) {
        return tableDefinitionMap.containsKey(tableName) ? tableDefinitionMap.get(tableName).lookupData(property, type) : null
    }

    Map<String, Data> toDataMap(TableMeta tableMeta) {
        final columnMap = tableMeta.columnMap
        final List<String> keys = new ArrayList<String>(columnMap.size())
        final dataMap = new LinkedHashMap<String, Data>()
        for (final entry : columnMap.entrySet()) {
            final columnName = entry.key
            final columnMeta = entry.value
            if ((columnMeta.nullable && !generateNullable) || (columnMeta.autoIncrement && !generateAutoFields)) {
                continue
            }
            final Data data
            if (columnMeta.isFk()) {
                data = lookupFkData(columnMeta)
            } else {
                // todo Serge: if field is autoincremental and is not defined, lookupIncrementalData
                data = lookupData(columnName, columnMeta.classType)
            }
            if (data) {
                keys.add(columnName)
                dataMap.put(columnName, data)
            }
        }

        Collections.sort(keys)
        final map = new LinkedHashMap<String, Data>()
        for (String column : keys) {
            map.put(column, dataMap.get(column))
        }
        return map
    }

    @PackageScope
    Data lookupFkData(ColumnMeta columnMeta) {
        final primaryTableName = columnMeta.primaryTableName
        final primaryColumnName = columnMeta.primaryColumnName
        final primaryTableMeta = tableMap.get(primaryTableName)
        final primaryColumnMeta = primaryTableMeta.columnMap.get(primaryColumnName)
        Data data = lookupData(primaryTableName, primaryColumnName, primaryColumnMeta.classType)
        if (data == null) {
            // todo Serge: add a strategy to generate FK keys: pickup object from DB or from generated, cached array
        }
        return data
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
        final tableMap = new TreeMap<String, TableMeta>(String.CASE_INSENSITIVE_ORDER)
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
                        columnProperties: Util.asMap(rs), classType: typeMap.get(columnType)))
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
        final result = new TreeMap<String, Map<String, Object>>(String.CASE_INSENSITIVE_ORDER)
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
                final String[] types = { 'TABLE' } as String[]
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

}
