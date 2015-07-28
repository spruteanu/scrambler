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
    public static <T> InstanceValue<T> instanceOf(Class<T> clazzType, String definition) throws IOException {
        return instanceOf(clazzType).usingDefinitions(Holder.groovyValueDefinition.parseDefinition(definition));
    }

    @SuppressWarnings("unchecked")
    public static <T> InstanceValue<T> instanceOf(String clazzType, String definition) throws IOException {
        return (InstanceValue<T>) instanceOf(clazzType).usingDefinitions(Holder.groovyValueDefinition.parseDefinition(definition));
    }

    public static <T> InstanceValue<T> instanceOf(Class<T> clazzType, File definition) throws IOException {
        return instanceOf(clazzType).usingDefinitions(Holder.groovyValueDefinition.parseDefinition(definition));
    }

    @SuppressWarnings("unchecked")
    public static <T> InstanceValue<T> instanceOf(String clazzType, File definition) throws IOException {
        return (InstanceValue<T>) instanceOf(clazzType).usingDefinitions(Holder.groovyValueDefinition.parseDefinition(definition));
    }

    public static <T> InstanceValue<T> instanceOf(Class<T> clazzType, InputStream inputStream) throws IOException {
        return instanceOf(clazzType).usingDefinitions(Holder.groovyValueDefinition.parseDefinition(inputStream));
    }

    @SuppressWarnings("unchecked")
    public static <T> InstanceValue<T> instanceOf(String clazzType, InputStream inputStream) throws IOException {
        return (InstanceValue<T>) instanceOf(clazzType).usingDefinitions(Holder.groovyValueDefinition.parseDefinition(inputStream));
    }

    public static <T> InstanceValue<T> instanceOf(Class<T> clazzType, Reader reader) throws IOException {
        return instanceOf(clazzType).usingDefinitions(Holder.groovyValueDefinition.parseDefinition(reader));
    }

    @SuppressWarnings("unchecked")
    public static <T> InstanceValue<T> instanceOf(String clazzType, Reader reader) throws IOException {
        return (InstanceValue<T>) instanceOf(clazzType).usingDefinitions(Holder.groovyValueDefinition.parseDefinition(reader));
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
        return instanceOf(type, (Map<Object, Object>)null);
    }

    public static <T> InstanceValue<T> instanceOf(Class<T> clazzType) {
        return instanceOf(clazzType, (Map<Object, Object>)null);
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


    //------------------------------------------------------------------------------------------------------------------
    // Map methods
    //------------------------------------------------------------------------------------------------------------------
    public static <K> MapValue<K> of(Map<K, Object> self, Map<K, Value> keyValueMap) {
        return new MapValue<K>(self, keyValueMap);
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
        return new IncrementalDate(self, step, calendarField);
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
    public static <T extends Number> Value incrementArray(T self, T step, Integer count) {
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


    //------------------------------------------------------------------------------------------------------------------
    // Class methods
    //------------------------------------------------------------------------------------------------------------------
    @SuppressWarnings({"unchecked"})
    public static <T> Value<T> incrementArray(Class<T> self, T defaultValue, Object step, Integer count) {
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
            value = new ArrayValue(self, count, (Value) Util.createInstance(Types.incrementTypeMap.get(componentType),
                    new Object[]{defaultValue, step}, new Class[]{componentType, componentType}));
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
        throw new UnsupportedOperationException(String.format("The of method is not supported for class type: %s, default value: %s", self, self));
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
        throw new UnsupportedOperationException(String.format("The of method is not supported for class type: %s, default value: %s", self, defaultValue));
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
    public static <T> Value<T> randomArray(Class<T> self, T defaultValue, Integer count) {
        final Class<?> componentType = self.getComponentType();
        Value valueType;
        if (defaultValue != null) {
            valueType = (Value) Util.createInstance(Types.randomTypeMap.get(componentType), new Object[]{defaultValue,}, new Class[]{componentType,});
        } else {
            valueType = (Value) Util.createInstance(Types.randomTypeMap.get(componentType), new Object[]{}, new Class[]{});
        }
        final Value<T> value;
        if (componentType.isPrimitive()) {
            value = (Value) Util.createInstance(Types.randomTypeMap.get(self), new Object[]{defaultValue, count, valueType}, new Class[]{self, Integer.class, Object.class});
        } else {
            value = new ArrayValue(self, valueType);
        }
        return value;
    }

    @SuppressWarnings("unchecked")
    public static <T> Value<T> of(Class clazzType, Value val, Integer count) {
        if (clazzType.isPrimitive()) {
            final Class<? extends Value> arrayValueType = Types.primitivesTypeMap.get(clazzType);
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
            value = (Value<T>) Util.createInstance( Types.randomTypeMap.get(self),
                    new Object[]{self.isInstance(defaultValue) ? defaultValue : null, count, instance},
                    new Class[]{self, Integer.class, Object.class}
            );
        } else {
            value = new ArrayValue(self, count, instance);
        }
        return value;
    }

    public static <K> MapValue<K> mapOf(Class<Map<K, Object>> mapType, Map<K, Value> keyValueMap) {
        return new MapValue<K>(mapType, keyValueMap);
    }

    public static <V, T extends Collection<V>> CollectionValue<V, T> collectionOf(Class<V> clazzType, Value<V> value) {
        return new CollectionValue<V, T>(clazzType, value, null);
    }

    //------------------------------------------------------------------------------------------------------------------
    // Boolean methods
    //------------------------------------------------------------------------------------------------------------------
    public static Value<Boolean> random(Boolean value) {
        return new RandomBoolean(value);
    }

    public static Value randomArray(Boolean value) {
        return randomArray(value, null, null);
    }

    public static Value randomArray(Boolean value, Integer count) {
        return randomArray(value, count, null);
    }

    @SuppressWarnings("unchecked")
    public static Value randomArray(Boolean value, Integer count, Class clazzType) {
        Util.checkPositiveCount(count);

        if (clazzType == null) {
            clazzType = value != null ? value.getClass() : null;
        }
        if (clazzType == null) {
            throw new IllegalArgumentException(String.format("Either classType: %s or value: %s should be not null", null, value));
        }
        boolean primitive = false;
        Class<?> componentType;
        if (clazzType.isArray()) {
            componentType = clazzType.getComponentType();
            if (componentType.isPrimitive()) {
                primitive = true;
                componentType = Types.primitiveWrapperMap.get(componentType);
            }
        } else {
            componentType = clazzType;
        }

        Value instance = (Value) Util.createInstance(Types.randomTypeMap.get(componentType), new Object[]{value}, new Class[]{componentType});
        final Value valueArray;
        if (primitive) {
            valueArray = (Value) Util.createInstance(Types.randomTypeMap.get(clazzType),
                    new Object[]{clazzType.isInstance(value) ? value : null, count, instance},
                    new Class[]{clazzType, Integer.class, Object.class}
            );
        } else {
            valueArray = new ArrayValue(clazzType, count, instance);
        }
        return valueArray;
    }
    
    private static class Holder {
        private static GroovyValueDefinition groovyValueDefinition = new GroovyValueDefinition();
    }
    
}
