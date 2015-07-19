package org.prismus.scrambler.builder;

/**
 * todo: add description
 *
 * @author Serge Pruteanu
 */
public class TypePredicate implements ValuePredicate {
    private Class type;

    public TypePredicate() {
    }

    public TypePredicate(Class type) {
        this.type = type;
    }

    @SuppressWarnings("unchecked")
    @Override
    public boolean apply(String property, Object value) {
        return value instanceof Class ? type.isAssignableFrom((Class<?>) value) : type.isInstance(value);
    }

    public void setType(Class type) {
        this.type = type;
    }

    public TypePredicate withType(Class type) {
        this.type = type;
        return this;
    }

    @Override
    public boolean isSame(ValuePredicate predicate) {
        if (predicate instanceof PropertyPredicate) {

        } else if (predicate instanceof TypePredicate) {

        }
        return false;
    }

    @Override
    public String toString() {
        return type.toString();
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

    public static TypePredicate of(Class type) {
        return new TypePredicate(type);
    }

}
