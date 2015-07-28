package org.prismus.scrambler.value;

import org.prismus.scrambler.Value;
import org.prismus.scrambler.ValuePredicate;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.regex.Pattern;

/**
 * todo: add description
 *
 * @author Serge Pruteanu
 */
@SuppressWarnings("unchecked")
public class ValueDefinition implements Cloneable {
    private ValueDefinition parent;

    private Map<ValuePredicate, Value> propertyValueMap = new LinkedHashMap<ValuePredicate, Value>();
    private Map<ValuePredicate, InstanceValue> instanceValueMap = new LinkedHashMap<ValuePredicate, InstanceValue>();
    private Map<String, Object> contextMap = new LinkedHashMap<String, Object>();

    public ValueDefinition() {
    }

    public ValueDefinition(Map<Object, Value> propertyValueMap) {
        of((Map) propertyValueMap);
    }

    public void setParent(ValueDefinition parent) {
        this.parent = parent;
    }

    public Map<ValuePredicate, Value> getPropertyValueMap() {
        return propertyValueMap;
    }

    //------------------------------------------------------------------------------------------------------------------
    // Definitions Builder Methods
    //------------------------------------------------------------------------------------------------------------------
    public ValueDefinition of(Value value) {
        Util.checkNullValue(value);
        final Object value1 = value.get();
        Util.checkNullValue(value1);
        registerPredicateValue(new TypePredicate(value1.getClass()), value);
        return this;
    }

    public ValueDefinition of(InstanceValue value) {
        Util.checkNullValue(value);
        registerPredicateValue(value.getPredicate(), value);
        return this;
    }

    public ValueDefinition of(Map<Object, Object> props) {
        Util.checkNullValue(props);
        for (final Map.Entry entry : props.entrySet()) {
            final Object key = entry.getKey();
            Util.checkNullValue(key);

            final Object value = entry.getValue();
            if (String.class.isInstance(key)) {
                if (value instanceof Value) {
                    of((String) key, (Value) value);
                } else {
                    of((String) key, value);
                }
            } else if (Pattern.class.isInstance(key)) {
                if (value instanceof Value) {
                    of(new PropertyPredicate((Pattern) key), (Value) value);
                } else {
                    of(new PropertyPredicate((Pattern) key), value);
                }
            } else if (key instanceof Class) {
                if (value instanceof Value) {
                    of((Class) key, (Value) value);
                } else {
                    of((Class) key, value);
                }
            } else if (ValuePredicate.class.isInstance(key)) {
                if (value instanceof Value) {
                    of((ValuePredicate) key, (Value) value);
                } else {
                    of((ValuePredicate) key, value);
                }
            } else {
                throw new IllegalArgumentException(String.format("Key should be of following types: [String, Class, ValuePredicate]; passed map: %s", props));
            }
        }
        return this;
    }

    public ValueDefinition constant(Object value) {
        Util.checkNullValue(value);
        registerPredicateValue(new TypePredicate(value.getClass()), new Constant(value));
        return this;
    }

    public ValueDefinition constant(Map<Object, Object> props) {
        Util.checkNullValue(props);
        for (final Map.Entry entry : props.entrySet()) {
            final Object key = entry.getKey();
            Util.checkNullValue(key);

            final Object value = entry.getValue();
            if (Value.class.isInstance(value)) {
                throw new IllegalArgumentException(String.format("Constant values can't be of Value type; passed map: %s", props));
            }
            if (String.class.isInstance(key)) {
                of((String) key, new Constant(value));
            } else if (key instanceof Class) {
                of((Class) key, new Constant(value));
            } else if (ValuePredicate.class.isInstance(key)) {
                of((ValuePredicate) key, new Constant(value));
            } else {
                throw new IllegalArgumentException(String.format("Key should be of following types: [String, Class, ValuePredicate]; passed map: %s", props));
            }
        }
        return this;
    }

    public ValueDefinition of(String propertyName, Object value) {
        registerPredicateValue(Util.createPropertyPredicate(propertyName), new Constant(value));
        return this;
    }

    public ValueDefinition of(Pattern pattern, Object value) {
        registerPredicateValue(PropertyPredicate.of(pattern), new Constant(value));
        return this;
    }

    public ValueDefinition of(Class type, Object value) {
        Util.checkNullValue(type);
        registerPredicateValue(new TypePredicate(type), new Constant(value));
        return this;
    }

    public ValueDefinition of(String propertyName, Value value) {
        Util.checkNullValue(value);
        if (value instanceof ReferenceValue) {
            ((ReferenceValue) value).setDefinition(this);
        }
        registerPredicateValue(Util.createPropertyPredicate(propertyName), value);
        return this;
    }

