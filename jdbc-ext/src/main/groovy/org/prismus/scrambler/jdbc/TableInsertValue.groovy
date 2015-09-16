package org.prismus.scrambler.jdbc

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
class TableInsertValue extends Constant<Map<String, Object>> {
    final DataSet dataSet

    TableInsertValue(DataSource dataSource, String table) {
        this.dataSet = new DataSet(new Sql(dataSource), table)
    }

    protected void insertData(String insertStatement, Map rowMap) {
        dataSet.executeInsert(rowMap, insertStatement)
    }

}
