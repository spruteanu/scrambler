package org.prismus.scrambler.builder;

import org.prismus.scrambler.Property;
import org.prismus.scrambler.property.*;
import org.prismus.scrambler.property.Random;
import org.apache.commons.beanutils.BeanUtilsBean;

import java.util.*;
import java.util.regex.Pattern;

/**
 * todo: add description
 *
 * @author Serge Pruteanu
 */
public class Instance<T> extends Generic<T> {
    private static final String FAILED_SET_PROPERTIES_MSG = "Failed to set instance: %s with properties: %s";

    // todo sergep: merge properties with regular expression properties map
    // todo add synchronized property decorator
    // todo add introspection property generation on class/instance/method
    // todo add AOP/proxy/byte code manipulation property generation on class/instance/method
    // todo add new instance creation property value, with properties introspection/matching on constructor arguments
    // todo investigate/see if Collection interface implementation will ease spring configuration, if yes, create an Container property class which implements collection/iterator? interface
    // todo add class/POJO introspection
    // todo add DB table introspection
    // todo create a facade class which will integrate all builder/org.prismus.scrambler.Property functionalities
    // todo change the facade org.prismus.scrambler.Property (org.prismus.scrambler.value.Incremental, org.prismus.scrambler.value.Random) to do arguments conversion, that will ease instantiation/creation using file (xml)
    // todo externalize messages
    // todo review/get rid of (where possible) external library dependencies
    // todo add tests
    // todo review usability all time :)
    public static final Map<String, Object> PERSISTENCE_PROPERTIES_MATCH_MAP = defaultPersistenceMatchMap();

    protected Map<String, Object> properties;
    protected BeanUtilsBean beanUtilsBean;

    public Instance() {
        this("instance", null);
    }

    public Instance(String name) {
        this(name, null);
    }

    public Instance(T instance) {
        this("instance", instance);
    }

    public Instance(String name, T instance) {
        super(name, instance);
        properties = new LinkedHashMap<String, Object>();
        beanUtilsBean = Util.createBeanUtilsBean();
    }

    public void setProperties(Map<String, Object> properties) {
        this.properties = properties;
    }

    public void setBeanUtilsBean(BeanUtilsBean beanUtilsBean) {
        this.beanUtilsBean = beanUtilsBean;
    }

    void processPropertyMap(Object instance) {
        final Map<String, Object> resultMap = new HashMap<String, Object>(properties.size());
        for (final Map.Entry<String, Object> entry : properties.entrySet()) {
            Object value = entry.getValue();
            if (value instanceof Property) {
                value = ((Property) value).value();
            }
            resultMap.put(entry.getKey(), value);
        }
        populate(instance, resultMap);
    }

    void populate(Object instance, Map<String, Object> properties) {
        try {
            beanUtilsBean.populate(instance, properties);
        } catch (Exception e) {
            throw new RuntimeException(String.format(FAILED_SET_PROPERTIES_MSG, instance, properties));
        }
    }

    @SuppressWarnings({"unchecked"})
    T checkCreateInstance() {
        T result = this.value;
        if (result instanceof String) {
            try {
                result = (T) Class.forName(((String) result));
            } catch (ClassNotFoundException e) {
                throw new IllegalStateException(String.format("Not found class for specified value: %s", result), e);
            }
        }
        if (result instanceof Class) {
            result = (T) Util.createInstance((Class) result, null, null);
        }
        return result;
    }

    static Map<String, Object> defaultPersistenceMatchMap() {
        final Map<String, Object> resultMap = new LinkedHashMap<String, Object>();
        resultMap.put("\\w*sid", Incremental.class);
        resultMap.put("\\w*id", Incremental.class);
        resultMap.put("\\w*modified\\w*date\\*", CurrentTime.class);
        resultMap.put("\\w*created\\w*date\\*", CurrentTime.class);
        return resultMap;
    }

    public T value() {
        final T instance = checkCreateInstance();
        processPropertyMap(instance);
        return instance;
    }

    public Instance matchProperties(Map<String, Object> properties) {
        return matchProperties(properties, null);
    }

    public Instance matchProperties(Map<String, Object> properties, Map<String, Object> regExPropertyMap) {
        final Map<Pattern, Object> patternObjectMap = getPatternObjectMap(regExPropertyMap);
        for (final Map.Entry<String, Object> entry : properties.entrySet()) {
            final String propertyName = entry.getKey();
            final Object value = entry.getValue();
            final Property instance = match(propertyName, value, patternObjectMap);
            if (instance != null) {
                addProperty(instance);
            }
        }
        return this;
    }

    public Instance persistenceMatch(Map<String, Object> properties) {
        return matchProperties(properties, PERSISTENCE_PROPERTIES_MATCH_MAP);
    }

