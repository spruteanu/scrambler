package org.prismus.scrambler.builder

import groovy.transform.CompileStatic
import org.prismus.scrambler.Value

/**
 * todo: add description
 *
 * @author Serge Pruteanu
 */
@CompileStatic
class ParentValue implements Value, DefinitionRegistrable {
    ValueDefinition definition

    @Override
    Object next() {
        return getValue()
    }

    @Override
    void registerDefinition(ValueDefinition definition) {
        this.definition = definition
    }

    public Object getValue() {
        return definition.parent.instanceValue?.value
    }

}
