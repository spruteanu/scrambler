package org.prismus.scrambler.test;

import org.prismus.scrambler.Value;

import java.lang.reflect.Field;

/**
 * todo: add description
 *
 * @author Serge Pruteanu
 */
public class FieldContext extends ExecutionContext {
    private final Field field;

    FieldContext(Object inspected, Field field) {
        this(inspected, field, null);
    }

    FieldContext(Object inspected, Field field, String message) {
        super(inspected);
        this.field = field;
        field.setAccessible(true);
        this.message = message;
    }

    @Override
    public Object getInspected() {
        final Object value = ((Value)inspected).get();
        try {
            return field.get(value);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(String.format("Failed to get field: %s value from instance: %s", field.getName(), value), e);
        }
    }

}
