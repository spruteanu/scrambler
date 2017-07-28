package org.prismus.scrambler.log

import groovy.transform.CompileStatic
import org.apache.commons.beanutils.ConvertUtilsBean
import org.apache.commons.beanutils.converters.DateConverter

import javax.sql.DataSource

/**
 * @author Serge Pruteanu
 */
@CompileStatic
class DbTableBatchConsumer implements LogConsumer {
    private static Map<Integer, Class> jdbcTypeClassMap = createJdbcTypeClassMap()
    private static ConvertUtilsBean convertUtilsBean = createConvertUtilsBean()

    String tableName
    DataSource dataSource
//    SimpleJdbcInsert insertTemplate

    int id = -1
    String idPropertyName = 'ID'
    boolean idIdentity

    Map<String, Class> propertyTypeMap = [:]
    private Map<String, Map> columnPropertiesMap = [:]
    private Map<String, Object> tableMetadata = [:]
//    private TableMetaDataProvider metaDataProvider

    @Override
    void consume(LogEntry entry) {
        // todo implement me
    }
/*
    def write(Collection entities) {
        checkInitialized()
        final insertRecords = new ArrayList<SqlParameterSource>(entities.size())
        for (entity in entities) {
            def properties = asMap(entity)
            properties = checkPropertiesConvert(properties)
            if (properties) {
                insertRecords << new MapSqlParameterSource(properties)
            }
        }
        try {
            doBatchInsert(insertRecords.toArray(new SqlParameterSource[insertRecords.size()]))
        } catch (Exception e) {
            throw new RuntimeException("Failed insert '${tableName}' entites", e)
        }
    }

    protected Map asMap(def entity) {
        final Map properties
        if (entity instanceof Map) {
            properties = new CaseInsensitiveMap(entity)
        } else {
            properties = new CaseInsensitiveMap(entity.properties)
        }
        return properties
    }

    Map checkPropertiesConvert(Map properties, boolean addIdIdentity = true) {
        if (addIdIdentity && !idIdentity && !properties.containsKey(idPropertyName)) {
            properties[idPropertyName] = ++id
        }
        for (entry in properties) {
            final String property = entry.key
            Object value = entry.value
            final Class type = propertyTypeMap[property]
            if (value && type && !type.isInstance(value)) {
                try {
                    if (value != null && !(value instanceof String)) {
                        value = value.toString()
                    }
                    properties[property] = convertUtilsBean.convert(value, type)
                } catch (Exception e) {
                    throw new RuntimeException("Failed convert property '${property}', value: '${value}'", e)
                }
            }
        }
        return properties
    }

    def checkInitialized() {
        if (insertTemplate) {
            return
        }
        assert tableName: 'Table name should be defined'
        assert dataSource: 'Data Source should be defined'

        createInsertTemplate()
    }

    protected def createInsertTemplate() {
        metaDataProvider = TableMetaDataProviderFactory.createMetaDataProvider(dataSource, new TableMetaDataContext(tableName: tableName))
        final Map<String, Class> typeMap = lookupPropertyTypeMap()
        try {
            columnPropertiesMap = new CaseInsensitiveMap(lookupColumnPropertyTypeMap())
            checkIdentifierValue()
            if (idIdentity) {
                typeMap.remove(idPropertyName)
            }
            propertyTypeMap = new CaseInsensitiveMap(typeMap)
            insertTemplate = new SimpleJdbcInsert(dataSource).withTableName(tableName).usingColumns(typeMap.keySet().toArray(new String[typeMap.size()]))
        } catch (Exception e) {
            throw new RuntimeException("Failed create insert template for table: '${tableName}'", e)
        }
    }

    protected Map<String, Class> lookupPropertyTypeMap() {
        final Map<String, Class> typeMap = [:]
        for (parameterMetadata in metaDataProvider.getTableParameterMetaData()) {
            typeMap[parameterMetadata.parameterName] = jdbcTypeClassMap[parameterMetadata.sqlType]
        }
        return typeMap
    }

    protected def getTableMetadata(TableMetaDataProvider metaDataProvider, DatabaseMetaData databaseMetaData) {
        def resultSet = databaseMetaData.getTables(null, null, metaDataProvider.tableNameToUse(tableName), null)
        def results = [:]
        try {
            final resultSetMetaData = resultSet.getMetaData()
            while (resultSet.next()) {
                def tableMetaProperties = [:]
                for (int i = 0; i < resultSetMetaData.getColumnCount(); i++) {
                    tableMetaProperties[resultSetMetaData.getColumnName(i + 1)] = resultSet.getObject(i + 1)
                }
                results = tableMetaProperties
                break
            }
        } catch (Exception e) {
            throw new RuntimeException("Error while accessing table meta data results: ${e.getMessage()}", e)
        } finally {
            resultSet.close()
        }
        return results
    }

    protected Map<String, Map> lookupColumnPropertyTypeMap() {
        final Map<String, Map> typeMap = [:]
        try {
            final connection = dataSource.getConnection()
            final databaseMetaData = connection.getMetaData()
            tableMetadata = getTableMetadata(metaDataProvider, databaseMetaData)
            final resultSet = databaseMetaData.getColumns(
                    (String) tableMetadata.TABLE_CAT, (String) tableMetadata.TABLE_SCHEM,
                    metaDataProvider.tableNameToUse(tableName), null
            )
            try {
                final resultSetMetaData = resultSet.getMetaData()
                while (resultSet.next()) {
                    final properties = [:]
                    for (int i = 0; i < resultSetMetaData.getColumnCount(); i++) {
                        properties[resultSetMetaData.getColumnName(i + 1)] = resultSet.getObject(i + 1)
                    }
                    typeMap[properties.COLUMN_NAME] = properties
                }
            } finally {
                resultSet.close()
                connection.close()
            }
        } catch (Exception e) {
            throw new RuntimeException("Failed get primary identifier info for table: ${tableName}", e)
        }
        return typeMap
    }

    protected final def checkIdentifierPropertyName() {
        final connection = dataSource.getConnection()
        try {
            final resultSet = connection.getMetaData().getPrimaryKeys((String) tableMetadata.TABLE_CAT, (String) tableMetadata.TABLE_SCHEM, metaDataProvider.tableNameToUse(tableName))
            def primaryKeys = []
            try {
                while (resultSet.next()) {
                    primaryKeys << resultSet.getString("COLUMN_NAME")
                }
                final idCount = primaryKeys.size()
                if (idCount == 1) {
                    idPropertyName = primaryKeys[0]
                    final columnProperties = columnPropertiesMap.get(idPropertyName)
                    idIdentity = columnProperties.TYPE_NAME?.toString()?.toLowerCase()?.contains("identity") || columnProperties.IS_AUTOINCREMENT?.toString()?.toLowerCase()?.equals("yes")
                } else {
                    if (idCount > 1) {
                        throw new UnsupportedOperationException("Only one identifier property is supported; table: ${tableName}, found identifiers: ${primaryKeys}")
                    }
                }
            } finally {
                resultSet.close()
            }
        } catch (Exception e) {
            throw new RuntimeException("Failed get primary identifier info for table: ${tableName}", e)
        } finally {
            connection.close()
        }
    }

    protected final def checkIdentifierValue() {
        if (id == -1) {
            checkIdentifierPropertyName()
            id = getMaxIdentifierValue(tableName)
        }
    }

    protected final def getMaxIdentifierValue(def tableName) {
        try {
            return new JdbcTemplate(dataSource).queryForInt("SELECT MAX(${idPropertyName}) FROM ${tableName}")
        } catch (Exception e) {
            throw new RuntimeException("Failed get count for table: '${tableName}'", e)
        }
    }

    protected final def getMinIdentifierValue(def tableName, Map<String, ?> parameters) {
        try {
            if (parameters) {
                return new JdbcTemplate(dataSource).queryForInt("SELECT MIN(${idPropertyName}) FROM ${tableName} WHERE ${generateWhereStatement(parameters)}", parameters.values().toArray())
            } else {
                return new JdbcTemplate(dataSource).queryForInt("SELECT MIN(${idPropertyName}) FROM ${tableName}")
            }
        } catch (Exception e) {
            throw new RuntimeException("Failed get count for table: '${tableName}'", e)
        }
    }


    protected String generateDeleteStatement(Map<String, ?> propertyValues) {
        return "DELETE FROM $tableName WHERE ${generateWhereStatement(propertyValues)}"
    }

    protected void deleteTableContent(Sql sql, Map propertyValues) {
        final deleteParameters = new LinkedHashMap(propertyValues)
        sql.execute(generateDeleteStatement(deleteParameters), checkPropertiesConvert(deleteParameters, false).values().toList())
    }


    void executeResourceScripts(String resourceName, Sql sql, Map propertyValues = null) {
        final statements = lookupResourceStatements(resourceName)
        if (statements) {
            final template = new NamedParameterJdbcTemplate(sql.dataSource)
            final parameters = propertyValues != null ? new LinkedHashMap(propertyValues) : [:]
            checkPropertiesConvert(parameters, false)
            for (final String statement : statements) {
                template.update(statement, parameters)
            }
        }
    }

    protected def lookupResourceStatements(final String resourceName) {
        final InputStream inputStream = this.class.getResourceAsStream(resourceName)
        try {
            return inputStream ? new SqlStatementWriter().splitStatements(inputStream.text) : null
        } finally {
            inputStream?.close()
        }
    }

    static String generateWhereStatement(Map<String, ?> propertyValues) {
        return propertyValues.keySet().collect { "$it = ?" }.join(' AND ')
    }

    static Map<String, Integer> getTablesRecordCount(Sql sql) {
        return getTablesRecordCount(sql, true)
    }

    static Map<String, Integer> getTablesRecordCount(Sql sql, boolean countAllNotEmpty) {
        assert sql: 'Sql instance should be defined'
        def final tableRecordCountMap = [:]
        for (table in getExistingTables(sql)) {
            try {
                final rowsCount = count(sql, table)
                tableRecordCountMap[table] = rowsCount
                if (!countAllNotEmpty && rowsCount > 0) {
                    break
                }
            } catch (Exception e) {
                throw new RuntimeException("Failed get table: '${table}' counts", e)
            }
        }
        return tableRecordCountMap
    }

    static def count(Sql sql, String table, Map<String, ?> parameters = null) {
        String sqlStatement = "SELECT COUNT(*) FROM [${table}]"
        if (parameters) {
            sqlStatement += " WHERE ${generateWhereStatement(parameters)}"
            return sql.rows(sqlStatement, parameters.values().toArray())[0][0]
        } else {
            return sql.rows(sqlStatement)[0][0]
        }
    }

    static Set<String> getExistingTables(Sql sql) {
        assert sql: 'Sql instance should be defined'
        return getExistingTables(sql.dataSource)
    }

    static Set getExistingTables(DataSource dataSource) {
        assert dataSource: 'DataSource instance should be defined'
        def final existingTables = [] as Set
        final Connection connection = dataSource.connection
        try {
            final DatabaseMetaData databaseMetaData = connection.getMetaData()
            final ResultSet tablesResultSet = databaseMetaData.getTables(null, null, null, "TABLE")
            try {
                while (tablesResultSet.next()) {
                    existingTables << tablesResultSet.getString("TABLE_NAME").toUpperCase()
                }
            } finally {
                tablesResultSet.close()
            }
        } finally {
            connection.close()
        }
        return existingTables
    }

    */

