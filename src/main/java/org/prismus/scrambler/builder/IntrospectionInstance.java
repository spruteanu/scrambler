package org.prismus.scrambler.builder;

import java.util.Date;

/**
 * todo: add description
 *
 * @author Serge Pruteanu
 */
public class IntrospectionInstance<T> extends DecoratorInstance<T> {

    public IntrospectionInstance<T> constant(String property, Object value) {
        throw new UnsupportedOperationException("The matchProperties method is not supported for given implementation");
    }

    public IntrospectionInstance<T> incremental(String property, Object value) {
        throw new UnsupportedOperationException("The incremental method is not supported for given implementation");
    }

    public IntrospectionInstance<T> incremental(String property, Object value, Object step) {
        throw new UnsupportedOperationException("The incremental method is not supported for given implementation");
    }

    public IntrospectionInstance<T> incremental(String property, Date value, int step) {
        throw new UnsupportedOperationException("The incremental method is not supported for given implementation");
    }

    public IntrospectionInstance<T> incremental(String property, Date value, int step, int calendarField) {
        throw new UnsupportedOperationException("The incremental method is not supported for given implementation");
    }

    public IntrospectionInstance<T> incremental(String property, String value, String pattern, Integer index) {
        throw new UnsupportedOperationException("The incremental method is not supported for given implementation");
    }

    public IntrospectionInstance<T> random(String property, Object value) {
        throw new UnsupportedOperationException("The matchProperties method is not supported for given implementation");
    }

    public IntrospectionInstance<T> random(String property, Object minimum, Object maximum) {
        throw new UnsupportedOperationException("The matchProperties method is not supported for given implementation");
    }

    public IntrospectionInstance<T> random(String property, Object value, Object minimum, Object maximum) {
        throw new UnsupportedOperationException("The matchProperties method is not supported for given implementation");
    }

    public IntrospectionInstance<T> random(String property, String value) {
        throw new UnsupportedOperationException("The matchProperties method is not supported for given implementation");
    }

    public IntrospectionInstance<T> random(String property, String value, Integer count) {
        throw new UnsupportedOperationException("The matchProperties method is not supported for given implementation");
    }

    public IntrospectionInstance<T> random(String property, String value, Integer count, boolean includeLetters) {
        throw new UnsupportedOperationException("The matchProperties method is not supported for given implementation");
    }

    public IntrospectionInstance<T> random(String property, String value, Integer count, boolean includeLetters, boolean includeNumbers) {
        throw new UnsupportedOperationException("The matchProperties method is not supported for given implementation");
    }

}
