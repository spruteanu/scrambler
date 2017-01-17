package org.prismus.scrambler.jdbc

import groovy.transform.CompileStatic
import groovy.transform.PackageScope

/**
 * Class responsible for DB column properties description
 *
 * @author Serge Pruteanu
 */
@CompileStatic
@PackageScope
class ColumnMeta {
    String name
    int type

    String fkName

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

    boolean isFk() {
        return fkName != null
    }

    String getPrimaryTableName() {
        if (!isFk()) {
            return null
        }
        final Map<String, Object> fkPropertyMap = columnProperties.get(fkName) as Map<String, Object>
        return fkPropertyMap.get('PKTABLE_NAME')
    }

    String getPrimaryColumnName() {
        if (!isFk()) {
            return null
        }
        final Map<String, Object> fkPropertyMap = columnProperties.get(fkName) as Map<String, Object>
        return fkPropertyMap.get('PKCOLUMN_NAME')
    }

}
