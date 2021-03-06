package org.prismus.scrambler.jdbc

import groovy.transform.CompileStatic

import java.sql.Connection
import java.sql.ResultSet
import java.sql.ResultSetMetaData
import java.sql.Statement

/**
 * DB utilities methods
 *
 * @author Serge Pruteanu
 */
@CompileStatic
final class Util {

    static void closeQuietly(Connection connection) {
        try {
            connection?.close()
        } catch (Exception ignore) { }
    }

    static void closeQuietly(ResultSet rs) {
        try {
            rs?.close()
        } catch (Exception ignore) { }
    }

    static void closeQuietly(Statement statement) {
        try {
            statement?.close()
        } catch (Exception ignore) { }
    }

    static Map<String, Object> asMap(ResultSet rs) {
        final props = new TreeMap<String, Object>(String.CASE_INSENSITIVE_ORDER)
        final ResultSetMetaData rsmd = rs.getMetaData()
        final int columnCount = rsmd.getColumnCount()
        for (int i = 0; i < columnCount; i++) {
            props.put(rsmd.getColumnName(i + 1), rs.getObject(i + 1))
        }
        return props
    }

}
