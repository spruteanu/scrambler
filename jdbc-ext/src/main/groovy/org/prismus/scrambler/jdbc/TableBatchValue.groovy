package org.prismus.scrambler.jdbc

import groovy.sql.BatchingPreparedStatementWrapper
import groovy.sql.DataSet
import groovy.sql.Sql
import groovy.transform.CompileStatic
import org.prismus.scrambler.value.Constant

import javax.sql.DataSource

/**
 * todo: add description
 *
 * @author Serge Pruteanu
 */
@CompileStatic
class TableBatchValue extends Constant<List<Map<String, Object>>> {
    final DataSet dataSet

    TableBatchValue(DataSource dataSource, String table) {
        this.dataSet = new DataSet(new Sql(dataSource), table)
    }

    protected void insertData(String table, String insertStatement, List<Map> rows) {
        dataSet.withTransaction {
            final counts = dataSet.withBatch(rows.size(), insertStatement) {
                final BatchingPreparedStatementWrapper statement ->
                    for (final rowMap : rows) {
                        statement.addBatch(new LinkedHashMap(rowMap))
                    }
            }
            if (counts == null || counts.length == 0) {
                throw new RuntimeException("Data for table $table are not inserted")
            }
        }
    }

}
