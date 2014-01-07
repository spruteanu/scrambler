package org.prismus.scrambler.builder;

import org.apache.commons.beanutils.BeanUtilsBean;
import org.prismus.scrambler.Value;
import org.prismus.scrambler.property.Constant;
import org.prismus.scrambler.property.Util;

import java.beans.PropertyDescriptor;
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
    // todo create a facade class which will integrate all builder/org.prismus.scrambler.Property functionalities
    // todo externalize messages
    // todo review/get rid of (where possible) external library dependencies
    // todo add tests
    // todo review usability all the time :)
    private Object valueType;
    protected List<Value> constructorValues;
    protected Map<String, Value> propertyValueMap;
    protected BeanUtilsBean beanUtilsBean;

    public Instance() {
        this((T)null);
    }

    public Instance(String valueType) {
        super(null);
        this.valueType = valueType;
        initialize();
    }

    public Instance(Class valueType) {
        super(null);
        this.valueType = valueType;
        initialize();
    }

    public Instance(T instance) {
        super(instance);
        valueType = null;
        initialize();
    }

    void initialize() {
        constructorValues = new ArrayList<Value>();
        propertyValueMap = new LinkedHashMap<String, Value>();
        beanUtilsBean = Util.createBeanUtilsBean();
    }

    public Instance<T> usingValueType(Object valueType) {
        this.valueType = valueType;
        return this;
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
            beanUtilsBean.populate(instance, properties);
        } catch (Exception e) {
            throw new RuntimeException(String.format(FAILED_SET_PROPERTIES_MSG, instance, properties));
        }
    }

    Map<String, PropertyDescriptor> lookupPropertyDescriptors(Object instanceType) {
        final PropertyDescriptor[] propertyDescriptors;
        if (instanceType instanceof Class) {
            propertyDescriptors = beanUtilsBean.getPropertyUtils().getPropertyDescriptors(((Class) instanceType));
        } else {
            propertyDescriptors = beanUtilsBean.getPropertyUtils().getPropertyDescriptors(instanceType);
        }
        final Map<String, PropertyDescriptor> propertyDescriptorMap = new LinkedHashMap<String, PropertyDescriptor>();
        if (propertyDescriptors != null) {
            for (final PropertyDescriptor propertyDescriptor : propertyDescriptors) {
                propertyDescriptorMap.put(propertyDescriptor.getName(), propertyDescriptor);
            }
            propertyDescriptorMap.remove("class");
        }
        return propertyDescriptorMap;
    }

    @SuppressWarnings({"unchecked"})
    T checkCreateInstance() {
        T result = this.value;
        Object valueType = this.valueType;
        if (valueType instanceof String) {
            try {
                valueType = Class.forName(((String) valueType));
            } catch (ClassNotFoundException e) {
                throw new IllegalStateException(String.format("Not found class for specified value: %s", result), e);
            }
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

    public T next() {
        final T instance = checkCreateInstance();
        processPropertyMap(instance);
        return instance;
    }

    BeanUtilsBean getBeanUtilsBean() {
        return beanUtilsBean;
    }

    Map<String, Value> getPropertyValueMap() {
        return propertyValueMap;
    }

}
