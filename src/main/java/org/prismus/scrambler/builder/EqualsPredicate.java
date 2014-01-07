package org.prismus.scrambler.builder;

/**
 * todo: add description
 *
 * @author Serge Pruteanu
 */
public class EqualsPredicate<V> implements ValuePredicate<V> {
    private V value;

    public void setValue(V value) {
        this.value = value;
    }

    @Override
    public boolean apply(V value) {
        return this.value.equals(value);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        EqualsPredicate that = (EqualsPredicate) o;
        return value.equals(that.value);
    }

    @Override
    public int hashCode() {
        return value.hashCode();
    }
}
