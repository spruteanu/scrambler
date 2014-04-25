package org.prismus.scrambler.builder

import groovy.transform.CompileStatic
import org.prismus.scrambler.Value

/**
 * todo: add description
 *
 * @author Serge Pruteanu
 */
@CompileStatic
class ReferenceValue implements Value, DefinitionRegistrable {
    ValueDefinition definition
    ValuePredicate predicate

    @Override
    Object next() {
        return getValue()
    }

    public Object getValue() {
        return definition.instanceValue?.getParentValue(predicate)
    }

    @Override
    void register(ValueDefinition definition) {
        this.definition = definition
    }

}
