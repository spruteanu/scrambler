package org.prismus.scrambler.value;

import org.prismus.scrambler.Data;
import org.prismus.scrambler.ObjectScrambler;

/**
 * Class is an internal implementation to allow definitions of random types at runtime for provided clazzType.
 * Class is used in default definitions declaration, when unknown types are resolved.
 *
 * @author Serge Pruteanu
 */
public class RandomTypeData<T> implements Data<Data<T>> {
    private Object[] arguments;

    public RandomTypeData() {
    }

    public RandomTypeData(Object... arguments) {
        usingArguments(arguments);
    }

    @SuppressWarnings("unchecked")
    public Data<T> next(Class<T> clazzType) {
        Data<T> data = null;
        if (arguments == null) {
            data = ObjectScrambler.random(clazzType);
        } else if (arguments.length == 1) {
            data = ObjectScrambler.random(clazzType, (T) arguments[0]);
        } else if (arguments.length == 2) {
            data = ObjectScrambler.random(clazzType, (T) arguments[0], (T) arguments[1]);
        }
        return data;
    }

    public RandomTypeData usingArguments(Object... arguments) {
        this.arguments = arguments;
        return this;
    }

    @Override
    public Data<T> next() {
        throw new UnsupportedOperationException("Next value is not supported in given implementation");
    }

    @Override
    public Data<T> get() {
        throw new UnsupportedOperationException("Get value is not supported in given implementation");
    }

    @Override
    public Object clone() throws CloneNotSupportedException {
        return super.clone();
    }

}
