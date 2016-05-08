package org.prismus.scrambler.jdbc

import groovy.sql.GroovyRowResult
import groovy.sql.Sql
import groovy.transform.CompileStatic
import groovy.transform.PackageScope
import org.prismus.scrambler.Value
import org.prismus.scrambler.ValuePredicate
import org.prismus.scrambler.ValuePredicates
import org.prismus.scrambler.value.Constant
import org.prismus.scrambler.value.ValueDefinition

import javax.sql.DataSource
import java.sql.*

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
    private Map<String, ValueDefinition> tableDefinitionMap = [:]

    private final DataSource dataSource

    DataSourceDefinition(DataSource dataSource) {
        this.dataSource = dataSource
    }

    @PackageScope
    void setTableMap(Map<String, TableMeta> tableMap) {
        this.tableMap = tableMap
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
        for (final String table : tableMap.keySet()) {
            if (!tableDefinitionMap.containsKey(table)) {
                tableDefinitionMap.put(table, new ValueDefinition().usingLibraryDefinitions(table))
            }
        }
        super.build()
        return this
    }

    DataSourceDefinition usingDefinition(String table, ValueDefinition definition) {
        tableDefinitionMap.put(table, definition)
        return this
    }

    DataSourceDefinition usingDefinition(String table, String definition, String... definitions) {
        tableDefinitionMap.put(table, new ValueDefinition().usingDefinitions((definitions != null
                ? (Arrays.asList(definition) + Arrays.asList(definitions)).toArray(new String[1 + definitions.length])
                : [definition] as String[]
        )))
        return this
    }

    Value lookupValue(String tableName, ValuePredicate predicate) {
        return tableDefinitionMap.containsKey(tableName) ? tableDefinitionMap.get(tableName).lookupValue(predicate) : null
    }

    Value lookupValue(String tableName, String property, Class type) {
        return tableDefinitionMap.containsKey(tableName) ? tableDefinitionMap.get(tableName).lookupValue(property, type) : null
    }

    Map<String, Value> toMapValue(TableMeta tableMeta, boolean generateNullable) {
        final columnMap = tableMeta.columnMap
        final List<String> keys = new ArrayList<String>(columnMap.size())
        final valueMap = new LinkedHashMap<String, Value>()
        for (Map.Entry<String, ColumnMeta> entry : columnMap.entrySet()) {
            final column = entry.value
            if (column.isAutoIncrement() || (!generateNullable && column.isNullable())) {
                continue
            }
            final columnName = entry.key
            final boolean fkColumn = column.isFk()
            Value value
            if (fkColumn) {
                value = lookupValue(ValuePredicates.matchProperty(columnName))
            } else {
                value = lookupValue(columnName, column.classType)
            }
            if (value) {
                keys.add(columnName)
                valueMap.put(columnName, value)
            } else if (fkColumn) {
                value = lookupFkValue(column, generateNullable)
                if (value) {
                    valueMap.put(columnName, value)
                }
            }
        }

        Collections.sort(keys)
        final map = new LinkedHashMap<String, Value>()
        for (String column : keys) {
            map.put(column, valueMap.get(column))
        }
        return map
    }

    @PackageScope
    Value lookupFkValue(ColumnMeta column, boolean generateNullable) {
        final primaryTableName = column.primaryTableName
        final primaryColumnName = column.primaryColumnName
        final primaryTableMeta = tableMap.get(primaryTableName)
        final primaryColumnMeta = primaryTableMeta.columnMap.get(primaryColumnName)
        Value value = lookupValue(primaryTableName, primaryColumnName, primaryColumnMeta.classType)
        if (value == null) {
            final insertValue = new TableInsertValue(dataSource, primaryTableName, toMapValue(primaryTableMeta, generateNullable))
            if (primaryColumnMeta.autoIncrement) {
                value = new ColumnDelegateValue(primaryColumnName, new AutoIncrementIdInsertValue(primaryColumnName,
                        new TableRowValue(dataSource, primaryTableName,
                                "SELECT $primaryColumnName FROM $primaryTableName ORDER BY $primaryColumnName DESC"),
                        insertValue))
            } else {
                value = new ColumnDelegateValue(primaryColumnName, insertValue)
            }
        }
        return value
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

    @CompileStatic
    private static class ColumnDelegateValue extends Constant {
        private final String columnName
        private final Value<Map<String, Object>> value

        ColumnDelegateValue(String columnName, Value<Map<String, Object>> value) {
            this.columnName = columnName
            this.value = value
        }

        @Override
        protected Object doNext() {
            final map = value.next()
            return map.get(columnName)
        }
    }

    @CompileStatic
    private static class AutoIncrementIdInsertValue extends ColumnDelegateValue {
        private final Value insertValue

        AutoIncrementIdInsertValue(String columnName, Value<Map<String, Object>> value, Value insertValue) {
            super(columnName, value)
            this.insertValue = insertValue
        }

        @Override
        protected Object doNext() {
            insertValue.next()
            return super.doNext()
        }
    }

}
