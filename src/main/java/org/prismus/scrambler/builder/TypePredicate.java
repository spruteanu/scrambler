package org.prismus.scrambler.builder;

/**
 * todo: add description
 *
 * @author Serge Pruteanu
 */
public class TypePredicate<V> implements ValuePredicate<Class> {
    private Class type;

    @SuppressWarnings("unchecked")
    @Override
    public boolean apply(Class value) {
        return type.isAssignableFrom(value);
    }

    public void setType(Class type) {
        this.type = type;
    }

}
