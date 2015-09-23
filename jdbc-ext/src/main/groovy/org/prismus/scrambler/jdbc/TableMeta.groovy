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

    List<String> ids
    Map<String, ColumnMeta> columnMap = [:]

    List<String> fkColumns = []

    Map<String, Map<String, Object>> relationshipMap
    Set<String> relationshipTables

    void setRelationshipMap(Map<String, Map<String, Object>> fkMap) {
        if (!fkMap) {
            return
        }
        this.relationshipMap = fkMap
        this.relationshipTables = new LinkedHashSet<String>(fkMap.size())
        for (Map<String, Object> fkProps : fkMap.values()) {
            relationshipTables.add(fkProps.get('FKTABLE_NAME').toString())
        }
    }

    boolean hasRelationship(String table) {
        return relationshipTables.contains(table)
    }

}
