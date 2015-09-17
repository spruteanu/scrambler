package org.prismus.scrambler.jdbc

import groovy.transform.CompileStatic
import org.prismus.scrambler.Value
import org.prismus.scrambler.value.ValueDefinition

/**
 * todo: add description
 *
 * @author Serge Pruteanu
 */
@CompileStatic
class DatabaseBatchBuilder {
    private final DatabaseScrambler databaseScrambler
    private boolean generateNullable = true
    private List<TableMeta> tables = new ArrayList<TableMeta>()
    private ValueDefinition definition

    protected DatabaseBatchBuilder(DatabaseScrambler databaseScrambler) {
        this.databaseScrambler = databaseScrambler
        this.definition = new ValueDefinition()
    }

    DatabaseBatchBuilder generateNullable() {
        this.generateNullable = true
        return this
    }

    DatabaseBatchBuilder generateRequired() {
        this.generateNullable = false
        return this
    }

    DatabaseBatchBuilder forTable(String table) {
        final tableMap = databaseScrambler.tableMap
        if (!tableMap.containsKey(table)) {
            throw new IllegalArgumentException("'$table' is not found in provided datasource")
        }
        tables.add(tableMap.get(table))
        return this
    }

    DatabaseBatchBuilder usingDefinition(ValueDefinition definition) {
        this.definition = definition
        return this
    }

    DatabaseBatchBuilder usingDefinition(String... definitions) {
        definition.usingDefinitions(definitions)
        return this
    }

    DatabaseBatchBuilder scanDefinition(String definition, String... definitions) {
        this.definition.scanDefinitions(definition, definitions)
        return this
    }

    DatabaseBatchBuilder scanLibraryDefinition(String definitionMatcher) {
        definition.usingLibraryDefinitions(definitionMatcher)
        return this
    }

    ValueDefinition getDefinition() {
        return definition
    }

    protected void sortTablesByFkDependency() {
        Collections.sort(tables, new Comparator<TableMeta>() {
            @Override
            int compare(TableMeta left, TableMeta right) {
                return right.hasFkDependency(left.name) ? 1 : 0
            }
        })
    }

    Value<Map<String, Object>> build() {
        // todo Serge: implement me
        throw new RuntimeException('Implement me')
    }

}
