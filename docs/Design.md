## Overview
*TBD*

## org.prismus.scrambler.Value interface

```java

/**
 * An interface used to generate an object.
 */
public interface Value<T> extends Serializable, Cloneable {

    /**
     * Generates an object.
     *
     * @return an instance of object
     */
    T next();

    /**
     * Gets current instance value
     *
     * @return current value instance
     */
    T get();

}

```

## Creating implementations of org.prismus.scrambler.Value 
*TBD*

## org.prismus.scrambler.ValuePredicate interface
*TBD*
