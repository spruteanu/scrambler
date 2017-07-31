package org.prismus.scrambler.log

import groovy.sql.BatchingPreparedStatementWrapper
import groovy.sql.GroovyRowResult
import groovy.sql.Sql
import groovy.transform.CompileStatic
import groovy.transform.PackageScope
import org.apache.commons.lang3.StringUtils

import javax.sql.DataSource
import java.sql.*
import java.text.SimpleDateFormat

/**
 * @author Serge Pruteanu
 */
@CompileStatic
class TableBatchInsertConsumer implements LogConsumer, Closeable {
    private static Map<Integer, Class> jdbcTypeClassMap = [
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
            (Types.DATE)         : java.util.Date,
            (Types.TIME)         : java.util.Date,
            (Types.TIMESTAMP)    : java.util.Date,
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

    private Sql sql
    private String tableName
    private List<String> columns
    private SimpleDateFormat dateFormat

    private String createTableScript
    private String statementSeparator

    private TableMeta tableMeta

    int batchSize = 1024
    private List<LogEntry> batchQueue = new ArrayList<>(1024)

    TableBatchInsertConsumer() {
    }

    TableBatchInsertConsumer(DataSource dataSource, int batchSize = 1024) {
        sql = new Sql(dataSource)
        setBatchSize(batchSize)
    }

    void setBatchSize(int batchSize) {
        this.batchSize = batchSize
        this.batchQueue = new ArrayList<>(batchSize)
    }

    TableBatchInsertConsumer withDatasource(DataSource dataSource, String tableName, String... columns) {
        sql = new Sql(dataSource)
        return forTable(tableName, columns)
    }

    TableBatchInsertConsumer forTable(String tableName, String... columns) {
        this.tableName = tableName
        if (columns) {
            this.columns = columns.toList()
        }
        return this
    }

    TableBatchInsertConsumer withCreateTableScript(String createTableScript, String statementSeparator = ';') {
        this.createTableScript = createTableScript
        this.statementSeparator = statementSeparator
        return this
    }

    TableBatchInsertConsumer withDateFormat(SimpleDateFormat dateFormat) {
        this.dateFormat = dateFormat
        return this
    }

    TableBatchInsertConsumer withDateFormat(String dateFormat) {
        return withDateFormat(new SimpleDateFormat(dateFormat))
    }

    protected Map<String, Object> toMap(LogEntry logEntry, boolean addIdIdentity = true) {
        final logValueMap = new TreeMap<String, Object>(String.CASE_INSENSITIVE_ORDER)
        logValueMap.putAll(logEntry.logValueMap)

        final Map<String, Object> resultMap = [:]
        final cols = columns ? columns : logValueMap.keySet().toList()

        final idName = tableMeta.idName
        final idIdentity = tableMeta.idIdentity
        if (addIdIdentity && !idIdentity && !logValueMap.containsKey(idName)) {
            logValueMap[idName] = tableMeta.nextId()
            cols.add(idName)
        }

        for (entry in logValueMap.subMap(cols)) {
            final String column = entry.key
            Object value = entry.value
            final Class type = jdbcTypeClassMap[tableMeta.columnTypeMap[column]]
            if (value && type && !type.isInstance(value)) {
                try {
                    if (Date.isAssignableFrom(type) && dateFormat) {
                        value = dateFormat.parse(value.toString())
                    } else {
                        value = convertType(column, value, type)
                    }
                } catch (Exception e) {
                    throw new RuntimeException("Failed convert column '$column', value: '$value'", e)
                }
            }
            resultMap.put(column, value)
        }
        return resultMap
    }

    protected Object convertType(String column, Object value, Class type) {
        if (Integer.isAssignableFrom(type)) {
            return Integer.parseInt(value.toString())
        } else if (Long.isAssignableFrom(type)) {
            return Long.parseLong(value.toString())
        } else if (Byte.isAssignableFrom(type)) {
            return Byte.parseByte(value.toString())
        } else if (Short.isAssignableFrom(type)) {
            return Short.parseShort(value.toString())
        } else if (Boolean.isAssignableFrom(type)) {
            return Boolean.parseBoolean(value.toString())
        } else if (Float.isAssignableFrom(type)) {
            return Float.parseFloat(value.toString())
        } else if (Double.isAssignableFrom(type)) {
            return Double.parseDouble(value.toString())
        } else if (Date.isAssignableFrom(type)) {
            throw new UnsupportedOperationException("Either provide date format or use '${DateConsumer.name}' for column: '$column' transformation before definition of '${TableBatchInsertConsumer.name}' consumer")
        }
        return value.asType(type)
    }

    protected void consume(List<LogEntry> entries) {
        final Set<String> cols = new LinkedHashSet<>()
        final List<Map<String, Object>> maps = new ArrayList<>()
        for (LogEntry logEntry : entries) {
            final map = toMap(logEntry)
            maps.add(map)
            cols.addAll(map.keySet())
        }
        cols.sort()
        sql.withBatch(entries.size(), "INSERT INTO ${tableMeta.name} (${cols.join(',')}) VALUES (${StringUtils.repeat(',?', cols.size()).substring(1)})".toString()) { BatchingPreparedStatementWrapper ps ->
            for (Map<String, Object> map : maps) {
                final values = new ArrayList()
                for (String col : cols) {
                    values.add(map.get(col))
                }
                ps.addBatch(values)
            }
        }
        entries.clear()
    }

    protected static void executeTableScript(Sql sql, String scriptResource, String separator) {
        String text = scriptResource
        if (scriptResource.toLowerCase().endsWith('.sql')) {
            text = Utils.readResourceText(scriptResource)
        }
        final statements = separator ? text.split(separator).toList() : [scriptResource]
        for (String statement : statements) {
            sql.execute(statement.trim())
        }
    }

    protected TableMeta readTableMeta() {
        tableMeta = listTableMap().get(tableName)
        if (!tableMeta) {
            if (createTableScript) {
                executeTableScript(sql, createTableScript, statementSeparator)
            } else {
                throw new IllegalArgumentException("Table: '$tableName' doesn't exist, no creation script provided")
            }
            tableMeta = listTableMap().get(tableName)
        }
        return tableMeta
    }

    @Override
    synchronized void consume(LogEntry entry) {
        if (!tableMeta) {
            readTableMeta()
        }
        batchQueue.add(entry)
        if (batchQueue.size() == batchSize) {
            consume(batchQueue)
        }
    }

    @Override
    void close() throws IOException {
        if (batchQueue) {
            consume(batchQueue)
        }
        try {
            sql.close()
        } catch (Exception ignore) { }
    }

    protected long getMaxIdentifierValue(String tableName, String idName) {
        try {
            final row = sql.firstRow("SELECT MAX($idName) FROM $tableName".toString())
            return row && row[0] ? row[0] as long : 0
        } catch (Exception e) {
            throw new RuntimeException("Failed get max($idName) for table: '$tableName'", e)
        }
    }

    protected long count() {
        try {
            final row = sql.firstRow("SELECT COUNT($tableMeta.idName) FROM $tableMeta.name".toString())
            return row && row[0] ? row[0] as long : 0
        } catch (Exception e) {
            throw new RuntimeException("Failed get count($tableMeta.idName) for table: '$tableMeta.name'", e)
        }
    }

    protected Map<String, TableMeta> listTableMap() {
        final tables = lookupExistingTables(sql)
        final tableMap = new TreeMap<String, TableMeta>(String.CASE_INSENSITIVE_ORDER)
        for (final String table : tables) {
            tableMap.put(table, readTableMeta(table))
        }
        return tableMap
    }

    protected TableMeta readTableMeta(String tableName) {
        Connection connection = null
        ResultSet rs = null
        final tableMeta = new TableMeta(name: tableName)
        try {
            connection = sql.dataSource.connection
            final databaseMetaData = connection.metaData
            rs = databaseMetaData.getColumns(connection.catalog, null, tableName, null)
            while (rs.next()) {
                final String columnName = rs.getString(4)
                final int columnType = rs.getInt(5)
                final columnProperties = asMap(rs)
                tableMeta.columnMap.put(columnName, columnProperties)
                tableMeta.columnTypeMap.put(columnName, columnType)
            }
        } finally {
            Utils.closeQuietly(rs)
            Utils.closeQuietly(connection)
        }
        final primaryKeys = getPrimaryKeys(sql, tableName)
        tableMeta.ids = primaryKeys
        if (primaryKeys.size() > 1) {
            throw new UnsupportedOperationException("Multiple primary keys are not supported; '$tableName', ids: '${primaryKeys.join(', ')}'")
        }
        tableMeta.idName = primaryKeys[0]
        if (tableMeta.idName) {
            final columnProperties = tableMeta.columnMap.get(tableMeta.idName)
            tableMeta.idIdentity = columnProperties.TYPE_NAME?.toString()?.toLowerCase()?.contains("identity") || columnProperties.IS_AUTOINCREMENT?.toString()?.toLowerCase()?.equals("yes")
            if (!tableMeta.idIdentity) {
                tableMeta.id = getMaxIdentifierValue(tableMeta.name, tableMeta.idName)
            }
        }
        return tableMeta
    }

    protected static List<String> listMssqlTables(DataSource dataSource) {
        return new Sql(dataSource)
                .rows('SELECT table_name FROM information_schema.tables WHERE table_type = \'base table\'')
                .collect { GroovyRowResult it -> it.getAt(0) } as List<String>
    }

    protected static List<String> listH2Tables(DataSource dataSource) {
        return new Sql(dataSource)
                .rows('SELECT DISTINCT table_name FROM information_schema.columns')
                .collect { GroovyRowResult it -> it.getAt(0) } as List<String>
    }

    protected static Set lookupExistingTables(DataSource dataSource) {
        assert dataSource: 'DataSource instance should be defined'
        Connection connection = null
        ResultSet rs = null
        List<String> result = new ArrayList<String>()
        try {
            connection = dataSource.connection
            final databaseMetaData = connection.metaData
            final databaseProductName = connection.metaData.databaseProductName
            if (databaseProductName.contains('Microsoft')) {
                result = listMssqlTables(dataSource)
            } else if (databaseProductName.contains('H2')) {
                result = listH2Tables(dataSource)
            } else {
                final String[] types = { 'TABLE' } as String[]
                rs = databaseMetaData.getTables(connection.catalog, null, null, types)
                while (rs.next()) {
                    result.add(rs.getString("TABLE_NAME"))
                }
            }
        } finally {
            Utils.closeQuietly(rs)
            Utils.closeQuietly(connection)
        }
        return result.toSet()
    }

    protected static Set<String> lookupExistingTables(Sql sql) {
        assert sql: 'Sql instance should be defined'
        return lookupExistingTables(sql.dataSource)
    }

    protected static List<String> getPrimaryKeys(Sql sql, String table) {
        Connection connection = null
        ResultSet rs = null
        final List<String> result = new ArrayList<String>()
        try {
            connection = sql.dataSource.connection
            rs = connection.metaData.getPrimaryKeys(connection.catalog, null, table)
            while (rs.next()) {
                result.add(rs.getString(4))
            }
        } finally {
            Utils.closeQuietly(rs)
            Utils.closeQuietly(connection)
        }
        return result
    }

    protected static Map<String, Object> asMap(ResultSet rs) {
        final props = new TreeMap<String, Object>(String.CASE_INSENSITIVE_ORDER)
        final ResultSetMetaData rsmd = rs.getMetaData()
        final int columnCount = rsmd.getColumnCount()
        for (int i = 0; i < columnCount; i++) {
            props.put(rsmd.getColumnName(i + 1), rs.getObject(i + 1))
        }
        return props
    }

    static TableBatchInsertConsumer of(DataSource dataSource, String tableName, String... columns) {
        return new TableBatchInsertConsumer(dataSource).forTable(tableName, columns)
    }

    @CompileStatic
    @PackageScope
    static class TableMeta {
        String name

        List<String> ids = []
        String idName

        Map<String, Map> columnMap = new TreeMap<String, Map>(String.CASE_INSENSITIVE_ORDER)
        Map<String, Integer> columnTypeMap = new TreeMap<String, Integer>(String.CASE_INSENSITIVE_ORDER)
        boolean idIdentity
        long id

        long nextId() {
            return ++id
        }
    }
}
