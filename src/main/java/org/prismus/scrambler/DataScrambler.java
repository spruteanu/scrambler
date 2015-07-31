package org.prismus.scrambler;

import org.prismus.scrambler.value.*;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.util.*;

/**
 * todo: add description
 *
 * @author Serge Pruteanu
 */
public class DataScrambler {

    //------------------------------------------------------------------------------------------------------------------
    // Parse definition methods
    //------------------------------------------------------------------------------------------------------------------
    public static ValueDefinition parseDefinition(String definition) throws IOException {
        return Holder.groovyValueDefinition.parseDefinition(definition);
    }

    public static ValueDefinition parseDefinition(String definition, Map<String, Object> contextMap) throws IOException {
        return Holder.groovyValueDefinition.parseDefinition(definition, contextMap);
    }

    static <T> InstanceValue<T> parseDefinitions(InstanceValue<T> instanceValue, Map<String, Object> contextMap, String... definitions) throws IOException {
        if (definitions != null) {
            for (String definition : definitions) {
                instanceValue.usingDefinitions(parseDefinition(definition, contextMap));
            }
        }
        return instanceValue;
    }

    public static <T> InstanceValue<T> instanceOf(Class<T> clazzType, String... definitions) throws IOException {
        return parseDefinitions(instanceOf(clazzType), null, definitions);
    }

    public static <T> InstanceValue<T> instanceOf(Class<T> clazzType, Map<String, Object> contextMap, String... definitions) throws IOException {
        return parseDefinitions(instanceOf(clazzType), contextMap, definitions);
    }

    @SuppressWarnings("unchecked")
    public static <T> InstanceValue<T> instanceOf(String clazzType, String... definitions) throws IOException {
        return (InstanceValue<T>) parseDefinitions(instanceOf(clazzType), null, definitions);
    }

    @SuppressWarnings("unchecked")
    public static <T> InstanceValue<T> instanceOf(String clazzType, Map<String, Object> contextMap, String... definitions) throws IOException {
        return (InstanceValue<T>) parseDefinitions(instanceOf(clazzType), contextMap, definitions);
    }

    public static ValueDefinition parseDefinition(File definition) throws IOException {
        return Holder.groovyValueDefinition.parseDefinition(definition);
    }

    public static ValueDefinition parseDefinition(File definition, Map<String, Object> contextMap) throws IOException {
        return Holder.groovyValueDefinition.parseDefinition(definition, contextMap);
    }

    static <T> InstanceValue<T> parseDefinitions(InstanceValue<T> instanceValue, Map<String, Object> contextMap, File... definitions) throws IOException {
        if (definitions != null) {
            for (File definition : definitions) {
                instanceValue.usingDefinitions(parseDefinition(definition, contextMap));
            }
        }
        return instanceValue;
    }

    public static <T> InstanceValue<T> instanceOf(Class<T> clazzType, File... definitions) throws IOException {
        return parseDefinitions(instanceOf(clazzType), null, definitions);
    }

    public static <T> InstanceValue<T> instanceOf(Class<T> clazzType, Map<String, Object> contextMap, File... definitions) throws IOException {
        return parseDefinitions(instanceOf(clazzType), contextMap, definitions);
    }

    @SuppressWarnings("unchecked")
    public static <T> InstanceValue<T> instanceOf(String clazzType, File... definitions) throws IOException {
        return (InstanceValue<T>) parseDefinitions(instanceOf(clazzType), null, definitions);
    }

    @SuppressWarnings("unchecked")
    public static <T> InstanceValue<T> instanceOf(String clazzType, Map<String, Object> contextMap, File... definitions) throws IOException {
        return (InstanceValue<T>) parseDefinitions(instanceOf(clazzType), contextMap, definitions);
    }

    public static ValueDefinition parseDefinition(InputStream inputStream) throws IOException {
        return Holder.groovyValueDefinition.parseDefinition(inputStream);
    }

