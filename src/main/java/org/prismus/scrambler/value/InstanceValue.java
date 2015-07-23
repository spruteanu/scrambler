package org.prismus.scrambler.value;

import org.apache.commons.beanutils.PropertyUtilsBean;
import org.prismus.scrambler.DataScrambler;
import org.prismus.scrambler.Value;
import org.prismus.scrambler.ValuePredicate;

import java.beans.PropertyDescriptor;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.*;
import java.util.concurrent.Callable;

/**
 * todo: add description
 *
 * @author Serge Pruteanu
 */
public class InstanceValue<T> extends Constant<T> implements Value<T> {

    private ValueDefinition definition;
    private Callable<ValueDefinition> definitionClosure;
    private ValuePredicate predicate;

    private Object type;
    private Collection<Value> constructorArguments;
    private final Map<String, Value> fieldValueMap;

    private Map<String, Field> fieldMap;
    private PropertyUtilsBean propertyUtils;
    private List<Value> constructorValues;

    public InstanceValue() {
        this(null, null, null);
    }

    public InstanceValue(String type) {
        this(lookupType(type, false), null, null);
    }

    public InstanceValue(Class type) {
        this(type, null, null);
    }

    public InstanceValue(Class type, Collection<Value> constructorArguments, ValueDefinition parentDefinition) {
        this.type = type;
        this.constructorArguments = constructorArguments;
        this.constructorValues = new ArrayList<Value>();
        fieldValueMap = new LinkedHashMap<String, Value>();
        propertyUtils = Util.createBeanUtilsBean().getPropertyUtils();
    }

    public ValueDefinition getDefinition() {
        return definition;
    }

    public void setPredicate(ValuePredicate predicate) {
        this.predicate = predicate;
    }

    public ValuePredicate getPredicate() {
        return predicate;
    }

    public void setType(Class type) {
        this.type = type;
    }

    public void setConstructorValues(List<Value> constructorValues) {
        this.constructorValues = constructorValues;
    }

