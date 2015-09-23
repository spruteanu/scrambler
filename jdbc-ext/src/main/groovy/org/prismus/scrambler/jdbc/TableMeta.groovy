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
class TableMeta {
    String name
    List<String> idFields = []
    Map<String, ColumnMeta> columnMap = [:]
    Map<String, Map<String, Object>> fkMap = [:]
    Set<String> fkTables = []

    void setFkMap(Map<String, Map<String, Object>> fkMap) {
        this.fkMap = fkMap
        this.fkTables = new LinkedHashSet<String>(fkMap.size())
        for (Map<String, Object> fkProps : fkMap.values()) {
            fkTables.add(fkProps.get('FKTABLE_NAME').toString())
        }
    }

    boolean hasFkDependency(String table) {
        return fkTables.contains(table)
    }

}
