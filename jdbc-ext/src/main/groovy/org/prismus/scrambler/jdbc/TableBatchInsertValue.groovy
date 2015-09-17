package org.prismus.scrambler.jdbc

import groovy.sql.BatchingPreparedStatementWrapper
import groovy.sql.DataSet
import groovy.sql.Sql
import groovy.transform.CompileStatic
import groovy.transform.PackageScope
import org.prismus.scrambler.Value
import org.prismus.scrambler.value.Constant

import javax.sql.DataSource

/**
 * todo: add description
 *
 * @author Serge Pruteanu
 */
@CompileStatic
class TableBatchInsertValue extends Constant<List<Map<String, Object>>> {
    final DataSet dataSet
    final Map<String, Value> valueMap
    final int count

    private final String insertStatement

    TableBatchInsertValue(DataSource dataSource, String table, Map<String, Value> valueMap, int count) {
        this.dataSet = new DataSet(new Sql(dataSource), table)
        this.valueMap = valueMap
        this.count = count
        insertStatement = Util.buildInsertStatement(table, valueMap.keySet().sort())
    }

    @Override
    protected List<Map<String, Object>> doNext() {
        final rows = new ArrayList<Map<String, Object>>(count)
        dataSet.withTransaction {
            dataSet.withBatch(count, insertStatement) { final BatchingPreparedStatementWrapper statement ->
                for (int i = 0; i < count; i++) {
                    final Map<String, Object> rowMap = generateRowMap()
                    rows.add(rowMap)
                    statement.addBatch(rowMap)
                }
            }
        }
        return rows
    }

    @PackageScope
    Map<String, Object> generateRowMap() {
        final rowMap = new LinkedHashMap<String, Object>(valueMap.size())
        for (Map.Entry<String, Value> entry : valueMap.entrySet()) {
            rowMap.put(entry.key, entry.value.next())
        }
        return rowMap
    }

}
