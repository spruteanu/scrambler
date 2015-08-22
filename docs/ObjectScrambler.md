## Generic object/array methods

```java

// declare a value instance that represents constant value
Assert.assertEquals(1, ObjectScrambler.constant(1).next().longValue());

// declare a value instance that will return randomly generated array
final Value<Long[]> longValues = ArrayScrambler.randomArray(1L, 10);
Assert.assertEquals(10, longValues.next().length);

// declare a value instance that will return randomly generated primitives array
final Value<long[]> longPrimitives = ArrayScrambler.randomArray(new long[10]);
Assert.assertEquals(10, longPrimitives.next().length);

// declare a value instance that will return an element from provided array randomly
Assert.assertTrue(new HashSet<Integer>(Arrays.asList(1, 2, 3, 4)).contains(
        ArrayScrambler.randomOf(new Integer[]{1, 2, 3, 4}).next()));

// declare a value instance that will generate an array of Long objects randomly in a specified range
final Value<Long[]> randomsInRange = ArrayScrambler.arrayOf(new Long[10], NumberScrambler.random(900L, 1000L));
Assert.assertEquals(10, randomsInRange.next().length);

// declare a value instance that will generate an array of short primitives randomly in a specified range
final Value<short[]> primitivesInRange = ArrayScrambler.arrayOf(new short[10], NumberScrambler.random((short) 900, (short) 1000));
Assert.assertEquals(10, primitivesInRange.next().length);

```

![Generic object classes](generic-object-value-class-dgm.png)

## Generate boolean methods

```java

// value instance that will return boolean randomly
Assert.assertNotNull(ObjectScrambler.random(true).next());

// value instance that will return randomly generated Boolean array
final Value<Boolean[]> booleanValues = ArrayScrambler.randomArray(true, 10);
Assert.assertEquals(10, booleanValues.next().length);

// value instance that will return randomly generated primitives boolean array
final Value<boolean[]> booleanPrimitives = ArrayScrambler.randomArray(new boolean[10]);
Assert.assertEquals(10, booleanPrimitives.next().length);

```
