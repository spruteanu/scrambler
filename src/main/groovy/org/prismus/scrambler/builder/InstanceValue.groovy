package org.prismus.scrambler.builder

import groovy.transform.CompileStatic
import org.prismus.scrambler.Value
import org.prismus.scrambler.property.Constant

/**
 * todo: add description
 *
 * @author Serge Pruteanu
 */
@CompileStatic
class InstanceValue implements Value<Object> {
    ValueDefinition parent
    Closure definitionClosure
    ValuePredicate predicate

    Class instanceType
    Collection<Value> constructorArguments

    protected ValueDefinition definition

    Object value

    @Override
    Object next() {
        throw new UnsupportedOperationException('Not implemented yet')
    }

    Object getParentValue(ValuePredicate valuePredicate) {
        Object resultValue = value
        if (valuePredicate != null) {
            for (final entry : resultValue?.properties?.entrySet()) { // todo Serge: method is not performant. change it with cached setter method, thus only first time it will be slow
                if (checkApply(entry, predicate)) {
                    resultValue = entry.value
                    break
                }
            }
        }
        return resultValue
    }

    protected boolean checkApply(Map.Entry entry, ValuePredicate valuePredicate) {
        return valuePredicate.apply(entry.value?.class)
    }

    protected boolean checkApply(Map.Entry entry, PropertyPredicate valuePredicate) {
        return valuePredicate.apply(entry.key.toString())
    }

    void process() {
        definition = new ValueDefinition(parent: parent, instanceValue: this)
        definitionClosure.rehydrate(definition, definition, definition).call(definition)
        definition.process()
    }

    void setConstructorArguments(Collection constructorArguments) {
        this.constructorArguments = new ArrayList<Value>()
        if (constructorArguments != null) {
            for (final value : constructorArguments) {
                if (Value.isInstance(value)) {
                    this.constructorArguments.add(value as Value)
                } else {
                    this.constructorArguments.add(new Constant(value))
                }
            }
        }
    }

}
