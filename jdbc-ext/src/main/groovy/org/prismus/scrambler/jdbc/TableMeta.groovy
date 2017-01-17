package org.prismus.scrambler.jdbc

import groovy.transform.CompileStatic
import groovy.transform.PackageScope

/**
 * Class responsible for table properties description
 *
 * @author Serge Pruteanu
 */
@CompileStatic
@PackageScope
class TableMeta {
    String name

    List<String> ids
    Map<String, ColumnMeta> columnMap = new TreeMap<String, ColumnMeta>(String.CASE_INSENSITIVE_ORDER)

    List<String> fkColumns = []

    Map<String, Map<String, Object>> relationshipMap
    Set<String> relationshipTables = new TreeSet<String>(String.CASE_INSENSITIVE_ORDER)

    void setRelationshipMap(Map<String, Map<String, Object>> fkMap) {
        if (!fkMap) {
            return
        }
        this.relationshipMap = fkMap
        this.relationshipTables = new TreeSet<String>(String.CASE_INSENSITIVE_ORDER)
        for (Map<String, Object> fkProps : fkMap.values()) {
            relationshipTables.add(fkProps.get('FKTABLE_NAME').toString())
        }
    }

    boolean hasRelationship(String table) {
        return relationshipTables.contains(table)
    }

}
