package org.prismus.scrambler.value;

import org.prismus.scrambler.Value;

/**
 * Class is an internal implementation to allow definitions of InstanceValue type at runtime for provided clazzType.
 * Class is used in default definitions declaration, when unknown types are resolved.
 *
 * @author Serge Pruteanu
 */
public class InstanceTypeValue<T> implements Value<InstanceValue<T>> {
    private InstanceValue<T> instanceValue;

    public InstanceTypeValue() {
    }

    public InstanceTypeValue(InstanceValue<T> instanceValue) {
        usingInstance(instanceValue);
    }

    @SuppressWarnings("unchecked")
    public InstanceValue<T> next(Class<T> clazzType) {
        InstanceValue<T> value = instanceValue;
        if (value == null) {
            value = new InstanceValue<T>(clazzType);
        } else {
            try {
                value = (InstanceValue<T>) value.clone();
            } catch (CloneNotSupportedException e) {
                throw new RuntimeException(String.format("Failed to clone instance value: %s", instanceValue), e);
            }
            value.ofType(clazzType);
        }
        return value;
    }

    public InstanceTypeValue usingInstance(InstanceValue<T> instanceValue) {
        this.instanceValue = instanceValue;
        return this;
    }

    @Override
    public InstanceValue<T> next() {
        throw new UnsupportedOperationException("Next value is not supported in given implementation");
    }

    @Override
    public InstanceValue<T> get() {
        throw new UnsupportedOperationException("Get value is not supported in given implementation");
    }

    @Override
    public Object clone() throws CloneNotSupportedException {
        return super.clone();
    }

}