    public static ValueDefinition parseDefinition(InputStream inputStream, Map<String, Object> contextMap) throws IOException {
        return Holder.groovyValueDefinition.parseDefinition(inputStream, contextMap);
    }

    static <T> InstanceValue<T> parseDefinitions(InstanceValue<T> instanceValue, Map<String, Object> contextMap, InputStream... inputStreams) throws IOException {
        if (inputStreams != null) {
            for (InputStream inputStream : inputStreams) {
                try {
                    instanceValue.usingDefinitions(parseDefinition(inputStream, contextMap));
                } finally {
                    try {
                        inputStream.close();
                    } catch (IOException ignore) { }
                }
            }
        }
        return instanceValue;
    }

    public static <T> InstanceValue<T> instanceOf(Class<T> clazzType, InputStream... inputStreams) throws IOException {
        return parseDefinitions(instanceOf(clazzType), null, inputStreams);
    }

    public static <T> InstanceValue<T> instanceOf(Class<T> clazzType, Map<String, Object> contextMap, InputStream... inputStreams) throws IOException {
        return parseDefinitions(instanceOf(clazzType), contextMap, inputStreams);
    }

    @SuppressWarnings("unchecked")
    public static <T> InstanceValue<T> instanceOf(String clazzType, InputStream... inputStreams) throws IOException {
        return (InstanceValue<T>) parseDefinitions(instanceOf(clazzType), null, inputStreams);
    }

    @SuppressWarnings("unchecked")
    public static <T> InstanceValue<T> instanceOf(String clazzType, Map<String, Object> contextMap, InputStream... inputStreams) throws IOException {
        return (InstanceValue<T>) parseDefinitions(instanceOf(clazzType), contextMap, inputStreams);
    }

    public static ValueDefinition parseDefinition(Reader reader, Map<String, Object> contextMap) throws IOException {
        return Holder.groovyValueDefinition.parseDefinition(reader, contextMap);
    }

    static <T> InstanceValue<T> parseDefinitions(InstanceValue<T> instanceValue, Map<String, Object> contextMap, Reader... readers) throws IOException {
        if (readers != null) {
            for (Reader reader : readers) {
                try {
                    instanceValue.usingDefinitions(parseDefinition(reader, contextMap));
                } finally {
                    try {
                        reader.close();
                    } catch (IOException ignore) { }
                }
            }
        }
        return instanceValue;
    }

    public static <T> InstanceValue<T> instanceOf(Class<T> clazzType, Reader... readers) throws IOException {
        return parseDefinitions(instanceOf(clazzType), null, readers);
    }

    public static <T> InstanceValue<T> instanceOf(Class<T> clazzType, Map<String, Object> contextMap, Reader... readers) throws IOException {
        return parseDefinitions(instanceOf(clazzType), contextMap, readers);
    }

    @SuppressWarnings("unchecked")
    public static <T> InstanceValue<T> instanceOf(String clazzType, Reader... readers) throws IOException {
        return (InstanceValue<T>) parseDefinitions(instanceOf(clazzType), null, readers);
    }

    @SuppressWarnings("unchecked")
    public static <T> InstanceValue<T> instanceOf(String clazzType, Map<String, Object> contextMap, Reader... readers) throws IOException {
        return (InstanceValue<T>) parseDefinitions(instanceOf(clazzType), contextMap, readers);
    }

    //------------------------------------------------------------------------------------------------------------------
    // Value definition parse methods
    //------------------------------------------------------------------------------------------------------------------
    @SuppressWarnings("unchecked")
    public static <T> Value<T> valueOf(Class<T> clazzType, String definitionResource) throws IOException {
        return Holder.groovyValueDefinition.parseValue(definitionResource);
    }

    @SuppressWarnings("unchecked")
    public static <T> Value<T> valueOf(String clazzType, String definitionResource) throws IOException {
        return Holder.groovyValueDefinition.parseValue(definitionResource);
    }

    @SuppressWarnings("unchecked")
    public static <T> Value<T> valueOf(Class<T> clazzType, File definition) throws IOException {
        return Holder.groovyValueDefinition.parseValue(definition);
    }

