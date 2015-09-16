package org.prismus.scrambler.jdbc

import groovy.transform.CompileStatic
import groovy.transform.PackageScope

/**
 * todo: add description
 *
 * @author Serge Pruteanu
 */
@CompileStatic
@PackageScope
class ColumnMeta {
    String name
    int type

    private Class classType

    Map<String, Object> columnProperties = [:]

    boolean isNullable() {
        return columnProperties.get('NULLABLE') == 0
    }

    boolean isAutoIncrement() {
        return columnProperties.get('IS_AUTOINCREMENT')?.toString()?.equalsIgnoreCase('yes')
    }

    Class getClassType() {
        return classType
    }

}
