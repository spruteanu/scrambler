package org.prismus.scrambler.data;

import org.prismus.scrambler.DataPredicate;

import java.util.regex.Pattern;

/**
 * Data predicate that matches classes by name
 *
 * @author Serge Pruteanu
 */
public class TypeFilterPredicate implements DataPredicate {
    private final Pattern pattern;

    public TypeFilterPredicate(Pattern pattern) {
        this.pattern = pattern;
    }

    @Override
    public boolean matches(String property, Object data) {
        final Class clazzType = data instanceof Class ? (Class<?>) data : data.getClass();
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
