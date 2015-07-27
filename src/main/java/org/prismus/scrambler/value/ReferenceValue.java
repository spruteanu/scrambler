package org.prismus.scrambler.value;

import org.prismus.scrambler.Value;
import org.prismus.scrambler.ValuePredicate;

import java.util.regex.Pattern;

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

    public ReferenceValue(ValuePredicate predicate) {
        this(predicate, null);
    }

    public ReferenceValue(Pattern fieldPattern) {
        this((ValuePredicate)null, PropertyPredicate.of(fieldPattern));
    }

    public ReferenceValue(ValuePredicate predicate, ValuePredicate fieldPredicate) {
        this.predicate = predicate;
        this.fieldPredicate = fieldPredicate;
    }

    public ReferenceValue(Pattern predicatePattern, Pattern fieldPattern) {
        this(PropertyPredicate.of(predicatePattern), PropertyPredicate.of(fieldPattern));
    }

    public ReferenceValue(ValueDefinition definition, Pattern pattern) {
        this(definition, PropertyPredicate.of(pattern));
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
            if (referencedInstance == null && predicate != null) {
                referencedInstance = definition.lookupValue(predicate);
            }
            if (referencedInstance != null) {
                result = referencedInstance.get();
            }
            if (fieldPredicate != null) {
                if (referencedInstance instanceof InstanceValue && referencedFieldValue == null) {
                    referencedFieldValue = ((InstanceValue) referencedInstance).lookupFieldValue(fieldPredicate);
                }
                if (referencedFieldValue == null && definition != null) {
                    referencedFieldValue = definition.lookupValue(fieldPredicate);
                }
                if (referencedFieldValue != null) {
                    result = referencedFieldValue.get();
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