    private static ConvertUtilsBean createConvertUtilsBean() {
        final ConvertUtilsBean convertUtilsBean = new ConvertUtilsBean()
//        convertUtilsBean.register(true, true, 0)
        final DateConverter dateConverter = new DateConverter()
        dateConverter.setPatterns([
                "yyyy-MM-dd", "MM/dd/yyyy",
                "MM/dd/yyyy HH:mm:ss.SSS", "MM/dd/yyyy HH:mm:ss",
                "EEE MMM dd HH:mm:ss zzz yyyy", "yyyyMMdd"
        ].toArray(new String[5]))
        dateConverter.setUseLocaleFormat(true)
        convertUtilsBean.register(dateConverter, java.util.Date.class)
        return convertUtilsBean
    }

    private static Map<Integer, Class> createJdbcTypeClassMap() {
        final jdbcTypeClassMap = [:]
        jdbcTypeClassMap.with {
            put(java.sql.Types.CHAR, String.class)
            put(java.sql.Types.VARCHAR, String.class)
            put(java.sql.Types.NCHAR, String.class)
            put(java.sql.Types.NVARCHAR, String.class)
            put(java.sql.Types.LONGVARCHAR, String.class)
            put(java.sql.Types.CLOB, String.class)
            put(java.sql.Types.NCLOB, String.class)

            put(java.sql.Types.NUMERIC, BigDecimal.class)
            put(java.sql.Types.REAL, Double.class)
            put(java.sql.Types.FLOAT, Float.class)
            put(java.sql.Types.DOUBLE, Double.class)
            put(java.sql.Types.DECIMAL, BigDecimal.class)

            put(java.sql.Types.BIT, Byte.class)
            put(java.sql.Types.BOOLEAN, Boolean.class)
            put(java.sql.Types.TINYINT, Byte.class)
            put(java.sql.Types.SMALLINT, Short.class)
            put(java.sql.Types.INTEGER, Integer.class)
            put(java.sql.Types.BIGINT, Long.class)
            put(java.sql.Types.DATE, Date.class)
            put(java.sql.Types.TIME, Date.class)
            put(java.sql.Types.TIMESTAMP, Date.class)
        }
        return jdbcTypeClassMap
    }
}
