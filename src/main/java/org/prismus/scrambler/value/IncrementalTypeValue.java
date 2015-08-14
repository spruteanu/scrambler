package org.prismus.scrambler.value;

import org.prismus.scrambler.Value;

import java.util.ArrayList;

/**
 * Class is an internal implementation to allow definitions of incremental types at runtime for provided clazzType.
 * Used mostly in default definitions declaration.
 *
 * @author Serge Pruteanu
 */
public class IncrementalTypeValue<T> implements Value<Value<T>> {
    private Object[] arguments;

    public IncrementalTypeValue() {
    }

    public IncrementalTypeValue(Object... arguments) {
        usingArguments(arguments);
    }

    public IncrementalTypeValue usingArguments(Object... arguments) {
        this.arguments = arguments;
        return this;
    }

    @SuppressWarnings("unchecked")
    public Value<T> next(Class<T> clazzType) {
        final Class<? extends Value> valueClass = Types.incrementTypeMap.get(clazzType);
        if (valueClass == null) {
            throw new UnsupportedOperationException(String.format("Incremental type value is not supported for provided clazz type: %s. Supported incremental types are: %s", clazzType, new ArrayList(Types.incrementTypeMap.keySet())));
        }
        return (Value<T>) Util.createInstance(valueClass, arguments);
    }

    @Override
    public Value<T> next() {
        throw new UnsupportedOperationException("Next value is not supported in given implementation");
    }

    @Override
    public Value<T> get() {
        throw new UnsupportedOperationException("Get value is not supported in given implementation");
    }

    @Override
    public Object clone() throws CloneNotSupportedException {
        return super.clone();
    }

}
