package org.prismus.scrambler.jdbc

import groovy.transform.CompileStatic
import groovy.transform.PackageScope
import org.prismus.scrambler.MapScrambler
import org.prismus.scrambler.Value
import org.prismus.scrambler.value.MapValue
import org.prismus.scrambler.value.ValueDefinition

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

    String insertStatement
    Set<String> sortedKeys
    MapValue<String> mapValue

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

    boolean isBuilt() {
        return !mapValue
    }

    Map<String, Object> getInsertMap() {
        return mapValue.next()
    }

    TableMeta build(DatabaseScrambler databaseValue) {
        final ValueDefinition definition = databaseValue.definition
        final generateNullable = databaseValue.generateNullable
        final List<String> keys = new ArrayList<String>(columnMap.size())
        final valueMap = new LinkedHashMap<String, Value>()
        for (Map.Entry<String, ColumnMeta> entry : columnMap.entrySet()) {
            final column = entry.value
            if (column.isAutoIncrement()) {
                continue
            }
            final columnName = entry.key
            Value value = definition.lookupValue(columnName, column.classType)
            if (!generateNullable && column.isNullable()) {
                continue
            }
            if (value != null) {
                keys.add(columnName)
                valueMap.put(columnName, value)
            } else {
                // todo Serge: resolve foreign key values
            }
        }

        Collections.sort(keys)
        sortedKeys = new LinkedHashSet<String>(keys)
        insertStatement = buildInsertStatement(name, sortedKeys)

        final map = new LinkedHashMap<String, Value>()
        for (String column : sortedKeys) {
            map.put(column, valueMap.get(column))
        }
        mapValue = MapScrambler.of(map)
        return this
    }

    @PackageScope
    static String buildInsertStatement(String table, Collection<String> sortedKeys) {
        return "INSERT INTO $table (${sortedKeys.join(',')}) VALUES (${':' + sortedKeys.join(', :')})"
    }
}
