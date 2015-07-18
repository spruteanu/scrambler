package org.prismus.scrambler.value;

import org.prismus.scrambler.Value;
import org.prismus.scrambler.builder.ValueDefinition;
import org.prismus.scrambler.builder.ValuePredicate;

/**
 * todo: add description
 *
 * @author Serge Pruteanu
 */
public class ReferenceValue implements Value {
    private ValueDefinition definition;
    private ValuePredicate predicate;
    private ValuePredicate fieldPredicate;

    public ReferenceValue(ValuePredicate predicate, ValuePredicate fieldPredicate) {
        this.predicate = predicate;
        this.fieldPredicate = fieldPredicate;
    }

    public ReferenceValue(ValueDefinition definition, ValuePredicate predicate) {
        this.definition = definition;
        this.predicate = predicate;
    }

    @Override
    public Object next() {
        return get();
    }

    public Object get() {
        Object result = null;
        if (definition != null) {
            final Value value = definition.lookupValue(predicate);
            if (value != null) {
                result = value.get();
            }
        }
        return result;
    }

    public void setDefinition(ValueDefinition definition) {
        this.definition = definition;
    }

    public void setPredicate(ValuePredicate predicate) {
        this.predicate = predicate;
    }

    public void setFieldPredicate(ValuePredicate fieldPredicate) {
        this.fieldPredicate = fieldPredicate;
    }
}
