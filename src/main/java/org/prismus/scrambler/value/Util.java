package org.prismus.scrambler.value;

import org.apache.commons.beanutils.BeanUtilsBean;
import org.apache.commons.beanutils.ConstructorUtils;
import org.apache.commons.beanutils.ConvertUtilsBean;
import org.apache.commons.beanutils.converters.DateConverter;
import org.prismus.scrambler.Value;
import org.prismus.scrambler.builder.PropertyPredicate;
import org.prismus.scrambler.builder.ValuePredicate;

import java.util.*;
import java.util.regex.Pattern;

/**
 * @author Serge Pruteanu
 */
public abstract class Util {

    private static final String NOT_DEFINED_MSG = "not defined";
    private static final String FAILED_CREATE_INSTANCE0_MSG = "Failed to create instance of type: %s, arguments: %s";
    private static final String FAILED_CREATE_INSTANCE_MSG = "Failed to create instance of type: %s, arguments: %s, types: %s";

    private static final Set<Character> PREFIXED_CHAR_SET = new HashSet<Character>(Arrays.asList('+', '(', ')', '^', '$', '.', '{', '}', '[', ']', '|', '\\'));

    @SuppressWarnings({"unchecked"})
    public static Object createInstance(Class clazzType,
                                        Object[] arguments) {
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

    public static <V> Map<Pattern, V> getPatternObjectMap(Map<String, V> regExObjectMap) {
        final Map<Pattern, V> patternObjectMap = new HashMap<Pattern, V>();
        for (final Map.Entry<String, V> entry : regExObjectMap.entrySet()) {
            patternObjectMap.put(Pattern.compile(replaceWildcards(entry.getKey()), Pattern.CASE_INSENSITIVE), entry.getValue());
        }
        return patternObjectMap;
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

    static <V> void validateArguments(Collection<V> value, Value<V> property) {
        if (value == null || property == null) {
            throw new IllegalArgumentException("Collection/property instances should not be null");
        }
    }

    static void validateArguments(Class valueType, Object array, Value property) {
        if (array == null) {
            if (valueType == null) {
                throw new IllegalArgumentException("Array instance or array type should not be null");
            }
        }
        if (property == null) {
            throw new IllegalArgumentException("Value instance should not be null");
        }
    }

    static void validateArguments(Object value) {
        if (value == null) {
            throw new IllegalArgumentException("Instance should not be null");
        }
    }

}
