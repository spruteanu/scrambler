package org.prismus.scrambler.value;

import org.prismus.scrambler.Data;

/**
 * Class is an internal implementation to allow definitions of InstanceData type at runtime for provided clazzType.
 * Class is used in default definitions declaration, when unknown types are resolved.
 *
 * @author Serge Pruteanu
 */
public class InstanceTypeData<T> implements Data<InstanceData<T>> {
    private InstanceData<T> instanceData;

    public InstanceTypeData() {
    }

    public InstanceTypeData(InstanceData<T> instanceData) {
        usingInstance(instanceData);
    }

    @SuppressWarnings("unchecked")
    public InstanceData<T> next(Class<T> clazzType) {
        InstanceData<T> value = instanceData;
        if (value == null) {
            value = new InstanceData<T>(clazzType);
        } else {
            try {
                value = (InstanceData<T>) value.clone();
            } catch (CloneNotSupportedException e) {
                throw new RuntimeException(String.format("Failed to clone instance value: %s", instanceData), e);
            }
            value.ofType(clazzType);
        }
        return value;
    }

    public InstanceTypeData usingInstance(InstanceData<T> instanceData) {
        this.instanceData = instanceData;
        return this;
    }

    @Override
    public InstanceData<T> next() {
        throw new UnsupportedOperationException("Next value is not supported in given implementation");
    }

    @Override
    public InstanceData<T> get() {
        throw new UnsupportedOperationException("Get value is not supported in given implementation");
    }

    @Override
    public Object clone() throws CloneNotSupportedException {
        return super.clone();
    }

}
