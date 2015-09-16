package org.prismus.scrambler.jdbc

import groovy.sql.DataSet
import groovy.sql.Sql
import groovy.transform.CompileStatic
import org.prismus.scrambler.Value
import org.prismus.scrambler.value.Constant

import javax.sql.DataSource
import java.sql.Connection
import java.sql.ResultSet
import java.sql.Statement

/**
 * todo: add description
 *
 * @author Serge Pruteanu
 */
@CompileStatic
class TableInsertValue extends Constant<Map<String, Object>> {
    final DataSet dataSet
    final Map<String, Value> valueMap

    private final String insertStatement
    private final List<String> sortedColumns

    TableInsertValue(DataSource dataSource, String table, Map<String, Value> valueMap) {
        this.dataSet = new DataSet(new Sql(dataSource), table)
        this.valueMap = valueMap
        sortedColumns = valueMap.keySet().sort()
        insertStatement = Util.buildInsertStatement(table, sortedColumns)
    }

    @Override
    protected Map<String, Object> doNext() {
        final Map<String, Object> rowMap = new LinkedHashMap<String, Object>(valueMap.size())
        dataSet.withTransaction { Connection connection ->
            final statement = connection.prepareStatement(insertStatement, Statement.RETURN_GENERATED_KEYS)
            ResultSet resultSet = null
            try {
                int i = 1
                for (final String column : sortedColumns) {
                    final value = valueMap.get(column)
                    rowMap.put(column, value.next())
                    statement.setObject(i++, value.get())
                }

                final update = statement.executeUpdate()
                if (update) {
                    resultSet = statement.getGeneratedKeys()
                    rowMap.putAll(Util.asMap(resultSet))
                }
            } finally {
                Util.closeQuietly(resultSet)
                Util.closeQuietly(statement)
            }
        }
        return rowMap
    }

}
