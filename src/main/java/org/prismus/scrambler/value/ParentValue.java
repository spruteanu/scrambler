package org.prismus.scrambler.value;

import org.prismus.scrambler.Value;
import org.prismus.scrambler.builder.ValuePredicate;

/**
 * todo: add description
 *
 * @author Serge Pruteanu
 */
public class ParentValue implements Value {
    private InstanceValue instanceValue;
    private ValuePredicate predicate;

    public ParentValue() {
    }

    public ParentValue(InstanceValue definition) {
        this.instanceValue = definition;
    }

    public ParentValue(InstanceValue definition, ValuePredicate predicate) {
        this.instanceValue = definition;
        this.predicate = predicate;
    }

    @Override
    public Object next() {
        return get();
    }

    public Object get() {
        return instanceValue != null ? instanceValue.get() : null;
    }

    public void setInstanceValue(InstanceValue instanceValue) {
        this.instanceValue = instanceValue;
    }

    public ValuePredicate getPredicate() { // todo Serge: fix predicates
        return predicate;
    }

    public void setPredicate(ValuePredicate predicate) {
        this.predicate = predicate;
    }
}
