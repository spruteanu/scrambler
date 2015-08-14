package org.prismus.scrambler.value;

import org.prismus.scrambler.ValuePredicate;

import java.util.regex.Pattern;

/**
 * Value predicate that matches classes by name
 *
 * @author Serge Pruteanu
 */
public class TypeFilterPredicate implements ValuePredicate {
    private Pattern pattern;

    public TypeFilterPredicate(Pattern pattern) {
        this.pattern = pattern;
    }

    @Override
    public boolean apply(String property, Object value) {
        final Class clazzType = value instanceof Class ? (Class<?>) value : value.getClass();
        return pattern.matcher(clazzType.getName()).matches();
    }

}
