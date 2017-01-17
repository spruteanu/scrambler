package org.prismus.scrambler.jdbc

import groovy.transform.CompileStatic
import org.prismus.scrambler.Value
import org.prismus.scrambler.value.ValueDefinition

import javax.sql.DataSource

/**
 * todo: add description
 *
 * @author Serge Pruteanu
 */
@CompileStatic
class DatabaseBatchBuilder {
    private boolean generateNullable = true
    private List<TableMeta> tables = new ArrayList<TableMeta>()
    private DataSourceDefinition definition

    protected DatabaseBatchBuilder(DataSource dataSource) {
        this(new DataSourceDefinition(dataSource))
    }

    protected DatabaseBatchBuilder(DataSourceDefinition dataSourceDefinition) {
        this.definition = dataSourceDefinition
    }

    DatabaseBatchBuilder generateNullable() {
        this.generateNullable = true
        return this
    }

    DatabaseBatchBuilder generateRequiredOnly() {
        this.generateNullable = false
        return this
    }

    DatabaseBatchBuilder forTable(String table) {
        final tableMap = definition.tableMap
        if (!tableMap.containsKey(table)) {
            throw new IllegalArgumentException("'$table' is not found in provided datasource")
        }
        tables.add(tableMap.get(table))
        return this
    }

    DatabaseBatchBuilder usingDefinition(DataSourceDefinition definition) {
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

    protected void sortTablesByFkDependency(List<TableMeta> tables) {
        Collections.sort(tables, new Comparator<TableMeta>() {
            @Override
            int compare(TableMeta left, TableMeta right) {
                return right.hasRelationship(left.name) ? 1 : 0
            }
        })
    }

    Value<Map<String, Map<String, Object>>> build() {
        if (!tables) {
            throw new IllegalStateException('No tables are defined for build')
        }
        sortTablesByFkDependency(tables)
        // todo Serge: implement me
        throw new RuntimeException('Implement me')
    }

}