    @SuppressWarnings("unchecked")
    public static <T> Value<T> valueOf(String clazzType, File definition) throws IOException {
        return Holder.groovyValueDefinition.parseValue(definition);
    }

    @SuppressWarnings("unchecked")
    public static <T> Value<T> valueOf(Class<T> clazzType, InputStream inputStream) throws IOException {
        return Holder.groovyValueDefinition.parseValue(inputStream);
    }

    @SuppressWarnings("unchecked")
    public static <T> Value<T> valueOf(String clazzType, InputStream inputStream) throws IOException {
        return Holder.groovyValueDefinition.parseValue(inputStream);
    }

    @SuppressWarnings("unchecked")
    public static <T> Value<T> valueOf(Class<T> clazzType, Reader reader) throws IOException {
        return Holder.groovyValueDefinition.parseValue(reader);
    }

    @SuppressWarnings("unchecked")
    public static <T> Value<T> valueOf(Reader reader) throws IOException {
        return Holder.groovyValueDefinition.parseValue(reader);
    }


    //------------------------------------------------------------------------------------------------------------------
    // InstanceValue methods
    //------------------------------------------------------------------------------------------------------------------
    public static <T> InstanceValue<T> instanceOf(String type) {
        return instanceOf(type, (Map<Object, Object>) null);
    }

    public static <T> InstanceValue<T> instanceOf(Class<T> clazzType) {
        return instanceOf(clazzType, (Map<Object, Object>) null);
    }

    public static <T> InstanceValue<T> instanceOf(Class<T> clazzType, Map<Object, Object> fieldMap) {
        return new InstanceValue<T>(clazzType).usingDefinitions(fieldMap);
    }

    public static <T> InstanceValue<T> instanceOf(String type, Map<Object, Object> fieldMap) {
        return new InstanceValue<T>(type).usingDefinitions(fieldMap);
    }

    public static <T> InstanceValue<T> of(Class<T> self, AbstractDefinitionCallable defCl) {
        return new InstanceValue<T>(self).withPredicate(new TypePredicate(self)).withDefinitionClosure(defCl);
    }

    public static <T> InstanceValue<T> of(Class<T> self, Map<Object, Object> propertyValueMap) {
        return of(self, propertyValueMap, null);
    }

    public static <T> InstanceValue<T> of(Class<T> self, Map<Object, Object> propertyValueMap, AbstractDefinitionCallable defCl) {
        return new InstanceValue<T>(self).withPredicate(new TypePredicate(self)).withDefinitionClosure(defCl).usingDefinitions(propertyValueMap);
    }

    public static <T> InstanceValue<T> of(Class<T> self, String propertyName, AbstractDefinitionCallable defCl) {
        return new InstanceValue<T>(self).withPredicate(Util.createPropertyPredicate(propertyName)).withDefinitionClosure(defCl);
    }

    public static <T> InstanceValue<T> of(Class<T> self, Collection constructorArgs, AbstractDefinitionCallable defCl) {
        return new InstanceValue<T>(self).withPredicate(new TypePredicate(self)).withDefinitionClosure(defCl).withConstructorArguments(constructorArgs);
    }

    public static <T> InstanceValue<T> of(Class<T> self, String propertyName, Collection constructorArgs, AbstractDefinitionCallable defCl) {
        return new InstanceValue<T>(self).withPredicate(Util.createPropertyPredicate(propertyName)).withDefinitionClosure(defCl).withConstructorArguments(constructorArgs);
    }

    public static ReferenceValue reference(Class self) {
        return reference(self, null);
    }

    public static ReferenceValue reference(Class self, String propertyPredicate) {
        return new ReferenceValue(new TypePredicate(self), propertyPredicate != null ? Util.createPropertyPredicate(propertyPredicate) : null);
    }

    public static <T> Value<T> arrayOf(Class<T> self, Value val) {
        return of(self, val, null);
    }

    public static <T> Value<T> arrayOf(Class<T> self, Value val, Integer count) {
        return of(self, val, count);
    }

