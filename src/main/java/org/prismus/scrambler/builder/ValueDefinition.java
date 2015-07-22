package org.prismus.scrambler.builder;

import org.prismus.scrambler.Value;
import org.prismus.scrambler.value.*;

import java.util.Collection;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.Callable;

/**
 * todo: add description
 *
 * @author Serge Pruteanu
 */
@SuppressWarnings("unchecked")
public class ValueDefinition {
    private ValueDefinition parent;

    private Map<ValuePredicate, Value> propertyValueMap = new LinkedHashMap<ValuePredicate, Value>();
    private Map<ValuePredicate, InstanceValue> instanceValueMap = new LinkedHashMap<ValuePredicate, InstanceValue>();

    public ValueDefinition() {
    }

    public ValueDefinition(Map<Object, Value> propertyValueMap) {
        of(propertyValueMap);
    }

    public void setParent(ValueDefinition parent) {
        this.parent = parent;
    }

    public Map<ValuePredicate, Value> getPropertyValueMap() {
        return propertyValueMap;
    }

    public ValueDefinition build() {
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
        Value result = null;
        for (final Map.Entry<ValuePredicate, Value> entry : propertyValueMap.entrySet()) {
            if (predicate.isSame(entry.getKey())) {
                result = entry.getValue();
                break;
            }
        }
        if (result == null && parent != null) {
            result = parent.lookupValue(predicate);
        }
        return result;
    }

    //------------------------------------------------------------------------------------------------------------------
    // Object Methods
    //------------------------------------------------------------------------------------------------------------------
    public ValueDefinition of(Object value) {
        Util.checkNullValue(value);
        registerPredicateValue(new TypePredicate(value.getClass()), new Constant(value));
        return this;
    }

    public ValueDefinition constant(Object value) {
        Util.checkNullValue(value);
        registerPredicateValue(new TypePredicate(value.getClass()), new Constant(value));
        return this;
    }

    //------------------------------------------------------------------------------------------------------------------
    // Number Methods
    //------------------------------------------------------------------------------------------------------------------
    public ValueDefinition incremental(Number value) {
        return incremental(value, null);
    }

    public ValueDefinition incremental(Number value, Number step) {
        Util.checkNullValue(value);
        registerPredicateValue(new TypePredicate(value.getClass()), NumberValue.increment(value, step));
        return this;
    }

    public ValueDefinition incremental(Number value, Number step, Integer count) {
        Util.checkNullValue(value);
        registerPredicateValue(new TypePredicate(value.getClass()), NumberValue.incrementArray(value, step, count));
        return this;
    }

    public ValueDefinition random() {
        return random((Number) null, null);
    }

    public ValueDefinition random(Number minimum) {
        return random(minimum, null);
    }

    public ValueDefinition random(Number minimum, Number maximum) {
        Util.checkNullValue(minimum, maximum);
        final Number value = Util.getNotNullValue(minimum, maximum);
        registerPredicateValue(new TypePredicate(value.getClass()), NumberValue.random(minimum, maximum));
        return this;
    }

    //------------------------------------------------------------------------------------------------------------------
    // Date Methods
    //------------------------------------------------------------------------------------------------------------------
    public ValueDefinition incremental(Date value) {
        return incremental(value, null, null);
    }

    public ValueDefinition incremental(Date value, Integer step) {
        return incremental(value, step, null);
    }

    public ValueDefinition incremental(Date value, Integer step, Integer calendarField) {
        Util.checkNullValue(value);
        registerPredicateValue(new TypePredicate(value.getClass()), DateValue.increment(value, step, calendarField));
        return this;
    }

    public ValueDefinition random(Date value) {
        return random(value, null, null);
    }

    public ValueDefinition random(Date value, Date minimum) {
        return random(value, minimum, null);
    }

    public ValueDefinition random(Date value, Date minimum, Date maximum) {
        Util.checkNullValue(value);
        registerPredicateValue(new TypePredicate(Date.class), DateValue.random(value, minimum, maximum));
        return this;
    }

