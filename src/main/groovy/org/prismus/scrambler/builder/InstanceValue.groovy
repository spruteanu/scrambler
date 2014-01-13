package org.prismus.scrambler.builder

import groovy.transform.CompileStatic
import org.prismus.scrambler.Value
import org.prismus.scrambler.value.Constant

/**
 * todo: add description
 *
 * @author Serge Pruteanu
 */
@CompileStatic
class InstanceValue implements Value<Object> {
    protected ValueDefinition parent
    protected Closure definitionClosure
    protected ValuePredicate predicate

    protected Class type
    protected Collection<Value> constructorArguments
    protected Map<Object, Object> propertyValueMap

    protected ValueDefinition definition
    protected Instance instance

    Object value

    InstanceValue() {
    }

    InstanceValue(Class type, Collection<Value> constructorArguments, ValueDefinition parent) {
        this.type = type
        this.constructorArguments = constructorArguments
        this.parent = parent
    }

    @Override
    Object next() {
        if (definition == null) {
            build()
        }
        value = instance.next()
        return value
    }

    Object getParentValue(ValuePredicate valuePredicate) {
        Object resultValue = value
        if (valuePredicate != null) {
            for (final entry : resultValue?.properties?.entrySet()) {
                // todo Serge: method is not performant. change it with cached setter method, thus only first time it will be slow
                if (checkApply(entry, predicate)) {
                    resultValue = entry.value
                    break
                }
            }
        }
        return resultValue
    }

    protected boolean checkApply(Map.Entry entry, ValuePredicate valuePredicate) {
        return valuePredicate.apply(entry.key.toString(), entry.value)
    }

    protected ValueDefinition build() {
        if (!definition) {
            definition = new ValueDefinition(parent: parent, instanceValue: this)
        }

        if (definitionClosure != null) {
            definitionClosure.rehydrate(definition, definition, definition).call(definition)
        }

        if (propertyValueMap) {
            definition.of((Map) propertyValueMap)
        }

        if (definitionClosure != null || propertyValueMap) {
            definition.build()
            if (parent?.shouldIntrospect()) {
                definition.introspect = Boolean.TRUE
            }
        } else {
            if (parent != null) {
                definition = parent
            }
        }

        instance = new Instance(type)
                .using(definition)
                .using(constructorArguments?.toList())
        return definition
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
