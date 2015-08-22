## org.prismus.scrambler.Value interface
Value interface defines data generation capabilities. 

![Value interface](value-class-dgm.png)

```java
/**
 * An interface used to generate data.
 */
public interface Value<T> extends Serializable, Cloneable {
    /**
     * Generates value.
     *
     * @return an instance of object
     */
    T next();

    /**
     * Gets current generated value
     *
     * @return current value
     */
    T get();
}
```

It consists of 2 methods: 

1. ``T next()``
  Generates a value. Each call will produce a new value.
2. ``T get()``
  Method is used to get the value from prior execution of ``next()`` method.
  
## Custom org.prismus.scrambler.Value implementation 
It is recommended to create custom Value implementations by extending **org.prismus.scrambler.value.Constant** class.
This class is a template one that allows to get value and performs value setting for newly generated object.
Your implementation has only to extend method **org.prismus.scrambler.value.Constant#doNext()** for object generation logic.

```java
public class Constant<T> implements Value<T> {
...
    public T get() {
        return value;
    }

    public T next() {
        final T value = doNext();
        setValue(value);
        return value;
    }

    protected T doNext() {
        return value;
    }
...
}
```

**Example**

```java 
public class RandomUuid extends Constant<String> {

    @Override
    protected String doNext() {
        return UUID.randomUUID().toString();
    }

}
```

## java.lang.Number generation facade and classes
![Number generation classes](number-value-class-dgm.png)

## java.util.Date generation facade and classes
![Date generation classes](date-value-class-dgm.png)

## java.lang.String generation facade and classes
![String generation classes](string-value-class-dgm.png)

## Generic object facade and classes
![Generic object classes](generic-object-value-class-dgm.png)

## Arrays generation facade and classes
![Array generation classes](array-value-class-dgm.png)

## java.util.Collection generation facade and classes
![Collection generation classes](collection-value-class-dgm.png)

## java.util.Map generation facade and classes
![Map generation classes](map-value-class-dgm.png)

## org.prismus.scrambler.ValuePredicate interface
![Value Predicate interface](value-predicate-dgm.png)
![Value Predicate interface](value-predicate-class-dgm.png)
**TBD**

## Java instances facade and classes
![Instance value classes](instance-value-class-dgm.png)

### Instance type definition' detection convention

### Default definitions

### Reference Value
**TBD**

## DataScrambler DSL

### Overview
![DSL classes](groovy-definition-class-dgm.png)

### Groovy compiler properties

## Data Scrambler extension

### Testing extension

### Definitions dictionary extensions

## Best Practices
**TBD**