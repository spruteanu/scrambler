package org.prismus.scrambler.jdbc

import groovy.sql.GroovyRowResult
import groovy.sql.Sql
import groovy.transform.CompileStatic
import org.prismus.scrambler.value.ValueDefinition

import javax.sql.DataSource
import java.sql.*

/**
 * todo: add description
 *
 * @author Serge Pruteanu
 */
@CompileStatic
class DatabaseScrambler {
    static Map<Integer, Class> typeClassMap = [
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

    private final DataSource dataSource
    private final Map<String, TableMeta> tableMap
    private final Map<String, String> fkTableMap = [:] as Map<String, String>
    private boolean generateNullable = true

    private List<TableMeta> tables = new ArrayList<TableMeta>()

    private ValueDefinition definition

    DatabaseScrambler(DataSource dataSource) {
        this.dataSource = dataSource
        this.definition = new ValueDefinition()
        this.tableMap = listTableMap()
    }

    DatabaseScrambler registerTypeClass(int type, Class clazzType) {
        typeClassMap.put(type, clazzType)
        return this
    }

    DatabaseScrambler generateAll() {
        this.generateNullable = true
        return this
    }

    DatabaseScrambler generateStrict() {
        this.generateNullable = false
        return this
    }

    DatabaseScrambler forTable(String table) {
        if (!tableMap.containsKey(table)) {
            throw new IllegalArgumentException("'$table' is not found in provided datasource")
        }
        tables.add(tableMap.get(table))
        return this
    }

    DatabaseScrambler usingDefinition(ValueDefinition definition) {
        this.definition = definition
        return this
    }

    DatabaseScrambler usingDefinition(String... definitions) {
        definition.usingDefinitions(definitions)
        return this
    }

    DatabaseScrambler scanDefinition(String definition, String... definitions) {
        this.definition.scanDefinitions(definition, definitions)
        return this
    }

    DatabaseScrambler scanLibraryDefinition(String definitionMatcher) {
        definition.usingLibraryDefinitions(definitionMatcher)
        return this
    }

    ValueDefinition getDefinition() {
        return definition
    }

    boolean getGenerateNullable() {
        return generateNullable
    }

    static Map<Integer, Class> getTypeClassMap() {
        return typeClassMap
    }

    protected void sortTablesByFkDependency() {
        Collections.sort(tables, new Comparator<TableMeta>() {
            @Override
            int compare(TableMeta left, TableMeta right) {
                return right.hasFkDependency(left.name) ? 1 : 0
            }
        })
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
        for (final String table : tables) {
            tableMap.put(table, getTableMeta(table))
        }
        return tableMap
    }

    protected TableMeta getTableMeta(String table) {
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
            result.idFields = getPrimaryKeys(table)
            result.fkMap = getForeignKeys(table)
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

    protected Map<String, Map<String, Object>> getForeignKeys(String table) {
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
                fkTableMap.put(buildFkTableKey(fkProps.get('FKTABLE_NAME').toString(), fkProps.get('FKCOLUMN_NAME').toString()), table)
            }
        } finally {
            Util.closeQuietly(rs)
            Util.closeQuietly(connection)
        }
        return result
    }

    protected String buildFkTableKey(String table, String column) {
        return "$table.$column"
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

}
