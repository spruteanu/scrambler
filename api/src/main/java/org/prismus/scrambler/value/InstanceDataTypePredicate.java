package org.prismus.scrambler.value;

import org.prismus.scrambler.DataPredicate;

import java.util.regex.Pattern;

/**
 * Data predicate that filters non JDK classes, used internally
 *
 * @author Serge Pruteanu
 */
public class InstanceDataTypePredicate implements DataPredicate {
    private Pattern pattern = Pattern.compile("^(?!java).+");

    @Override
    public boolean apply(String property, Object value) {
        if (value == null) {
            return false;
        }
        final Class clazzType = value instanceof Class ? (Class<?>) value : value.getClass();
        return !clazzType.isPrimitive() && !clazzType.isArray() && pattern.matcher(clazzType.getName()).matches();
    }

}
