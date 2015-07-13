package org.prismus.scrambler.value;

import org.prismus.scrambler.Value;
import org.prismus.scrambler.builder.DefinitionRegistrable;
import org.prismus.scrambler.builder.ValueDefinition;

/**
 * todo: add description
 *
 * @author Serge Pruteanu
 */
public class ParentValue implements Value, DefinitionRegistrable {
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

    public ValueDefinition getDefinition() {
        return definition;
    }

    public void setDefinition(ValueDefinition definition) {
        this.definition = definition;
    }

}
