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

    public Class getType() {
        return type;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        TypePredicate that = (TypePredicate) o;
        return type.equals(that.type);
    }

    @Override
    public int hashCode() {
        return type.hashCode();
    }

}
