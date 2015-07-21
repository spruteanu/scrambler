package org.prismus.scrambler.value;

import org.apache.commons.beanutils.BeanUtilsBean;
import org.apache.commons.beanutils.ConstructorUtils;
import org.apache.commons.beanutils.ConvertUtilsBean;
import org.apache.commons.beanutils.converters.DateConverter;
import org.prismus.scrambler.builder.PropertyPredicate;
import org.prismus.scrambler.builder.ValuePredicate;

import java.util.*;

/**
 * @author Serge Pruteanu
 */
public abstract class Util {

    private static final String NOT_DEFINED_MSG = "not defined";
    private static final String FAILED_CREATE_INSTANCE0_MSG = "Failed to create instance of type: %s, arguments: %s";
    private static final String FAILED_CREATE_INSTANCE_MSG = "Failed to create instance of type: %s, arguments: %s, types: %s";

    private static final Set<Character> PREFIXED_CHAR_SET = new HashSet<Character>(Arrays.asList('+', '(', ')', '^', '$', '.', '{', '}', '[', ']', '|', '\\'));
    static Map<Class, Class> primitiveWrapperMap = new LinkedHashMap<Class, Class>() {{
        put(byte.class, Byte.class);
        put(short.class, Short.class);
        put(boolean.class, Boolean.class);
        put(double.class, Double.class);
        put(float.class, Float.class);
        put(int.class, Integer.class);
        put(long.class, Long.class);
    }};

    @SuppressWarnings({"unchecked"})
    public static Object createInstance(Class clazzType, Object[] arguments) {
        try {
            return ConstructorUtils.invokeConstructor(clazzType, arguments);
        } catch (Exception e) {
            throw new RuntimeException(String.format(FAILED_CREATE_INSTANCE0_MSG,
                    clazzType, checkNotDefinedMessage(arguments)
            ), e);
        }
    }

    @SuppressWarnings({"unchecked"})
    public static Object createInstance(Class clazzType,
                                        Object[] arguments,
                                        Class[] classes) {
        try {
            return ConstructorUtils.invokeConstructor(clazzType, arguments, classes);
        } catch (Exception e) {
            throw new RuntimeException(String.format(FAILED_CREATE_INSTANCE_MSG,
                    clazzType, checkNotDefinedMessage(arguments), checkNotDefinedMessage(classes)
            ), e);
        }
    }

    private static Object checkNotDefinedMessage(Object[] arguments) {
        return arguments != null ? Arrays.asList(arguments) : NOT_DEFINED_MSG;
    }

    public static void checkNullValue(Object value) {
        if (value == null) {
            throw new IllegalArgumentException("Value can't be null");
        }
    }

    public static void checkEmpty(String value) {
        if (value == null || value.length() == 0) {
            throw new IllegalArgumentException("Value can't be null or empty");
        }
    }

    public static void checkEmptyCollection(Collection values) {
        if (values.size() == 0) {
            throw new IllegalArgumentException("Values collection can't be empty");
        }
    }

    public static void checkNullValue(Object minimum, Object maximum) {
        if (minimum == null && maximum == null) {
            throw new IllegalArgumentException("Either minimum or maximum should be not null");
        }
    }

    public static ValuePredicate createPropertyPredicate(String propertyWildcard) {
        checkEmpty(propertyWildcard);
        return new PropertyPredicate(propertyWildcard);
    }

    public static Number getNotNullValue(Number minimum, Number maximum) {
        Number value = minimum;
        if (value == null) {
            value = maximum;
        }
        return value;
    }

    public static BeanUtilsBean createBeanUtilsBean() {
        final ConvertUtilsBean convertUtilsBean = new ConvertUtilsBean();
        final DateConverter dateConverter = new DateConverter();
        dateConverter.setPatterns(new String[]{"MM/dd/yyyy HH:mm:ss.SSS", "MM/dd/yyyy HH:mm:ss", "MM/dd/yyyy", "EEE MMM dd HH:mm:ss zzz yyyy"});
        convertUtilsBean.register(dateConverter, Date.class);
        return new BeanUtilsBean(convertUtilsBean);
    }

    public static String replaceWildcards(String wildcardPattern) {
        final StringBuilder builder = new StringBuilder();
        builder.append('^');
        boolean replaced = false;
        final int length = wildcardPattern.length();
        for (int i = 0; i < length; ++i) {
            final char ch = wildcardPattern.charAt(i);
            if (ch == '*') {
                builder.append(".*");
                replaced = true;
            } else if (ch == '?') {
                builder.append(".");
                replaced = true;
            } else if (PREFIXED_CHAR_SET.contains(ch)) {
                builder.append('\\').append(ch);
            } else {
                builder.append(ch);
            }
        }
        builder.append('$');
        return replaced ? builder.toString() : wildcardPattern;
    }

    static void checkPositiveCount(Integer count) {
        if (count != null && count < 0) {
            throw new IllegalArgumentException(String.format("Count should be a positive number: %s", count));
        }
    }
}
