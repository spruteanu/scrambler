package org.prismus.scrambler.builder

import groovy.transform.CompileStatic

/**
 * todo: add description
 *
 * @author Serge Pruteanu
 */
@CompileStatic
class ParentValue extends ReferenceValue {

    @Override
    ValueDefinition getDefinition() {
        return super.definition.parent
    }

    public Object getValue() {
        return this.getDefinition().instanceValue?.getParentValue(predicate)
    }

}
