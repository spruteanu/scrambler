package org.prismus.scrambler.value;

import org.prismus.scrambler.ObjectScrambler;
import org.prismus.scrambler.Value;

/**
 * Class is an internal implementation to allow definitions of random types at runtime for provided clazzType.
 * Class is used in default definitions declaration, when unknown types are resolved.
 *
 * @author Serge Pruteanu
 */
public class RandomTypeValue<T> implements Value<Value<T>> {
    private Object[] arguments;

    public RandomTypeValue() {
    }

    public RandomTypeValue(Object... arguments) {
        usingArguments(arguments);
    }

    @SuppressWarnings("unchecked")
    public Value<T> next(Class<T> clazzType) {
        Value<T> value = null;
        if (arguments == null) {
            value = ObjectScrambler.random(clazzType);
        } else if (arguments.length == 1) {
            value = ObjectScrambler.random(clazzType, (T) arguments[0]);
        } else if (arguments.length == 2) {
            value = ObjectScrambler.random(clazzType, (T) arguments[0], (T) arguments[1]);
        }
        return value;
    }

    public RandomTypeValue usingArguments(Object... arguments) {
        this.arguments = arguments;
        return this;
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
