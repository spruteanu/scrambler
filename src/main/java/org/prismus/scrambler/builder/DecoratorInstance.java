package org.prismus.scrambler.builder;

import org.prismus.scrambler.property.Constant;

/**
 * todo: add description
 *
 * @author Serge Pruteanu
 */
public class DecoratorInstance<T> extends Constant<T> {
    protected final Instance<T> instance;

    public DecoratorInstance() {
        this(new Instance<T>());
    }

    public DecoratorInstance(Instance<T> instance) {
        this.instance = instance;
    }

    @Override
    public void setName(String name) {
        instance.setName(name);
    }

    @Override
    public void setValue(T value) {
        instance.setValue(value);
    }

    @Override
    public DecoratorInstance<T> usingValue(T value) {
        instance.usingValue(value);
        return this;
    }

    @Override
    public String getName() {
        return instance.getName();
    }

    @Override
    public T getValue() {
        return instance.getValue();
    }

    @Override
    public T value() {
        return instance.value();
    }
}
