package org.prismus.scrambler.builder;

import org.apache.commons.beanutils.BeanUtilsBean;
import org.apache.commons.beanutils.PropertyUtilsBean;
import org.prismus.scrambler.Value;
import org.prismus.scrambler.value.*;
import org.prismus.scrambler.value.Random;

import java.beans.PropertyDescriptor;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.*;

/**
 * todo: add description
 *
 * @author Serge Pruteanu
 */
public class Instance<T> extends Constant<T> {
    private static final String FAILED_SET_PROPERTIES_MSG = "Failed to set instance: %s with properties: %s";

    private Object type;

    private Map<String, Property> propertyMap;
    private BeanUtilsBean beanUtilsBean;
    private Map<String, Value> propertyValueMap;
    protected List<Value> constructorValues;

    public Instance() {
        this((T) null);
    }

    public Instance(String type) {
        super(null);
        this.type = lookupType(type, false);
        if (this.type == null) {
            this.type = type;
        }
        initialize();
    }

    public Instance(Class type) {
        super(null);
        this.type = type;
        initialize();
    }

    public Instance(T instance) {
        super(instance);
        initialize();
    }

    protected void initialize() {
        constructorValues = new ArrayList<Value>();
        propertyValueMap = new LinkedHashMap<String, Value>();
        beanUtilsBean = Util.createBeanUtilsBean();
        propertyMap = lookupPropertyDefinitions(value != null ? value : type);
    }

    public Instance<T> usingType(Class valueType) {
        this.type = valueType;
        return this;
    }

    public Instance<T> usingType(String valueType) {
        this.type = valueType;
        return this;
    }

    public Instance<T> using(ValueDefinition valueDefinition) {
        registerPropertyValues(valueDefinition);
        final InstanceValue instanceValue = new InstanceValue();
        instanceValue.setInstance(this);
        instanceValue.setDefinition(valueDefinition);
        instanceValue.setParentDefinition(valueDefinition.getParent());
        valueDefinition.setInstanceValue(instanceValue);
        return this;
    }

    void registerPropertyValues(ValueDefinition valueDefinition) {
        final Map<String, Property> unresolvedProps = new HashMap<String, Property>(propertyMap);
        for (final Map.Entry<ValuePredicate, Value> entry : valueDefinition.getPredicateValueMap().entrySet()) {
            final ValuePredicate predicate = entry.getKey();
            for (final Property property : propertyMap.values()) {
                final String propertyName = property.getName();
                if (!propertyValueMap.containsKey(propertyName)) {
                    if (predicate.apply(propertyName, property.getValueType())) {
                        addPropertyValue(propertyName, entry.getValue());
                        break;
                    }
                }
            }
        }
        if (valueDefinition.shouldIntrospect()) {
            unresolvedProps.keySet().removeAll(propertyValueMap.keySet());
            propertyValueMap.putAll(introspectTypes(valueDefinition, unresolvedProps));
        }
    }

