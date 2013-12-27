package org.prismus.scrambler.property;

import org.apache.commons.beanutils.BeanUtilsBean;
import org.apache.commons.beanutils.ConstructorUtils;
import org.apache.commons.beanutils.ConvertUtilsBean;
import org.apache.commons.beanutils.MethodUtils;
import org.apache.commons.beanutils.converters.DateConverter;
import org.apache.commons.lang.StringUtils;

import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

/**
 * @author Serge Pruteanu
 */
public abstract class Util {
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
            patternObjectMap.put(Pattern.compile(lookupWildcards(entry.getKey()), Pattern.CASE_INSENSITIVE), entry.getValue());
        }
        return patternObjectMap;
    }

    static String lookupWildcards(String regExKey) {
//        final StringBuilder stringBuilder = new StringBuilder();
        regExKey = StringUtils.replace(regExKey, "?", "\\w");
        regExKey = StringUtils.replace(regExKey, "*", "\\w*");
        return regExKey;
    }

}
