package org.prismus.scrambler.value;

import org.prismus.scrambler.Value;
import org.prismus.scrambler.builder.ValueDefinition;
import org.prismus.scrambler.builder.ValuePredicate;

/**
 * todo: add description
 *
 * @author Serge Pruteanu
 */
public class ReferenceValue extends Constant<Object> {
    private ValueDefinition definition;
    private ValuePredicate predicate;
    private ValuePredicate fieldPredicate;

    private Value referencedInstance;
    private Value referencedFieldValue;

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
        Object result = resolveValue();
        setValue(result);
        return result;
    }

    final Object resolveValue() {
        Object result = null;
        if (definition != null) {
            if (referencedInstance == null) {
                referencedInstance = definition.lookupValue(predicate);
            }
            if (referencedInstance != null) {
                result = referencedInstance.get();
                if (fieldPredicate != null) {
                    if (referencedInstance instanceof InstanceValue && referencedFieldValue == null) {
                        referencedFieldValue = ((InstanceValue) referencedInstance).lookupFieldValue(fieldPredicate);
                    }
                    if (referencedFieldValue != null) {
                        result = referencedFieldValue.get();
                    }
                }
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