    public static <T> Value<T> arrayOf(Object self, Value value) {
        return arrayOf(self, value, null);
    }

    @SuppressWarnings("unchecked")
    public static <T> Value<T> arrayOf(Object self, Value value, Integer count) {
        Util.checkNullValue(self);
        final Class<?> selfClass = self.getClass();
        if (!selfClass.isArray()) {
            throw new IllegalArgumentException(String.format("An array instance must be provided; provided: %s", self));
        }
        final Class<?> componentType = selfClass.getComponentType();
        if (componentType.isPrimitive()) {
            return (Value) Util.createInstance(Types.primitivesArrayTypeMap.get(componentType),
                    new Object[]{self, count, value}, new Class[]{selfClass, Integer.class, Object.class}
            );
        } else {
            return new ArrayValue((T[]) self, count, value);
        }
    }


    //------------------------------------------------------------------------------------------------------------------
    // Map methods
    //------------------------------------------------------------------------------------------------------------------
    @SuppressWarnings("unchecked")
    public static <K> MapValue<K> of(Map<K, Value> keyValueMap) {
        return new MapValue<K>((Class<Map>) keyValueMap.getClass(), keyValueMap);
    }

    public static <K> MapValue<K> of(Map<K, Object> self, Map<K, Value> keyValueMap) {
        return new MapValue<K>(self, keyValueMap);
    }

    public static <K> MapValue<K> mapOf(Set<K> self, Map<ValuePredicate, Value> definitionMap) {
        final Map<K, Object> valueMap = new LinkedHashMap<K, Object>();
        final Map<K, Value> keyValueMap = new LinkedHashMap<K, Value>();
        for (Map.Entry<ValuePredicate, Value> entry : definitionMap.entrySet()) {
            for (K key : self) {
                final ValuePredicate predicate = entry.getKey();
                final Value value = entry.getValue();
                if (predicate.apply(key.toString(), value.get())) {
                    keyValueMap.put(key, value);
                    valueMap.put(key, value.get());
                    break;
                }
            }
        }
        return new MapValue<K>(valueMap, keyValueMap);
    }


    //------------------------------------------------------------------------------------------------------------------
    // String methods
    //------------------------------------------------------------------------------------------------------------------
    public static IncrementalString increment(String self) {
        return new IncrementalString(self);
    }

    public static IncrementalString increment(String self, String pattern) {
        return new IncrementalString(self, pattern);
    }

    public static IncrementalString increment(String self, Integer index) {
        return new IncrementalString(self, index);
    }

    public static IncrementalString increment(String self, String pattern, Integer index) {
        return new IncrementalString(self, pattern, index);
    }

    public static ArrayValue<String> incrementArray(String self, Integer count) {
        return new ArrayValue<String>(String.class, count, new IncrementalString(self));
    }

    public static ArrayValue<String> incrementArray(String self, String pattern, Integer count) {
        return new ArrayValue<String>(String.class, count, new IncrementalString(self, pattern));
    }

    public static ArrayValue<String> incrementArray(String value, Integer index, Integer count) {
        return new ArrayValue<String>(String.class, count, new IncrementalString(value, index));
    }

    public static ArrayValue<String> incrementArray(String value, String pattern, Integer index, Integer count) {
        return new ArrayValue<String>(String.class, count, new IncrementalString(value, pattern, index));
    }

    public static RandomString random(String value) {
        return new RandomString(value);
    }

    public static RandomString random(String value, Integer count) {
        return new RandomString(value, count);
    }

    public static ArrayValue<String> randomArray(String value, Integer arrayCount) {
        return new ArrayValue<String>(String.class, arrayCount, random(value));
    }

    public static ArrayValue<String> randomArray(String value, Integer count, Integer arrayCount) {
        return new ArrayValue<String>(String.class, count, random(value, arrayCount));
    }


    //------------------------------------------------------------------------------------------------------------------
    // Collection methods
    //------------------------------------------------------------------------------------------------------------------
    public static <V, T extends Collection<V>> CollectionValue<V, T> of(T collection, Value<V> value) {
        return of(collection, value, null);
    }

