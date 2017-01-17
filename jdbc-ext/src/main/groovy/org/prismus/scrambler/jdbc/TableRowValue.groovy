package org.prismus.scrambler.jdbc

import groovy.sql.DataSet
import groovy.sql.Sql
import groovy.transform.CompileStatic
import org.prismus.scrambler.value.Constant

import javax.sql.DataSource
import java.sql.Statement

/**
 * Value instance that returns an object from DB based on provided DB selection string and parameters.
 *
 * @author Serge Pruteanu
 */
@CompileStatic
class TableRowValue extends Constant<Map<String, Object>> {
    private final DataSet dataSet

    private String select
    private Object params

    private Closure where
    private boolean constantValue

    TableRowValue(DataSource dataSource, String table, boolean constantValue = false) {
        this.dataSet = new DataSet(new Sql(dataSource), table)
        dataSet.withStatement { Statement statement ->
            statement.setFetchSize(1)
        }
        this.constantValue = constantValue
    }

    TableRowValue(DataSource dataSource, String table, String select, boolean constantValue = false) {
        this(dataSource, table)
        this.select = select
        this.constantValue = constantValue
    }

    TableRowValue(DataSource dataSource, String table, String select, List params, boolean constantValue = false) {
        this(dataSource, table, select)
        this.params = params
        this.constantValue = constantValue
    }

    TableRowValue(DataSource dataSource, String table, String select, Object[] params, boolean constantValue = false) {
        this(dataSource, table, select)
        this.params = params
        this.constantValue = constantValue
    }

    TableRowValue(DataSource dataSource, String table, Closure where, boolean constantValue = false) {
        this(dataSource, table)
        this.where = where
        this.constantValue = constantValue
    }

    TableRowValue withStatement(Closure statementClosure) {
        this.dataSet.withStatement statementClosure
        return this
    }

    @Override
    protected Map<String, Object> doNext() {
        if (constantValue && value != null) {
            return (Map<String, Object>) value
        }
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

    /**
     * Creates an instance of table selection value based on provided parameters
     *
     * @param dataSource DB datasource instance
     * @param table table name
     * @param select DB selection string "select max('age') from Person where name='%ete'"
     */
    static TableRowValue of(DataSource dataSource, String table, String select, boolean constantValue = false) {
        return new TableRowValue(dataSource, table, select, constantValue)
    }

    /**
     * Creates an instance of table selection value based on provided parameters
     *
     * @param dataSource DB datasource instance
     * @param table table name
     * @param select DB selection string "select * from PROJECT where name=:foo"
     * @param params selection parameters, example: [foo:'Gradle']
     */
    static TableRowValue of(DataSource dataSource, String table, String select, Map params, boolean constantValue = false) {
        return new TableRowValue(dataSource, table, select, params, constantValue)
    }

    /**
     * Creates an instance of table selection value based on provided parameters
     *
     * @param dataSource DB datasource instance
     * @param table table name
     * @param select DB selection string "select * from PERSON where lastname like ?"
     * @param params selection parameters, example: new Object[] {"%ete%"}
     */
    static TableRowValue of(DataSource dataSource, String table, String select, Object[] params, boolean constantValue = false) {
        return new TableRowValue(dataSource, table, select, params, constantValue)
    }

    /**
     * Creates an instance of table selection value based on provided parameters
     *
     * @param dataSource DB datasource instance
     * @param table table name
     * @param select DB selection string "select * from PERSON where lastname like ?"
     * @param params selection parameters, example: Arrays.asList("%ete%")
     */
    static TableRowValue of(DataSource dataSource, String table, String select, List params, boolean constantValue = false) {
        return new TableRowValue(dataSource, table, select, params, constantValue)
    }

    /**
     * Creates an instance of table selection value based on provided parameters
     *
     * @param dataSource DB datasource instance
     * @param table table name
     * @param select DB selection string "select * from PERSON where lastname like ?"
     * @param params selection parameters, example: { ResultSet rs ->
     *     while (rs.next()) println rs.getString('firstname') + ' ' + rs.getString(3)
     *}
     */
    static TableRowValue of(DataSource dataSource, String table, Closure where, boolean constantValue = false) {
        return new TableRowValue(dataSource, table, where, constantValue)
    }

}
