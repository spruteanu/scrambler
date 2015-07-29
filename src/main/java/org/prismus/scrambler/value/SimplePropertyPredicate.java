package org.prismus.scrambler.value;

import org.prismus.scrambler.ValuePredicate;

/**
 * Property predicate, represents a simple property, matched by name
 *
 * @author Serge Pruteanu
 */
public class SimplePropertyPredicate implements ValuePredicate {
    private String property;

    public SimplePropertyPredicate(String property) {
        this.property = property;
    }

    @Override
    public boolean apply(String property, Object value) {
        return this.property.equalsIgnoreCase(property);
    }

    public String getProperty() {
        return property;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        SimplePropertyPredicate that = (SimplePropertyPredicate) o;
        return property.equals(that.property);
    }

    @Override
    public int hashCode() {
        return property.hashCode();
    }

    @Override
    public String toString() {
        return property;
    }
}