    public static <V, T extends Collection<V>> CollectionValue<V, T> of(T collection, Value<V> value, Integer count) {
        return new CollectionValue<V, T>(collection, value, count);
    }

    public static <T> Value<T> randomOf(List<T> values) {
        return new ListRandomElement<T>(values);
    }

    public static <T> Value<T> randomOf(Collection<T> collection) {
        return new ListRandomElement<T>(new ArrayList<T>(collection));
    }


    //------------------------------------------------------------------------------------------------------------------
    // Dates methods
    //------------------------------------------------------------------------------------------------------------------
    public static IncrementalDate increment(Date self) {
        return new IncrementalDate(self);
    }

    public static IncrementalDate increment(Date self, Integer step) {
        return new IncrementalDate(self, step);
    }

    public static IncrementalDate increment(Date self, Integer step, Integer calendarField) {
        return new IncrementalDate(self, calendarField, step);
    }

    public static ArrayValue<Date> incrementArray(Date self, Integer step, Integer count) {
        return new ArrayValue<Date>(Date.class, count, increment(self, step));
    }

    public static IncrementalDate increment(Date self, Map<Integer, Integer> calendarFieldStepMap) {
        return new IncrementalDate(self).incrementBy(calendarFieldStepMap);
    }

    public static ArrayValue<Date> incrementArray(Date self, Map<Integer, Integer> calendarFieldStepMap, Integer count) {
        return new ArrayValue<Date>(Date.class, count, increment(self, calendarFieldStepMap));
    }

    public static ArrayValue<Date> incrementArray(Date self, Integer step, Integer calendarField, Integer count) {
        return new ArrayValue<Date>(Date.class, count, increment(self, step, calendarField));
    }

    public static ArrayValue<Date> incrementArray(Date self, Integer count) {
        return new ArrayValue<Date>(Date.class, count, increment(self));
    }

    public static ArrayValue<Date> arrayOf(Value<Date> value, Integer count) {
        return new ArrayValue<Date>(Date.class, count, value);
    }

    public static RandomDate random(Date value) {
        return new RandomDate(value);
    }

    public static RandomDate random(Date self, Date minimum, Date maximum) {
        return new RandomDate(self, minimum, maximum);
    }

    public static ArrayValue<Date> randomArray(Date self, Date minimum, Date maximum, Integer count) {
        final RandomDate randomDate = new RandomDate(self, minimum, maximum);
        randomDate.next();
        return new ArrayValue<Date>(Date.class, count, randomDate);
    }


    //------------------------------------------------------------------------------------------------------------------
    // Number methods
    //------------------------------------------------------------------------------------------------------------------
    @SuppressWarnings({"unchecked"})
    public static <T extends Number> Value<T> increment(T self) {
        return increment((Class<T>) self.getClass(), self, null);
    }

    @SuppressWarnings({"unchecked"})
    public static <T extends Number> Value<T> increment(T self, T step) {
        return increment((Class<T>) self.getClass(), self, step);
    }

    @SuppressWarnings("unchecked")
    public static <T> Value<T> incrementArray(Object self, Object step, Integer count) {
        return incrementArray((Class<T>) self.getClass(), self, step, count);
    }

    @SuppressWarnings({"unchecked"})
    public static <T extends Number> Value<T> random(T value) {
        return random((Class<T>) value.getClass(), value);
    }

    @SuppressWarnings({"unchecked"})
    public static <T extends Number> Value<T> random(T minimum, T maximum) {
        return random((Class<T>) minimum.getClass(), minimum, maximum);
    }

    @SuppressWarnings({"unchecked"})
    public static <T extends Number> Value<T> random(T val, T minimum, T maximum) {
        final Value<T> value = random((Class<T>) val.getClass(), val);
        if (value instanceof AbstractRandomRange) {
            final AbstractRandomRange<Number> randomRangeValue = (AbstractRandomRange<Number>) value;
            randomRangeValue.between(minimum, maximum);
        } else {
            throw new UnsupportedOperationException(String.format(Types.NOT_SUPPORTED_RANGE_TYPE_MSG, val.getClass(), minimum, maximum));
        }
        return value;
    }