    @Override
    public T next() {
        if (definition == null) {
            build(null);
            final Object valueType = lookupType();
            if (valueType instanceof Class) {
                definition.registerPredicateValue(new TypePredicate((Class) valueType), this);
            }
        }
        final T instance = checkCreateInstance();
        setValue(instance);
        populate(instance);
        return instance;
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

    public InstanceValue<T> withConstructorArguments(Collection constructorArguments) {
        this.constructorArguments = new ArrayList<Value>();
        setConstructorArguments(constructorArguments);
        return this;
    }

    public InstanceValue<T> withPredicate(ValuePredicate predicate) {
        this.predicate = predicate;
        return this;
    }

    public InstanceValue<T> withDefinitionClosure(AbstractDefinitionCallable definitionClosure) {
        this.definitionClosure = definitionClosure;
        return this;
    }

    public InstanceValue<T> registerFieldValue(String field, Value value) {
        fieldValueMap.put(field, value);
        return this;
    }

    public Value lookupFieldValue(ValuePredicate fieldPredicate) {
        Value result = null;
        for (final Map.Entry<String, Value> entry : fieldValueMap.entrySet()) {
            final String fieldName = entry.getKey();
            final Value value = entry.getValue();
            if (fieldPredicate.apply(fieldName, value)) {
                result = value;
                break;
            }
        }
        return result;
    }

    public InstanceValue<T> usingDefinitions(ValueDefinition valueDefinition) {
        registerFieldValues(valueDefinition);
        definition = valueDefinition;
        return this;
    }

    public InstanceValue<T> usingDefinitions(Map<Object, Object> valueDefinitions) {
        if (valueDefinitions != null && valueDefinitions.size() > 0) {
            registerFieldValues(new ValueDefinition().of(valueDefinitions));
        }
        return this;
    }

    public InstanceValue<T> using(List<Value> constructorValues) {
        this.constructorValues = constructorValues;
        return this;
    }

    public InstanceValue<T> addConstructorValue(Value value) {
        constructorValues.add(value);
        return this;
    }

    public InstanceValue<T> usingValue(T value) {
        this.value = value;
        return this;
    }

    public InstanceValue<T> usingType(Class valueType) {
        this.type = valueType;
        return this;
    }

    public InstanceValue<T> usingType(String valueType) {
        this.type = valueType;
        return this;
    }

    //------------------------------------------------------------------------------------------------------------------
    // Internal Methods
    //------------------------------------------------------------------------------------------------------------------
    void setDefinitionClosure(Callable<ValueDefinition> definitionClosure) {
        this.definitionClosure = definitionClosure;
    }

    void registerFieldValues(ValueDefinition valueDefinition) {
        checkFieldMapCreated();

        final Map<String, Field> unresolvedProps = new HashMap<String, Field>(fieldMap);
        for (final Map.Entry<ValuePredicate, Value> entry : valueDefinition.getPropertyValueMap().entrySet()) {
            final ValuePredicate predicate = entry.getKey();
            for (final Field field : fieldMap.values()) {
                final String propertyName = field.getName();
                if (!fieldValueMap.containsKey(propertyName)) {
                    if (predicate.apply(propertyName, field.getValueType())) {
                        registerFieldValue(propertyName, entry.getValue());
                        break;
                    }
                }
            }
        }
        unresolvedProps.keySet().removeAll(fieldValueMap.keySet());
        fieldValueMap.putAll(introspectTypes(valueDefinition, unresolvedProps));
    }

    void checkDefinitionCreated() {
        if (definition == null) {
            definition = new ValueDefinition();
            registerFieldValues(definition);
        }
    }

    @SuppressWarnings("unchecked")
    InstanceValue<T> build(ValueDefinition parent) {
        checkDefinitionCreated();
        checkFieldMapCreated();
        definition.setParent(parent);
        definition.of((Map) fieldValueMap);
        if (definitionClosure != null) {
            executeDefinitionClosure();
        }
        if (definitionClosure != null || fieldValueMap.size() > 0) {
            definition.build();
        }
        return this;
    }

    void executeDefinitionClosure() {
        try {
            if (definitionClosure instanceof AbstractDefinitionCallable) {
                final AbstractDefinitionCallable callable = (AbstractDefinitionCallable) this.definitionClosure;
                if (callable.getDefinition() == null) {
                    callable.setDefinition(definition);
                }
            }
            definitionClosure.call();
        } catch (Exception e) {
            throw new RuntimeException("Failed to execute definition closure", e);
        }
    }

    Set<Class> getSupportedTypes() {
        final Set<Class> knownTypes = new HashSet<Class>();
        knownTypes.addAll(Types.getSupportedIncrementTypes());
        knownTypes.addAll(Types.getSupportedRandomTypes());
        return knownTypes;
    }

    @SuppressWarnings("unchecked")
    Map<String, Value> introspectTypes(ValueDefinition valueDefinition, Map<String, Field> unresolvedProps) {
        final Set<Class> supportedTypes = getSupportedTypes();
        final Map<String, Value> propertyValueMap = new LinkedHashMap<String, Value>();
        for (final Map.Entry<String, Field> entry : unresolvedProps.entrySet()) {
            final String propertyName = entry.getKey();
            final Field field = entry.getValue();
            final Class propertyType = field.getType();
            Value val = null;
            if (supportedTypes.contains(propertyType)) {
                val = DataScrambler.random(propertyType);
            } else if (propertyType.isArray() && supportedTypes.contains(propertyType.getComponentType())) {
                val = DataScrambler.random(propertyType, null);
            } else {
                if (Iterable.class.isAssignableFrom(propertyType) || Map.class.isAssignableFrom(propertyType)) {
                    continue;
                }
                final List<Value> ctorArgs = lookupConstructorArguments(valueDefinition, propertyType, supportedTypes);
                if (ctorArgs != null) {
                    val = new InstanceValue(propertyType, ctorArgs, valueDefinition);
                }
            }
            if (val != null) {
                propertyValueMap.put(propertyName, val);
            }
        }
        return propertyValueMap;
    }

    List<Value> lookupConstructorArguments(ValueDefinition valueDefinition, Class type, Set<Class> supportedTypes) {
        List<Value> result = null;
        try {
            final Map<ValuePredicate, Value> typeValueMap = valueDefinition.getPropertyValueMap();
            for (final Constructor ctor : type.getConstructors()) {
                final Class[] types = ctor.getParameterTypes();
                if (types == null) {
                    return new ArrayList<Value>();
                }
                final List<Value> values = new ArrayList<Value>();
                for (final Class argType : types) {
                    Value val = typeValueMap.get(new TypePredicate(argType));
                    if (val == null && supportedTypes.contains(argType)) {
                        val = DataScrambler.random(argType);
                    }
                    if (val != null) {
                        values.add(val);
                    }
                }
                if (values.size() == types.length) {
                    result = values;
                    break;
                }
            }
        } catch (Exception ignore) {
        }
        return result;
    }

    void populate(Object instance) {
        final Map<String, Object> propertyObjectMap = new LinkedHashMap<String, Object>(fieldValueMap.size());
        for (Map.Entry<String, Value> entry : fieldValueMap.entrySet()) {
            propertyObjectMap.put(entry.getKey(), entry.getValue().next());
        }
        populate(instance, propertyObjectMap);
    }

    void populate(Object instance, Map<String, Object> properties) {
        for (final Map.Entry<String, Object> entry : properties.entrySet()) {
            final String key = entry.getKey();
            final Object value = entry.getValue();
            try {
                setPropertyValue(instance, key, value);
            } catch (Exception e) {
                throw new RuntimeException(String.format("Failed to set instance: %s with property: %s; value: %s", instance, key, value), e);
            }
        }
    }

    Map<String, Field> checkFieldMapCreated() {
        if (fieldMap != null) {
            return fieldMap;
        }
        fieldMap = lookupFields(value != null ? value : type);
        return fieldMap;
    }

    Map<String, Field> lookupFields(Object instanceType) {
        final boolean isInstance = !(instanceType instanceof Class);
        final Map<String, Field> propertyDefinitionMap = new LinkedHashMap<String, Field>();

        final PropertyDescriptor[] propertyDescriptors;
        if (isInstance) {
            propertyDescriptors = propertyUtils.getPropertyDescriptors(instanceType);
        } else {
            propertyDescriptors = propertyUtils.getPropertyDescriptors(((Class) instanceType));
        }
        if (propertyDescriptors != null) {
            for (final PropertyDescriptor propertyDescriptor : propertyDescriptors) {
                final String name = propertyDescriptor.getName();
                final Object value = isInstance ? getPropertyValue(propertyDescriptor, instanceType) : null;
                propertyDefinitionMap.put(name, new Field(propertyDescriptor, value));
            }
            propertyDefinitionMap.remove("class");
            propertyDefinitionMap.remove("metaClass");
        }
        return propertyDefinitionMap;
    }

    void setPropertyValue(PropertyDescriptor propertyDescriptor, Object instance, Object value) {
        try {
            propertyDescriptor.getWriteMethod().invoke(instance, value);
        } catch (Exception ignore) {
        }
    }

    void setPropertyValue(Object instance, String propertyName, Object value) {
        final Field field = fieldMap.get(propertyName);
        if (field != null) {
            setPropertyValue(field.propertyDescriptor, instance, value);
        } else {
            try {
                propertyUtils.setSimpleProperty(instance, propertyName, value);
            } catch (Exception ignore) {
            }
        }
    }

    Object getPropertyValue(PropertyDescriptor propertyDescriptor, Object instance) {
        Object value = null;
        try {
            value = propertyDescriptor.getReadMethod().invoke(instance);
        } catch (Exception ignore) {
        }
        return value;
    }

    Object getPropertyValue(Object instance, String propertyName) {
        final Field field = fieldMap.get(propertyName);
        Object value = null;
        if (field != null) {
            value = getPropertyValue(field.propertyDescriptor, instance);
        } else {
            try {
                value = propertyUtils.getProperty(instance, propertyName);
            } catch (Exception ignore) {
            }
        }
        return value;
    }

    @SuppressWarnings({"unchecked"})
    T checkCreateInstance() {
        T result = this.value;
        Object valueType = lookupType();
        if (valueType instanceof Class) {
            Object[] arguments = null;
            Class[] types = null;
            if (constructorValues != null && constructorValues.size() > 0) {
                arguments = new Object[constructorValues.size()];
                types = new Class[constructorValues.size()];
                for (int i = 0; i < constructorValues.size(); i++) {
                    final Value value = constructorValues.get(i);
                    final Object valueObject = value.next();
                    arguments[i] = valueObject;
                    types[i] = valueObject.getClass();
                }
            }
            result = (T) Util.createInstance((Class) valueType, arguments, types);
        }
        return result;
    }

    Object lookupType() {
        Object valueType = type;
        if (valueType == null) {
            valueType = value;
        }
        if (valueType instanceof String) {
            valueType = lookupType((String) valueType, true);
        }
        return valueType;
    }

    static Class lookupType(String classType, boolean throwError) {
        Class result = null;
        try {
            result = Class.forName(((String) classType));
        } catch (ClassNotFoundException e) {
            if (throwError) {
                throw new IllegalStateException(String.format("Not found class for specified value: %s", classType), e);
            }
        }
        return result;
    }

    static class Field {
        final PropertyDescriptor propertyDescriptor;
        final Object value;

        public Field(PropertyDescriptor propertyDescriptor, Object value) {
            this.propertyDescriptor = propertyDescriptor;
            this.value = value;
        }

        public Object getValueType() {
            return value != null ? value : getType();
        }

        public String getName() {
            return propertyDescriptor.getName();
        }

        public Class getType() {
            return propertyDescriptor.getPropertyType();
        }

        public Method getter() {
            return propertyDescriptor.getReadMethod();
        }

        public Method setter() {
            return propertyDescriptor.getWriteMethod();
        }
    }

}
