package org.prismus.scrambler.data;

import org.prismus.scrambler.Data;

import java.util.ArrayList;

/**
 * Class is an internal implementation to allow definitions of incremental types at runtime for provided clazzType.
 * Used mostly in default definitions declaration.
 *
 * @author Serge Pruteanu
 */
public class IncrementalTypeData<T> implements Data<Data<T>> {
    private Object[] arguments;

    public IncrementalTypeData() {
    }

    public IncrementalTypeData(Object... arguments) {
        usingArguments(arguments);
    }

    public IncrementalTypeData usingArguments(Object... arguments) {
        this.arguments = arguments;
        return this;
    }

    @SuppressWarnings("unchecked")
    public Data<T> next(Class<T> clazzType) {
        final Class<? extends Data> dataType = Types.incrementTypeMap.get(clazzType);
        if (dataType == null) {
            throw new UnsupportedOperationException(String.format("Incremental type object is not supported for provided clazz type: %s. Supported incremental types are: %s", clazzType, new ArrayList(Types.incrementTypeMap.keySet())));
        }
        return (Data<T>) Util.createInstance(dataType, arguments);
    }

    @Override
    public Data<T> next() {
        throw new UnsupportedOperationException("Next object is not supported in given implementation");
    }

    @Override
    public Data<T> get() {
        throw new UnsupportedOperationException("Get object is not supported in given implementation");
    }

    @Override
    public Object clone() throws CloneNotSupportedException {
        return super.clone();
    }

}