    Property match(String name, Object value, Map<Pattern, Object> regExObjectMap) {
        Property instance = null;
        if (regExObjectMap != null) {
            for (final Map.Entry<Pattern, Object> entry : regExObjectMap.entrySet()) {
                if (entry.getKey().matcher(name).matches()) {
                    instance = matchProperty(name, entry.getValue(), value);
                    break;
                }
            }
        }
        if (instance == null) {
            instance = match(name, value, null);
        }
        return instance;
    }

    Map<Pattern, Object> getPatternObjectMap(Map<String, Object> regExObjectMap) {
        final Map<Pattern, Object> patternObjectMap = new HashMap<Pattern, Object>();
        for (final Map.Entry<String, Object> entry : regExObjectMap.entrySet()) {
            patternObjectMap.put(Pattern.compile(entry.getKey(), Pattern.CASE_INSENSITIVE), entry.getValue());
        }
        return patternObjectMap;
    }

    @SuppressWarnings({"unchecked"})
    Property matchProperty(String name, Object value, Object defaultValue) {
        Property instance = null;
        if (Property.class.isInstance(value)) {
            instance = (Property) value;
            if (instance instanceof Generic) {
                ((Generic) instance).setValue(defaultValue);
            }
        } else if (value instanceof Class) {
            final Class valueClass = (Class) value;
            instance = matchProperty(name, valueClass, defaultValue);
        } else {
            if (value != null) {
                instance = Random.of(name, value);
            }
        }
        return instance;
    }

    @SuppressWarnings({"unchecked"})
    Property matchProperty(String name, Class valueClass, Object defaultValue) {
        Property instance = null;
        if (Property.class.isAssignableFrom(valueClass)) {
            instance = (Property) Util.createInstance(
                    valueClass, new Object[]{name}, new Class[]{String.class}
            );
            if (instance instanceof Generic) {
                ((Generic) instance).setValue(defaultValue);
            }
        } else if (Collection.class.isAssignableFrom(valueClass)) {
            if (Collection.class.isInstance(defaultValue)) {
                instance = Random.of(name, (Collection) defaultValue);
            } else {
                if (defaultValue != null) {
                    instance = new PropertyCollection(name,
                            (List) Util.createInstance(valueClass, null, null),
                            Random.of(name, defaultValue)
                    );
                }
            }
        } else {
            if (defaultValue == null || valueClass.isInstance(defaultValue)) {
                instance = Random.of(name, valueClass, defaultValue);
            }
        }
        return instance;
    }

    public Instance addProperty(Property property) {
        properties.put(property.getName(), property);
        return this;
    }

    public Instance addProperties(Collection<Property> propertyCollection) {
        for (final Property property : propertyCollection) {
            addProperty(property);
        }
        return this;
    }

    public Instance value(String propertyName, Object value) {
        return addProperty(new Generic<Object>(propertyName, value));
    }

    public Instance random(String propertyName, Class classType) {
        return random(propertyName, classType, null);
    }

    @SuppressWarnings({"unchecked"})
    public <V> Instance random(String propertyName,
                               Class<V> elementClassType,
                               List<V> collection,
                               int count) {
        return addProperty(new PropertyCollection(propertyName, collection, count, Random.of(propertyName, elementClassType, null)));
    }

    @SuppressWarnings({"unchecked"})
    public Instance random(String propertyName, Class classType, Object defaultValue) {
        return addProperty(Random.of(propertyName, classType, defaultValue));
    }

    public Instance random(Map<String, Class> propertyClassTypeMap) {
        for (final Map.Entry<String, Class> entry : propertyClassTypeMap.entrySet()) {
            random(entry.getKey(), entry.getValue());
        }
        return this;
    }

    public Instance incremental(Map<String, Class> propertyClassTypeMap) {
        for (final Map.Entry<String, Class> entry : propertyClassTypeMap.entrySet()) {
            incremental(entry.getKey(), entry.getValue());
        }
        return this;
    }

    @SuppressWarnings({"unchecked"})
    public Instance incremental(String propertyName, Class clazzType) {
        return incremental(propertyName, clazzType, null);
    }

    @SuppressWarnings({"unchecked"})
    public Instance incremental(String propertyName, Class clazzType, Number step) {
        return incremental(propertyName, clazzType, null, step);
    }

    @SuppressWarnings({"unchecked"})
    public Instance incremental(String propertyName, Class clazzType, Object defaultValue, Number step) {
        return addProperty(Incremental.of(propertyName, clazzType, defaultValue, step));
    }

    BeanUtilsBean getBeanUtilsBean() {
        return beanUtilsBean;
    }

    Map<String, Object> getProperties() {
        return properties;
    }
}
