package org.prismus.scrambler.data;

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
    public boolean matches(String property, Object data) {
        if (data == null) {
            return false;
        }
        final Class clazzType = data instanceof Class ? (Class<?>) data : data.getClass();
        return !clazzType.isPrimitive() && !clazzType.isArray() && pattern.matcher(clazzType.getName()).matches();
    }

}