    //------------------------------------------------------------------------------------------------------------------
    // Object methods
    //------------------------------------------------------------------------------------------------------------------
    public static <T> Value<T> constant(T value) {
        return new Constant<T>(value);
    }

    public static <T> Value<T> randomOf(T[] array) {
        return new ArrayRandomElement<T>(array);
    }

    @SuppressWarnings("unchecked")
    public static <T> Value<T> randomArray(Object value) {
        return (Value<T>) randomArray(value.getClass(), value, null);
    }

    @SuppressWarnings("unchecked")
    public static <T> Value<T> randomArray(Object value, Integer count) {
        return (Value<T>) randomArray(value.getClass(), (Object) value, count);
    }


    //------------------------------------------------------------------------------------------------------------------
    // Class methods
    //------------------------------------------------------------------------------------------------------------------
    @SuppressWarnings({"unchecked"})
    public static <T> Value<T> incrementArray(Class<T> self, Object defaultValue, Object step, Integer count) {
        Util.checkPositiveCount(count);
        final Class<?> componentType = self.isArray() ? self.getComponentType() : self;
        final Value value;
        if (componentType.isPrimitive()) {
            value = (Value) Util.createInstance(Types.incrementTypeMap.get(self), new Object[]{
                            self.isInstance(defaultValue) ? defaultValue : null, count,
                            (Value) Util.createInstance(
                                    Types.incrementTypeMap.get(componentType),
                                    new Object[]{self.isInstance(defaultValue) ? null : defaultValue, step},
                                    new Class[]{Types.primitiveWrapperMap.get(componentType), Types.primitiveWrapperMap.get(componentType)}
                            )}, new Class[]{self, Integer.class, Object.class}
            );
        } else {
            value = new ArrayValue(componentType, count, (Value) Util.createInstance(
                    Types.incrementTypeMap.get(componentType),
                    new Object[]{componentType.isInstance(defaultValue) ? defaultValue : null, step}, new Class[]{componentType, componentType}
            ));
        }
        return value;
    }

    @SuppressWarnings({"unchecked"})
    public static <T extends Number> Value<T> increment(Class<T> self, T defaultValue, T step) {
        if (Types.incrementTypeMap.containsKey(self)) {
            final Value value;
            if (self.isArray()) {
                value = incrementArray(self, defaultValue, step, null);
            } else {
                value = (Value) Util.createInstance(Types.incrementTypeMap.get(self), new Object[]{defaultValue, step}, new Class[]{self, self});
            }
            return value;
        }
        throw new UnsupportedOperationException(String.format("The method is not supported for class type: %s, default value: %s", self, self));
    }

    public static <T> Value<T> random(Class<T> self) {
        return random(self, null);
    }

    @SuppressWarnings({"unchecked"})
    public static <T> Value<T> random(Class<T> self, T defaultValue) {
        if (Types.randomTypeMap.containsKey(self)) {
            if (self.isArray()) {
                return randomArray(self, defaultValue, null);
            } else {
                if (self.isPrimitive()) {
                    return (Value) Util.createInstance(Types.randomTypeMap.get(self), null, null);
                } else {
                    return (Value) Util.createInstance(Types.randomTypeMap.get(self), new Object[]{defaultValue}, new Class[]{self});
                }
            }
        }
        throw new UnsupportedOperationException(String.format("The method is not supported for class type: %s, default value: %s", self, defaultValue));
    }

    public static <T> Value<T> random(Class<T> self, T minimum, T maximum) {
        final Value<T> value = random(self, minimum);
        if (value instanceof AbstractRandomRange) {
            final AbstractRandomRange<T> randomRangeValue = (AbstractRandomRange<T>) value;
            randomRangeValue.between(minimum, maximum);
        } else {
            throw new UnsupportedOperationException(String.format(Types.NOT_SUPPORTED_RANGE_TYPE_MSG, self, minimum, maximum));
        }
        return value;
    }

