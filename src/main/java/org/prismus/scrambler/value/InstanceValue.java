package org.prismus.scrambler.value;

import groovy.lang.Closure;
import org.prismus.scrambler.Value;
import org.prismus.scrambler.builder.DefinitionRegistrable;
import org.prismus.scrambler.builder.Instance;
import org.prismus.scrambler.builder.ValueDefinition;
import org.prismus.scrambler.builder.ValuePredicate;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;

/**
 * todo: add description
 *
 * @author Serge Pruteanu
 */
public class InstanceValue implements Value<Object>, DefinitionRegistrable {
    private ValueDefinition parentDefinition;
    private Closure definitionClosure;
    private ValuePredicate predicate;

    private Class type;
    private Collection<Value> constructorArguments;
    private Map<Object, Object> propertyValueMap;

    private ValueDefinition definition;
    private Instance instance;

    public InstanceValue() {
    }

    public InstanceValue(Class type, Collection<Value> constructorArguments, ValueDefinition parentDefinition) {
        this.type = type;
        this.constructorArguments = constructorArguments;
        this.parentDefinition = parentDefinition;
    }

    @Override
    public void register(ValueDefinition parentDefinition) {
        this.parentDefinition = parentDefinition;
    }

    @Override
    public Object next() {
        if (definition == null) {
            build();
        }
        return instance.next();
    }

    public Object getValue() {
        return instance.getValue();
    }

    @SuppressWarnings("unchecked")
    public ValueDefinition build() {
        if (definition == null) {
            definition = new ValueDefinition();
            definition.setParent(parentDefinition);
            definition.setInstanceValue(this);
        }

        if (definitionClosure != null) {
            definitionClosure.rehydrate(definition, definition, definition).call(definition);
        }

        if (propertyValueMap != null) {
            definition.of((Map) propertyValueMap);
        }

        if (definitionClosure != null || (propertyValueMap != null && propertyValueMap.size() > 0)) {
            definition.build();
            if (definition.getIntrospect() == null && parentDefinition != null) {
                definition.setIntrospect(parentDefinition.getIntrospect());
            }
        } else {
            if (parentDefinition != null) {
                definition = parentDefinition;
            }
        }

        instance = new Instance(type).using(definition);
        if (constructorArguments != null) {
            instance.using(new ArrayList<Value>(constructorArguments));
        }
        return definition;
    }

    @SuppressWarnings("unchecked")
    public void setConstructorArguments(Collection constructorArguments) {
        this.constructorArguments = new ArrayList<Value>();
        if (constructorArguments != null) {
            for (final Object value : constructorArguments) {
                if (Value.class.isInstance(value)) {
                    this.constructorArguments.add((Value) value);
                } else {
                    this.constructorArguments.add(new Constant(value));
                }
            }
        }
    }

    public ValueDefinition getParentDefinition() {
        return parentDefinition;
    }

    public void setParentDefinition(ValueDefinition parentDefinition) {
        this.parentDefinition = parentDefinition;
    }

    public Closure getDefinitionClosure() {
        return definitionClosure;
    }

    public void setDefinitionClosure(Closure definitionClosure) {
        this.definitionClosure = definitionClosure;
    }

    public ValuePredicate getPredicate() {
        return predicate;
    }

    public void setPredicate(ValuePredicate predicate) {
        this.predicate = predicate;
    }

    public Class getType() {
        return type;
    }

    public void setType(Class type) {
        this.type = type;
    }

    public Collection<Value> getConstructorArguments() {
        return constructorArguments;
    }

    public Map<Object, Object> getPropertyValueMap() {
        return propertyValueMap;
    }

    public void setPropertyValueMap(Map<Object, Object> propertyValueMap) {
        this.propertyValueMap = propertyValueMap;
    }

    public ValueDefinition getDefinition() {
        return definition;
    }

    public void setDefinition(ValueDefinition definition) {
        this.definition = definition;
    }

    public Instance getInstance() {
        return instance;
    }

    public void setInstance(Instance instance) {
        this.instance = instance;
    }
}
