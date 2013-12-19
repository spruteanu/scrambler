package org.prismus.scrambler.builder;

import org.prismus.scrambler.Value;

import java.beans.PropertyDescriptor;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * todo: add description
 *
 * @author Serge Pruteanu
 */
public class IntrospectionInstance<T> extends DecoratorInstance<T> {

    private Map<String, PropertyDescriptor> propertyDescriptorMap;

    public IntrospectionInstance() {
        this(new Instance<T>());
    }

    public IntrospectionInstance(Instance<T> instance) {
        super(instance);
        propertyDescriptorMap = lookupInstanceValue(value);
    }

    @Override
    public void setValue(T value) {
        propertyDescriptorMap = lookupInstanceValue(value);
        super.setValue(value);
    }

    @Override
    public IntrospectionInstance<T> usingValue(T value) {
        propertyDescriptorMap = lookupInstanceValue(value);
        super.usingValue(value);
        return this;
    }

    Map<String, PropertyDescriptor> lookupInstanceValue(T instanceValue) {
        final PropertyDescriptor[] propertyDescriptors;
        if (instanceValue instanceof Class) {
            propertyDescriptors = instance.getBeanUtilsBean().getPropertyUtils().getPropertyDescriptors(((Class) instanceValue));
        } else {
            propertyDescriptors = instance.getBeanUtilsBean().getPropertyUtils().getPropertyDescriptors(instanceValue);
        }
        final Map<String, PropertyDescriptor> propertyDescriptorMap = new LinkedHashMap<String, PropertyDescriptor>();
        if (propertyDescriptors != null) {
            for (final PropertyDescriptor propertyDescriptor : propertyDescriptors) {
                propertyDescriptorMap.put(propertyDescriptor.getName(), propertyDescriptor);
            }
        }
        return propertyDescriptorMap;
    }

    public IntrospectionInstance<T> property(Value value, Value... properties) {
        throw new UnsupportedOperationException("The property method is not supported for given implementation");
    }

    public IntrospectionInstance<T> values(String property, Object value) {
        throw new UnsupportedOperationException("The matchProperties method is not supported for given implementation");
    }

    public IntrospectionInstance<T> incremental(String property, Object value) {
        throw new UnsupportedOperationException("The incremental method is not supported for given implementation");
    }

    public IntrospectionInstance<T> random(String property, Object value) {
        throw new UnsupportedOperationException("The matchProperties method is not supported for given implementation");
    }

    public IntrospectionInstance<T> random(String property, Object minimum, Object maximum) {
        throw new UnsupportedOperationException("The matchProperties method is not supported for given implementation");
    }

    public IntrospectionInstance<T> values(String property, String value, String... propertyValues) {
        throw new UnsupportedOperationException("The matchProperties method is not supported for given implementation");
    }

    public IntrospectionInstance<T> incremental(String property, String value, String... propertyValues) {
        throw new UnsupportedOperationException("The incremental method is not supported for given implementation");
    }

    public IntrospectionInstance<T> random(String property, String value, String... propertyValues) {
        throw new UnsupportedOperationException("The matchProperties method is not supported for given implementation");
    }

    public IntrospectionInstance<T> randomAll() {
        throw new UnsupportedOperationException("The randomAll method is not supported for given implementation");
    }

    public IntrospectionInstance<T> incrementalAll() {
        throw new UnsupportedOperationException("The incrementalAll method is not supported for given implementation");
    }
}
