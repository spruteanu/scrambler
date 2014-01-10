package org.prismus.scrambler.builder

import groovy.transform.CompileStatic
import org.prismus.scrambler.Value

/**
 * todo: add description
 *
 * @author Serge Pruteanu
 */
@CompileStatic
class ParentValue implements Value {
    ValueDefinition parent
    ValuePredicate predicate

    @Override
    Object next() {
        return getValue()
    }

    public Object getValue() {
        return parent.instanceValue?.getParentValue(predicate)
    }

}