    public ValueDefinition of(Pattern pattern, Value value) {
        Util.checkNullValue(value);
        if (value instanceof ReferenceValue) {
            ((ReferenceValue) value).setDefinition(this);
        }
        registerPredicateValue(PropertyPredicate.of(pattern), value);
        return this;
    }

    public ValueDefinition of(Class type, Value value) {
        Util.checkNullValue(type);
        Util.checkNullValue(value);
        registerPredicateValue(new TypePredicate(type), value);
        return this;
    }

    public ValueDefinition of(ValuePredicate valuePredicate, Value value) {
        Util.checkNullValue(valuePredicate);
        Util.checkNullValue(value);
        registerPredicateValue(valuePredicate, value);
        return this;
    }

    public ValueDefinition of(ValuePredicate valuePredicate, Object value) {
        Util.checkNullValue(valuePredicate);
        registerPredicateValue(valuePredicate, new Constant(value));
        return this;
    }

    public ValueDefinition reference(Class type) {
        Util.checkNullValue(type);
        final ValuePredicate predicate = new TypePredicate(type);
        registerPredicateValue(predicate, new ReferenceValue(this, predicate));
        return this;
    }

    public ValueDefinition reference(Class type, String parentPredicate) {
        reference(type, Util.createPropertyPredicate(parentPredicate));
        return this;
    }

    public ValueDefinition reference(Class type, Class parentPredicate) {
        Util.checkNullValue(parentPredicate);
        reference(type, new TypePredicate(parentPredicate));
        return this;
    }

    public ValueDefinition reference(Class type, ValuePredicate parentPredicate) {
        Util.checkNullValue(type);
        final ValuePredicate predicate = new TypePredicate(type);
        registerPredicateValue(predicate, new ReferenceValue(this, parentPredicate));
        return this;
    }

    public ValueDefinition reference(String propertyName, Class parentPredicate) {
        Util.checkNullValue(parentPredicate);
        reference(Util.createPropertyPredicate(propertyName), new TypePredicate(parentPredicate));
        return this;
    }

    public ValueDefinition reference(Pattern pattern, Class parentPredicate) {
        Util.checkNullValue(parentPredicate);
        reference(PropertyPredicate.of(pattern), new TypePredicate(parentPredicate));
        return this;
    }

    public ValueDefinition reference(String propertyName) {
        final ValuePredicate predicate = Util.createPropertyPredicate(propertyName);
        registerPredicateValue(predicate, new ReferenceValue(this, predicate));
        return this;
    }

    public ValueDefinition reference(Pattern pattern) {
        final PropertyPredicate predicate = PropertyPredicate.of(pattern);
        registerPredicateValue(predicate, new ReferenceValue(this, predicate));
        return this;
    }

    public ValueDefinition reference(ValuePredicate valuePredicate, ValuePredicate parentPredicate) {
        Util.checkNullValue(valuePredicate);
        registerPredicateValue(valuePredicate, new ReferenceValue(this, parentPredicate));
        return this;
    }

    public ValueDefinition usingContext(Map<String, Object> contextMap) {
        if (contextMap != null) {
            this.contextMap.putAll(contextMap);
        }
        return this;
    }

    //------------------------------------------------------------------------------------------------------------------
    // Internal Methods
    //------------------------------------------------------------------------------------------------------------------
    ValueDefinition build() {
        for (final InstanceValue value : instanceValueMap.values()) {
            value.build(this);
        }
        return this;
    }

    void lookupRegisterInstanceValue(ValuePredicate valuePredicate, Value value) {
        if (InstanceValue.class.isInstance(value)) {
            final InstanceValue instanceValue = (InstanceValue) value;
            if (instanceValue.getDefinition() != this) {
                instanceValueMap.put(valuePredicate, instanceValue);
            }
        } else if (CollectionValue.class.isInstance(value)) {
            lookupRegisterInstanceValue(valuePredicate, ((CollectionValue) value).getInstance());
        } else if (value instanceof ArrayValue) {
            lookupRegisterInstanceValue(valuePredicate, ((ArrayValue) value).getInstance());
        }
    }

    public ValueDefinition registerPredicateValue(ValuePredicate valuePredicate, Value value) {
        lookupRegisterInstanceValue(valuePredicate, value);
        propertyValueMap.put(valuePredicate, value);
        return this;
    }

    public Value lookupValue(ValuePredicate predicate) {
        Value result = propertyValueMap.get(predicate);
        if (result == null && parent != null) {
            result = parent.lookupValue(predicate);
        }
        return result;
    }

    public Object getContextProperty(String property) {
        return contextMap.get(property);
    }

    @Override
    public Object clone() throws CloneNotSupportedException {
        return super.clone();
    }

}
