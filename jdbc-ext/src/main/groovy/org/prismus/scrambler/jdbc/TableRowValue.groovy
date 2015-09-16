package org.prismus.scrambler.jdbc

import groovy.sql.DataSet
import groovy.sql.Sql
import groovy.transform.CompileStatic
import org.prismus.scrambler.value.Constant

import javax.sql.DataSource
import java.sql.Statement

/**
 * todo: add description
 *
 * @author Serge Pruteanu
 */
@CompileStatic
class TableRowValue extends Constant<Map<String, Object>> {
    private final DataSet dataSet

    private String select
    private Object params

    private Closure where

    TableRowValue(DataSource dataSource, String table) {
        this.dataSet = new DataSet(new Sql(dataSource), table)
        dataSet.withStatement { Statement statement ->
            statement.setFetchSize(1)
        }
    }

    TableRowValue(DataSource dataSource, String table, String select) {
        this(dataSource, table)
        this.select = select
    }

    TableRowValue(DataSource dataSource, String table, String select, List params) {
        this(dataSource, table, select)
        this.params = params
    }

    TableRowValue(DataSource dataSource, String table, String select, Object[] params) {
        this(dataSource, table, select)
        this.params = params
    }

    TableRowValue(DataSource dataSource, String table, Closure where) {
        this(dataSource, table)
        this.where = where
    }

    TableRowValue withStatement(Closure statementClosure) {
        this.dataSet.withStatement statementClosure
        return this
    }

    @Override
    protected Map<String, Object> doNext() {
        if (where) {
            return (Map<String, Object>) dataSet.findAll(where).firstRow()
        }
        if (params) {
            if (params instanceof Map) {
                return (Map<String, Object>) dataSet.firstRow((Map) params, select)
            } else {
                return (Map<String, Object>) dataSet.firstRow(select, params)
            }
        } else {
            return (Map<String, Object>) dataSet.firstRow(select)
        }
    }

    static TableRowValue of(DataSource dataSource, String table, String select) {
        return new TableRowValue(dataSource, table, select)
    }

    static TableRowValue of(DataSource dataSource, String table, String select, Map params) {
        return new TableRowValue(dataSource, table, select)
    }

    static TableRowValue of(DataSource dataSource, String table, String select, Object[] params) {
        return new TableRowValue(dataSource, table, select)
    }

    static TableRowValue of(DataSource dataSource, String table, String select, List params) {
        return new TableRowValue(dataSource, table, select)
    }

    static TableRowValue of(DataSource dataSource, String table, Closure where) {
        return new TableRowValue(dataSource, table, where)
    }

}
