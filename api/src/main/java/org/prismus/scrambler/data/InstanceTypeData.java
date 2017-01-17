package org.prismus.scrambler.data;

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
        InstanceData<T> data = instanceData;
        if (data == null) {
            data = new InstanceData<T>(clazzType);
        } else {
            try {
                data = (InstanceData<T>) data.clone();
            } catch (CloneNotSupportedException e) {
                throw new RuntimeException(String.format("Failed to clone instance object: %s", instanceData), e);
            }
            data.ofType(clazzType);
        }
        return data;
    }

    public InstanceTypeData usingInstance(InstanceData<T> instanceData) {
        this.instanceData = instanceData;
        return this;
    }

    @Override
    public InstanceData<T> next() {
        throw new UnsupportedOperationException("Next object is not supported in given implementation");
    }

    @Override
    public InstanceData<T> get() {
        throw new UnsupportedOperationException("Get object is not supported in given implementation");
    }

    @Override
    public Object clone() throws CloneNotSupportedException {
        return super.clone();
    }

}
