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
class InstanceValue implements Value<Object>, DefinitionRegistrable {
    ValueDefinition parent
    protected Closure definitionClosure
    protected ValuePredicate predicate

    protected Class type
    protected Collection<Value> constructorArguments
    protected Map<Object, Object> propertyValueMap

    protected ValueDefinition definition
    protected Instance instance

    InstanceValue() {
    }

    InstanceValue(Class type, Collection<Value> constructorArguments, ValueDefinition parent) {
        this.type = type
        this.constructorArguments = constructorArguments
        this.parent = parent
    }

    @Override
    void register(ValueDefinition definition) {
        parent = definition
    }

    @Override
    Object next() {
        if (definition == null) {
            build()
        }
        return instance.next()
    }

    Object getValue() {
        return instance.value
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
            if (definition.introspect == null) {
                definition.introspect = parent?.introspect
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
