package org.prismus.scrambler.value;

import org.prismus.scrambler.ValuePredicate;

import java.util.regex.Pattern;

/**
 * Value predicate that filters non JDK classes, used internally
 *
 * @author Serge Pruteanu
 */
public class InstanceValueTypePredicate implements ValuePredicate {
    private Pattern pattern = Pattern.compile("^(?!java).+");

    @Override
    public boolean apply(String property, Object value) {
        final Class clazzType = value instanceof Class ? (Class<?>) value : value.getClass();
        return !clazzType.isPrimitive() && !clazzType.isArray() && pattern.matcher(clazzType.getName()).matches();
    }

}
