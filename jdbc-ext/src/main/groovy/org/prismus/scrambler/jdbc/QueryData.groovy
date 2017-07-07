package org.prismus.scrambler.jdbc

import groovy.sql.GroovyRowResult
import groovy.sql.Sql
import groovy.transform.CompileStatic
import org.prismus.scrambler.CollectionScrambler
import org.prismus.scrambler.Data
import org.prismus.scrambler.data.ConstantData

import javax.sql.DataSource

/**
 * Data instance that returns field/value map from DB based on provided query and parameters.
 *
 * @author Serge Pruteanu
 */
@CompileStatic
class QueryData extends ConstantData<Map<String, Object>> {
    private final DataSource dataSource

    private String query
    private Object params

    private Closure whereClosure
    private boolean constantQuery

    QueryData(DataSource dataSource, String query, boolean constantQuery = false) {
        this.dataSource = dataSource
        this.query = query
        this.constantQuery = constantQuery
    }

    QueryData(DataSource dataSource, String query, Object params, boolean constantQuery = false) {
        this(dataSource, query, constantQuery)
        this.params = params
    }

    QueryData(DataSource dataSource, String query, Closure whereClosure, boolean constantQuery = false) {
        this(dataSource, query, constantQuery)
        this.whereClosure = whereClosure
    }

    protected Map<String, Object> getQueryRow(List<GroovyRowResult> rows) {
        if (rows.size() > 1) {
            throw new IllegalArgumentException("Provided query: '$query' with params: $params returned more than one rows: ${rows.size()}")
        } else if (rows.size() == 0) {
            throw new IllegalArgumentException("No data found with provided query: '$query' and params: $params")
        }
        return (Map<String, Object>) rows.first()
    }

    @Override
    protected Map<String, Object> doNext() {
        if (constantQuery && object != null) {
            return (Map<String, Object>) object
        }
        final sql = new Sql(dataSource)
        try {
            if (whereClosure) {
                return getQueryRow(sql.rows(query, whereClosure))
            }
            if (params) {
                if (params instanceof Map) {
                    return getQueryRow(sql.rows((Map) params, query))
                } else {
                    return getQueryRow(sql.rows(query, params))
                }
            } else {
                return getQueryRow(sql.rows(query))
            }
        } finally {
            sql.close()
        }
    }

    protected List<Map<String, Object>> queryRows() {
        final sql = new Sql(dataSource)
        List<Map<String, Object>> resultList = Collections.emptyList()
        try {
            if (whereClosure) {
                resultList = sql.rows(query, whereClosure) as List<Map<String, Object>>
            }
            if (params) {
                if (params instanceof Map) {
                    resultList = sql.rows((Map) params, query) as List<Map<String, Object>>
                } else {
                    resultList = sql.rows(query, params) as List<Map<String, Object>>
                }
            } else {
                resultList = sql.rows(query) as List<Map<String, Object>>
            }
        } finally {
            sql.close()
        }
        return resultList
    }

    /**
     * Creates an instance that queries DB based on provided selection query
     *
     * @param dataSource DB datasource instance
     * @param table table name
     * @param query DB selection string; example: "SELECT MAX('age') FROM Person WHERE name='%ete'"
     */
    static QueryData of(DataSource dataSource, String query, boolean constantQuery = false) {
        return new QueryData(dataSource, query, constantQuery)
    }

    /**
     * Creates an instance that queries DB based on provided selection query with parameters
     *
     * @param dataSource DB datasource instance
     * @param query DB selection string; example: "SELECT eta FROM project WHERE name=:foo"
     * @param params selection parameters, example: [foo:'Gradle']
     */
    static QueryData of(DataSource dataSource, String query, Map params, boolean constantQuery = false) {
        return new QueryData(dataSource, query, params, constantQuery)
    }

    /**
     * Creates an instance that queries DB based on provided selection query with parameters
     *
     * @param dataSource DB datasource instance
     * @param query DB selection string; example: "SELECT fullName FROM person WHERE lastname like ?"
     * @param params selection parameters, example: new Object[] {"%ete%"}
     */
    static QueryData of(DataSource dataSource, String query, Object[] params, boolean constantQuery = false) {
        return new QueryData(dataSource, query, params, constantQuery)
    }

    /**
     * Creates an instance that queries DB based on provided selection query with parameters
     *
     * @param dataSource DB datasource instance
     * @param table table name
     * @param query DB selection string "SELECT fullName FROM person WHERE lastname like ?"
     * @param params selection parameters, example: Arrays.asList("%ete%")
     */
    static QueryData of(DataSource dataSource, String query, List params, boolean constantQuery = false) {
        return new QueryData(dataSource, query, params, constantQuery)
    }

    /**
     * Creates an instance that queries DB based on provided selection query and closure
     *
     * @param dataSource DB datasource instance
     * @param query DB selection string "SELECT fullName FROM person WHERE lastname like ?"
     * @param where statement closure, example: { ResultSet rs ->
     *     while (rs.next()) println rs.getString('firstname') + ' ' + rs.getString(3)
     *}
     */
    static QueryData of(DataSource dataSource, String query, Closure where, boolean constantQuery = false) {
        return new QueryData(dataSource, query, where, constantQuery)
    }

    /**
     * Creates a {@code Data} instance that will return a random row using provided query
     *
     * @param dataSource DB datasource instance
     * @param table table name
     * @param query DB selection string; example: "SELECT MAX('age') FROM Person WHERE name='%ete'"
     */
    static Data<Map<String, Object>> randomOf(DataSource dataSource, String query) {
        return CollectionScrambler.randomOf(new QueryData(dataSource, query).queryRows())
    }

    /**
     * Creates a {@code Data} instance that will return a random row using provided query with parameters
     *
     * @param dataSource DB datasource instance
     * @param query DB selection string; example: "SELECT eta FROM project WHERE name=:foo"
     * @param params selection parameters, example: [foo:'Gradle']
     */
    static Data<Map<String, Object>> randomOf(DataSource dataSource, String query, Map params) {
        return CollectionScrambler.randomOf(new QueryData(dataSource, query, params).queryRows())
    }

    /**
     * Creates a {@code Data} instance that will return a random row using provided query with parameters
     *
     * @param dataSource DB datasource instance
     * @param query DB selection string; example: "SELECT fullName FROM person WHERE lastname like ?"
     * @param params selection parameters, example: new Object[] {"%ete%"}
     */
    static Data<Map<String, Object>> randomOf(DataSource dataSource, String query, Object[] params) {
        return CollectionScrambler.randomOf(new QueryData(dataSource, query, params).queryRows())
    }

    /**
     * Creates a {@code Data} instance that will return a random row using provided query with parameters
     *
     * @param dataSource DB datasource instance
     * @param table table name
     * @param query DB selection string "SELECT fullName FROM person WHERE lastname like ?"
     * @param params selection parameters, example: Arrays.asList("%ete%")
     */
    static Data<Map<String, Object>> randomOf(DataSource dataSource, String query, List params) {
        return CollectionScrambler.randomOf(new QueryData(dataSource, query, params).queryRows())
    }

    /**
     * Creates a {@code Data} instance that will return a random row using provided query and closure
     *
     * @param dataSource DB datasource instance
     * @param query DB selection string "SELECT fullName FROM person WHERE lastname like ?"
     * @param where statement closure, example: { ResultSet rs ->
     *     while (rs.next()) println rs.getString('firstname') + ' ' + rs.getString(3)
     *}
     */
    static Data<Map<String, Object>> randomOf(DataSource dataSource, String query, Closure where) {
        return CollectionScrambler.randomOf(new QueryData(dataSource, query, where).queryRows())
    }

}