    @SuppressWarnings({"unchecked"})
    public static <T> Value<T> randomArray(Class<T> self, Object defaultValue, Integer count) {
        final Class<?> componentType = self.isArray() ? self.getComponentType() : self;
        final Class<?> valueClassType = componentType.isPrimitive() ? Types.primitiveWrapperMap.get(componentType) : componentType;
        final Value valueType;
        if (defaultValue != null && !defaultValue.getClass().isArray()) {
            valueType = (Value) Util.createInstance(Types.randomTypeMap.get(valueClassType), new Object[]{defaultValue,}, new Class[]{valueClassType,});
        } else {
            valueType = (Value) Util.createInstance(Types.randomTypeMap.get(valueClassType), new Object[]{}, new Class[]{});
        }
        if (componentType.isPrimitive()) {
            final Class<? extends Value> arrayValueType = Types.primitivesArrayTypeMap.get(componentType);
            return (Value) Util.createInstance(arrayValueType, new Object[]{defaultValue, count, valueType}, new Class[]{self, Integer.class, Object.class});
        } else {
            return new ArrayValue(componentType, count, valueType);
        }
    }

    @SuppressWarnings("unchecked")
    public static <T> Value<T> of(Class clazzType, Value val, Integer count) {
        if (clazzType.isPrimitive()) {
            final Class<? extends Value> arrayValueType = Types.primitivesArrayTypeMap.get(clazzType);
            return (Value) Util.createInstance(arrayValueType, new Object[]{null, count, val}, new Class[]{Types.arrayTypeMap.get(clazzType), Integer.class, Object.class});
        } else {
            return new ArrayValue(clazzType, count, val);
        }
    }

    @SuppressWarnings("unchecked")
    public static <T extends Number> Value<T> randomArray(Class self, T minimum, T maximum, Integer count) {
        Util.checkPositiveCount(count);

        Object defaultValue = minimum != null ? minimum : maximum != null ? maximum : null;
        if (self == null) {
            self = defaultValue != null ? defaultValue.getClass() : null;
        }
        if (self == null) {
            throw new IllegalArgumentException(String.format("Either minimum: %s or maximum: %s should be not null", minimum, maximum));
        }
        boolean primitive = false;
        Class<?> componentType;
        if (self.isArray()) {
            componentType = self.getComponentType();
            if (componentType.isPrimitive()) {
                primitive = true;
                componentType = Types.primitiveWrapperMap.get(componentType);
            }
        } else {
            componentType = self;
        }

        Value instance = (Value) Util.createInstance(Types.randomTypeMap.get(componentType),
                new Object[]{minimum, maximum}, new Class[]{componentType, componentType}
        );
        final Value<T> value;
        if (primitive) {
            value = (Value<T>) Util.createInstance(Types.randomTypeMap.get(self),
                    new Object[]{self.isInstance(defaultValue) ? defaultValue : null, count, instance},
                    new Class[]{self, Integer.class, Object.class}
            );
        } else {
            value = new ArrayValue(self, count, instance);
        }
        return value;
    }

    @SuppressWarnings("unchecked")
    public static <K> MapValue<K> mapOf(Class<? extends Map> mapType, Map<K, Value> keyValueMap) {
        return new MapValue<K>((Class<Map>) mapType, keyValueMap);
    }

    public static <V, T extends Collection<V>> CollectionValue<V, T> collectionOf(Class<T> clazzType, Value<V> value) {
        return new CollectionValue<V, T>(clazzType, value, null);
    }

    //------------------------------------------------------------------------------------------------------------------
    // Boolean methods
    //------------------------------------------------------------------------------------------------------------------
    public static Value<Boolean> random(Boolean value) {
        return new RandomBoolean(value);
    }

    private static class Holder {
        private static GroovyValueDefinition groovyValueDefinition = new GroovyValueDefinition();
    }

}
