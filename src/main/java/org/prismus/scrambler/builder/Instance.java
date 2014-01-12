package org.prismus.scrambler.builder;

import org.apache.commons.beanutils.BeanUtilsBean;
import org.apache.commons.beanutils.PropertyUtilsBean;
import org.prismus.scrambler.Value;
import org.prismus.scrambler.value.Constant;
import org.prismus.scrambler.value.Util;

import java.beans.PropertyDescriptor;
import java.lang.reflect.Method;
import java.util.*;

/**
 * todo: add description
 *
 * @author Serge Pruteanu
 */
public class Instance<T> extends Constant<T> {
    private static final String FAILED_SET_PROPERTIES_MSG = "Failed to set instance: %s with properties: %s";

    // todo add introspection property generation on class/instance/method
    // todo add new instance creation property value, with propertyValueMap introspection/matching on constructor arguments
    // todo add DB table introspection
    // todo review/get rid of (where possible) external library dependencies
    // todo add tests
    private Object valueType;
    protected List<Value> constructorValues;
    protected Map<String, Property> propertyMap;
    protected Map<String, Value> propertyValueMap;
    protected BeanUtilsBean beanUtilsBean;

    public Instance() {
        this((T)null);
    }

    public Instance(String valueType) {
        super(null);
        this.valueType = lookupType(valueType, false);
        if (this.valueType == null) {
            this.valueType = valueType;
        }
        initialize();
    }

    public Instance(Class valueType) {
        super(null);
        this.valueType = valueType;
        initialize();
    }

    public Instance(T instance) {
        super(instance);
        initialize();
    }

    void initialize() {
        constructorValues = new ArrayList<Value>();
        propertyValueMap = new LinkedHashMap<String, Value>();
        beanUtilsBean = Util.createBeanUtilsBean();
        propertyMap = lookupPropertyDefinitions(value != null ? value : valueType);
    }

    public Instance<T> usingValueType(Object valueType) {
        this.valueType = valueType;
        return this;
    }

    public Instance<T> usingValueDefinition(ValueDefinition valueDefinition) {
        for (final Map.Entry<ValuePredicate, Value> entry : valueDefinition.getPredicateValueMap().entrySet()) {
            final ValuePredicate predicate = entry.getKey();
            for (final Property property : propertyMap.values()) {
                final String name = property.getName();
                if (!propertyValueMap.containsKey(name)) {
                    if (predicate.apply(name, property.getValueType())) {
                        addPropertyValue(name, entry.getValue());
                        break;
                    }
                }
            }
        }
        return this;
    }

    public Map<String, Property> getPropertyMap() {
        return propertyMap;
    }

    public void setPropertyMap(Map<String, Property> propertyMap) {
        this.propertyMap = propertyMap;
    }

    public void setPropertyValueMap(Map<String, Value> propertyValueMap) {
        this.propertyValueMap = propertyValueMap;
    }

    public Instance<T> addPropertyValue(String property, Value value) {
        propertyValueMap.put(property, value);
        return this;
    }

    public List<Value> getConstructorValues() {
        return constructorValues;
    }

    public void setConstructorValues(List<Value> constructorValues) {
        this.constructorValues = constructorValues;
    }

    public Instance<T> usingConstructorArguments(List<Value> constructorValues) {
        this.constructorValues = constructorValues;
        return this;
    }

    public Instance<T> addConstructorValue(Value value) {
        constructorValues.add(value);
        return this;
    }

    public void setBeanUtilsBean(BeanUtilsBean beanUtilsBean) {
        this.beanUtilsBean = beanUtilsBean;
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
        final PropertyDescriptor[] propertyDescriptors;
        final PropertyUtilsBean propertyUtils = beanUtilsBean.getPropertyUtils();
        final boolean isInstance = !(instanceType instanceof Class);
        if (isInstance) {
            propertyDescriptors = propertyUtils.getPropertyDescriptors(instanceType);
        } else {
            propertyDescriptors = propertyUtils.getPropertyDescriptors(((Class) instanceType));
        }
        final Map<String, Property> propertyDefinitionMap = new LinkedHashMap<String, Property>();
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
        } catch (Exception ignore) {
        }
    }

    void setPropertyValue(PropertyUtilsBean propertyUtils, Object instance, String propertyName, Object value) {
        try {
            propertyUtils.setSimpleProperty(instance, propertyName, value);
        } catch (Exception ignore) {
        }
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
        } catch (Exception ignore) {
        }
        return value;
    }

    Object getPropertyValue(PropertyUtilsBean propertyUtils, Object instance, String propertyName) {
        Object value = null;
        try {
            value = propertyUtils.getProperty(instance, propertyName);
        } catch (Exception ignore) {
        }
        return value;
    }

    @SuppressWarnings({"unchecked"})
    T checkCreateInstance() {
        T result = this.value;
        Object valueType = this.valueType;
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
        processPropertyMap(instance);
        setValue(instance);
        return instance;
    }

    BeanUtilsBean getBeanUtilsBean() {
        return beanUtilsBean;
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
