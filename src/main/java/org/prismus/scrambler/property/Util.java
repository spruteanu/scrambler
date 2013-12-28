package org.prismus.scrambler.property;

import org.apache.commons.beanutils.BeanUtilsBean;
import org.apache.commons.beanutils.ConstructorUtils;
import org.apache.commons.beanutils.ConvertUtilsBean;
import org.apache.commons.beanutils.MethodUtils;
import org.apache.commons.beanutils.converters.DateConverter;

import java.util.*;
import java.util.regex.Pattern;

/**
 * @author Serge Pruteanu
 */
public abstract class Util {

    private static final Set<Character> PREFIXED_CHAR_SET = new HashSet<Character>(Arrays.asList('+', '(', ')', '^', '$', '.', '{', '}', '[', ']', '|', '\\'));

    @SuppressWarnings({"unchecked"})
    public static Object createInstance(Class clazzType,
                                        Object[] arguments,
                                        Class[] classes) {
        try {
            return ConstructorUtils.invokeConstructor(clazzType, arguments, classes);
        } catch (Exception e) {
            throw new RuntimeException(String.format("Failed to create instance of type: %s, arguments: %s, types: %s", clazzType, Arrays.asList(arguments), Arrays.asList(classes)), e);
        }
    }

    public static void invokeMethod(Object targetObject,
                                    String methodName,
                                    Object[] objects,
                                    Class[] classes) {
        try {
            MethodUtils.invokeMethod(targetObject, methodName, objects, classes);
        } catch (Exception e) {
            throw new RuntimeException(String.format("Failed to invoke %s method, arguments: %s", methodName, Arrays.asList(objects)), e);
        }
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
        final int length = wildcardPattern.length();
        for (int i = 0; i < length; ++i) {
            final char ch = wildcardPattern.charAt(i);
            if (ch == '*') {
                builder.append(".*");
            } else if (ch == '?') {
                builder.append(".");
            } else if (PREFIXED_CHAR_SET.contains(ch)) {
                builder.append('\\').append(ch);
            } else {
                builder.append(ch);
            }
        }
        builder.append('$');
        return builder.toString();
    }

}
