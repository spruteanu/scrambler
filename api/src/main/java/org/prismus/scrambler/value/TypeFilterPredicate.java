package org.prismus.scrambler.value;

import org.prismus.scrambler.ValuePredicate;

import java.util.regex.Pattern;

/**
 * Value predicate that matches classes by name
 *
 * @author Serge Pruteanu
 */
public class TypeFilterPredicate implements ValuePredicate {
    private final Pattern pattern;

    public TypeFilterPredicate(Pattern pattern) {
        this.pattern = pattern;
    }

    @Override
    public boolean apply(String property, Object value) {
        final Class clazzType = value instanceof Class ? (Class<?>) value : value.getClass();
        return pattern.matcher(clazzType.getName()).matches();
    }

    @Override
    public int hashCode() {
        return pattern.hashCode();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        final TypeFilterPredicate predicate = (TypeFilterPredicate) o;
        return pattern.equals(predicate.pattern);
    }

    @Override
    public String toString() {
        return String.format("Matches types by regular expression: '%s'", pattern.toString());
    }
}