    //------------------------------------------------------------------------------------------------------------------
    // String Methods
    //------------------------------------------------------------------------------------------------------------------
    public ValueDefinition incremental(String value, Integer index) {
        Util.checkNullValue(value);
        registerPredicateValue(new TypePredicate(value.getClass()), StringValue.increment(value, index));
        return this;
    }

    public ValueDefinition incremental(String value) {
        return incremental(value, null, null);
    }

    public ValueDefinition incremental(String value, String pattern) {
        return incremental(value, pattern, null);
    }

    public ValueDefinition incremental(String value, String pattern, Integer index) {
        Util.checkNullValue(value);
        registerPredicateValue(new TypePredicate(String.class), StringValue.increment(value, pattern, index));
        return this;
    }

    public ValueDefinition random(String value) {
        return random(value, null);
    }

    public ValueDefinition random(String value, Integer count) {
        Util.checkNullValue(value);
        registerPredicateValue(new TypePredicate(String.class), StringValue.random(value, count));
        return this;
    }

    public ValueDefinition of(String propertyName, Object value) {
        registerPredicateValue(Util.createPropertyPredicate(propertyName), new Constant(value));
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

    public ValueDefinition reference(String propertyName, Class parentPredicate) {
        Util.checkNullValue(parentPredicate);
        reference(Util.createPropertyPredicate(propertyName), new TypePredicate(parentPredicate));
        return this;
    }

    public ValueDefinition reference(String propertyName) {
        final ValuePredicate predicate = Util.createPropertyPredicate(propertyName);
        registerPredicateValue(predicate, new ReferenceValue(this, predicate));
        return this;
    }

    //------------------------------------------------------------------------------------------------------------------
    // Collection Methods
    //------------------------------------------------------------------------------------------------------------------
    public ValueDefinition randomOf(Collection values) {
        Util.checkNullValue(values);
        Util.checkEmptyCollection(values);

        final Object value = values.iterator().next();
        registerPredicateValue(new TypePredicate(value.getClass()), CollectionValue.randomOf(values));
        return this;
    }

    public ValueDefinition random(Collection collection, Value value) {
        return random(collection, value, 0);
    }

    public ValueDefinition random(Collection collection, Value value, Integer count) {
        Util.checkNullValue(value);
        Util.checkNullValue(collection);
        registerPredicateValue(new TypePredicate(Collection.class), new CollectionValue(collection, value, count));
        return this;
    }

    //------------------------------------------------------------------------------------------------------------------
    // Value Methods
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

    public ValueDefinition reference(ValuePredicate valuePredicate, ValuePredicate parentPredicate) {
        Util.checkNullValue(valuePredicate);
        registerPredicateValue(valuePredicate, new ReferenceValue(this, parentPredicate));
        return this;
    }

    //------------------------------------------------------------------------------------------------------------------
    // Class Methods
    //------------------------------------------------------------------------------------------------------------------
    public ValueDefinition of(Class type, Object value) {
        Util.checkNullValue(type);
        registerPredicateValue(new TypePredicate(type), new Constant(value));
        return this;
    }

    public ValueDefinition of(Class type, Value value) {
        Util.checkNullValue(type);
        Util.checkNullValue(value);
        registerPredicateValue(new TypePredicate(type), value);
        return this;
    }

    public ValueDefinition of(Class type) {
        return of(type, null, null);
    }

    public ValueDefinition of(Class type, Collection constructorArgs) {
        return of(type, constructorArgs, null);
    }

    public ValueDefinition of(Class type, Collection constructorArgs, Callable<ValueDefinition> defCl) {
        Util.checkNullValue(type);
        final InstanceValue instanceValue = new InstanceValue(type);
        instanceValue.setConstructorArguments(constructorArgs);
        instanceValue.setDefinitionClosure(defCl);
        registerPredicateValue(new TypePredicate(type), instanceValue);
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

    //------------------------------------------------------------------------------------------------------------------
    // Map Methods
    //------------------------------------------------------------------------------------------------------------------
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

}
