package org.prismus.scrambler.jdbc

import groovy.transform.CompileStatic
import org.prismus.scrambler.Data
import org.prismus.scrambler.data.DataDefinition

import javax.sql.DataSource

/**
 * todo: add description
 *
 * @author Serge Pruteanu
 */
@CompileStatic
class DbDataBuilder {
    private boolean generateNullable = true
    private List<TableMeta> tables = new ArrayList<TableMeta>()
    private DbDataDefinition definition

    protected DbDataBuilder(DataSource dataSource) {
        this(new DbDataDefinition(dataSource))
    }

    protected DbDataBuilder(DbDataDefinition dataSourceDefinition) {
        this.definition = dataSourceDefinition
    }

    DbDataBuilder generateNullable() {
        this.generateNullable = true
        return this
    }

    DbDataBuilder generateRequiredOnly() {
        this.generateNullable = false
        return this
    }

    DbDataBuilder forTable(String table) {
        final tableMap = definition.tableMap
        if (!tableMap.containsKey(table)) {
            throw new IllegalArgumentException("'$table' is not found in provided datasource")
        }
        tables.add(tableMap.get(table))
        return this
    }

    DbDataBuilder usingDefinition(DbDataDefinition definition) {
        this.definition = definition
        return this
    }

    DbDataBuilder usingDefinition(String... definitions) {
        definition.usingDefinitions(definitions)
        return this
    }

    DbDataBuilder scanDefinition(String definition, String... definitions) {
        this.definition.scanDefinitions(definition, definitions)
        return this
    }

    DbDataBuilder scanLibraryDefinition(String definitionMatcher) {
        definition.usingLibraryDefinitions(definitionMatcher)
        return this
    }

    DataDefinition getDefinition() {
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

    Data<Map<String, Map<String, Object>>> build() {
        if (!tables) {
            throw new IllegalStateException('No tables are defined for build')
        }
        sortTablesByFkDependency(tables)
        // todo Serge: add multiple relationship types handling: 1x1, 1xMany, Manyx1, ManyXMany
        // todo Serge: implement me
        throw new RuntimeException('Implement me')
    }

}
