package org.prismus.scrambler.value;

import org.prismus.scrambler.Value;
import org.prismus.scrambler.builder.DefinitionRegistrable;
import org.prismus.scrambler.builder.ValueDefinition;
import org.prismus.scrambler.builder.ValuePredicate;

/**
 * todo: add description
 *
 * @author Serge Pruteanu
 */
public class ParentValue implements Value, DefinitionRegistrable {
    private ValuePredicate predicate;
    private ValueDefinition definition;

    @Override
    public Object next() {
        return getValue();
    }

    @Override
    public void register(ValueDefinition definition) {
        this.definition = definition;
    }

    public Object getValue() {
        final InstanceValue instanceValue = definition.getParent().getInstanceValue();
        return instanceValue != null ? instanceValue.getValue() : null;
    }

    public void setDefinition(ValueDefinition definition) {
        this.definition = definition;
    }

    public ValuePredicate getPredicate() { // todo Serge: fix predicates
        return predicate;
    }

    public void setPredicate(ValuePredicate predicate) {
        this.predicate = predicate;
    }
}
