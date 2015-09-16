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
class TableValue extends Constant<Map<String, Object>> {
    private final DataSet dataSet
    private final TableMeta tableMeta

    private String select
    private Object params

    private Closure where

    TableValue(DataSource dataSource, TableMeta tableMeta) {
        this.dataSet = new DataSet(new Sql(dataSource), tableMeta.name)
        this.tableMeta = tableMeta
        dataSet.withStatement { Statement statement ->
            statement.setFetchSize(1)
        }
    }

    TableValue select(String select) {
        this.select = select
        return this
    }

    TableValue select(String select, Map params) {
        this.select = select
        this.params = params
        return this
    }

    TableValue select(String select, Object[] params) {
        this.select = select
        this.params = params
        return this
    }

    TableValue select(String select, List params) {
        this.select = select
        this.params = params
        return this
    }

    TableValue where(Closure closure) {
        this.where = closure
        return this
    }

    TableValue withStatement(Closure statementClosure) {
        this.dataSet.withStatement statementClosure
        return this
    }

    @Override
    protected Map<String, Object> doNext() {
        if (where) {
            return (Map<String, Object>) dataSet.findAll(where).firstRow()
        }
        String select = this.select
        if (!select) {
            select = "SELECT ${tableMeta.idFields.join(', ')} FROM $tableMeta.name ORDER BY ${tableMeta.idFields.join('DESC, ')}"
            select += ' DESC'
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

}