    @SuppressWarnings("unchecked")
    Map<String, Value> introspectTypes(ValueDefinition valueDefinition, Map<String, Property> unresolvedProps) {
        final Set<Class> supportedTypes = getSupportedTypes();
        final Map<String, Value> propertyValueMap = new LinkedHashMap<String, Value>();
        for (final Map.Entry<String, Property> entry : unresolvedProps.entrySet()) {
            final String propertyName = entry.getKey();
            final Property property = entry.getValue();
            final Class propertyType = property.getType();
            Value val = null;
            if (supportedTypes.contains(propertyType)) {
                val = Random.of(propertyType);
            } else if(propertyType.isArray() && supportedTypes.contains(propertyType.getComponentType())) {
                if (!propertyType.getComponentType().isPrimitive()) { // todo Serge: fix primitives
                    val = new ValueArray(propertyType, Random.of(propertyType.getComponentType()));
                }
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

    protected Set<Class> getSupportedTypes() {
        final Set<Class> knownTypes = new HashSet<Class>();
        knownTypes.addAll(Incremental.getSupportedTypes());
        knownTypes.addAll(org.prismus.scrambler.value.Random.getSupportedTypes());
        return knownTypes;
    }

    List<Value> lookupConstructorArguments(ValueDefinition valueDefinition, Class type, Set<Class> supportedTypes) {
        List<Value> result = null;
        try {
            final Map<ValuePredicate, Value> typeValueMap = valueDefinition.getTypeValueMap();
            for (final Constructor ctor : type.getConstructors()) {
                final Class[] types = ctor.getParameterTypes();
                if (types == null) {
                    return new ArrayList<Value>();
                }
                final List<Value> values = new ArrayList<Value>();
                for (final Class argType : types) {
                    // todo Serge: fishy, instead, a full iteration should be done across type map on predicate verification
                    Value val = typeValueMap.get(new TypePredicate(argType));
                    if (val == null && supportedTypes.contains(argType)) {
                        val = Random.of(argType);
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
        } catch (Exception ignore) { }
        return result;
    }

    public void setPropertyValueMap(Map<String, Value> propertyValueMap) {
        this.propertyValueMap = propertyValueMap;
    }

    public Instance<T> addPropertyValue(String property, Value value) {
        propertyValueMap.put(property, value);
        return this;
    }

    public void setConstructorValues(List<Value> constructorValues) {
        this.constructorValues = constructorValues;
    }

    public Instance<T> using(List<Value> constructorValues) {
        this.constructorValues = constructorValues;
        return this;
    }

    public Instance<T> addConstructorValue(Value value) {
        constructorValues.add(value);
        return this;
    }

    void processPropertyMap(Object instance) {
        final Map<String, Object> resultMap = new HashMap<String, Object>(propertyValueMap.size());
        for (final Map.Entry<String, Value> entry : propertyValueMap.entrySet()) {
            resultMap.put(entry.getKey(), entry.getValue().next());
        }
        populate(instance, resultMap);
    }

    void populate(Object instance, Map<String, Object> properties) {
        try {
            for (final Map.Entry<String, Object> entry : properties.entrySet()) {
                setPropertyValue(instance, entry.getKey(), entry.getValue());
            }
        } catch (Exception e) {
            throw new RuntimeException(String.format(FAILED_SET_PROPERTIES_MSG, instance, properties));
        }
    }

    Map<String, Property> lookupPropertyDefinitions(Object instanceType) {
        final PropertyUtilsBean propertyUtils = beanUtilsBean.getPropertyUtils();
        final boolean isInstance = !(instanceType instanceof Class);
        final Map<String, Property> propertyDefinitionMap = new LinkedHashMap<String, Property>();

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
                propertyDefinitionMap.put(name, new Property(propertyDescriptor, value));
            }
            propertyDefinitionMap.remove("class");
            propertyDefinitionMap.remove("metaClass");
        }
        return propertyDefinitionMap;
    }

    void setPropertyValue(PropertyDescriptor propertyDescriptor, Object instance, Object value) {
        try {
            propertyDescriptor.getWriteMethod().invoke(instance, value);
        } catch (Exception ignore) { }
    }

    void setPropertyValue(PropertyUtilsBean propertyUtils, Object instance, String propertyName, Object value) {
        try {
            propertyUtils.setSimpleProperty(instance, propertyName, value);
        } catch (Exception ignore) { }
    }

    public void setPropertyValue(Object instance, String propertyName, Object value) {
        final Property property = propertyMap.get(propertyName);
        if (property != null) {
            setPropertyValue(property.propertyDescriptor, instance, value);
        } else {
            setPropertyValue(beanUtilsBean.getPropertyUtils(), instance, propertyName, value);
        }
    }

    public Object getPropertyValue(Object instance, String propertyName) {
        final Property property = propertyMap.get(propertyName);
        final Object value;
        if (property != null) {
            value = getPropertyValue(property.propertyDescriptor, instance);
        } else {
            value = getPropertyValue(beanUtilsBean.getPropertyUtils(), instance, propertyName);
        }
        return value;
    }

    Object getPropertyValue(PropertyDescriptor propertyDescriptor, Object instance) {
        Object value = null;
        try {
            value = propertyDescriptor.getReadMethod().invoke(instance);
        } catch (Exception ignore) { }
        return value;
    }

    Object getPropertyValue(PropertyUtilsBean propertyUtils, Object instance, String propertyName) {
        Object value = null;
        try {
            value = propertyUtils.getProperty(instance, propertyName);
        } catch (Exception ignore) { }
        return value;
    }

    @SuppressWarnings({"unchecked"})
    T checkCreateInstance() {
        T result = this.value;
        Object valueType = this.type;
        if (valueType instanceof String) {
            valueType = lookupType((String) valueType, true);
        }
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

    Object lookupType(String classType, boolean throwError) {
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

    public T next() {
        final T instance = checkCreateInstance();
        setValue(instance);
        processPropertyMap(instance);
        return instance;
    }

    Map<String, Value> getPropertyValueMap() {
        return propertyValueMap;
    }

    static class Property {
        final PropertyDescriptor propertyDescriptor;
        final Object value;

        public Property(PropertyDescriptor propertyDescriptor, Object value) {
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
