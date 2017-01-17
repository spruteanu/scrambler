## Generic object/array methods

```java

// declare a data instance that represents constant data
Assert.assertEquals(1, ObjectScrambler.constant(1).next().longValue());

// declare a data instance that will return randomly generated array
final Data<Long[]> longValues = ArrayScrambler.randomArray(1L, 10);
Assert.assertEquals(10, longValues.next().length);

// declare a data instance that will return randomly generated primitives array
final Data<long[]> longPrimitives = ArrayScrambler.randomArray(new long[10]);
Assert.assertEquals(10, longPrimitives.next().length);

// declare a data instance that will return an element from provided array randomly
Assert.assertTrue(new HashSet<Integer>(Arrays.asList(1, 2, 3, 4)).contains(
        ArrayScrambler.randomOf(new Integer[]{1, 2, 3, 4}).next()));

// declare a data instance that will generate an array of Long objects randomly in a specified range
final Data<Long[]> randomsInRange = ArrayScrambler.arrayOf(new Long[10], NumberScrambler.random(900L, 1000L));
Assert.assertEquals(10, randomsInRange.next().length);

// declare a data instance that will generate an array of short primitives randomly in a specified range
final Data<short[]> primitivesInRange = ArrayScrambler.arrayOf(new short[10], NumberScrambler.random((short) 900, (short) 1000));
Assert.assertEquals(10, primitivesInRange.next().length);

```

![Generic object classes](generic-object-data-class-dgm.png)

## Generate boolean methods

```java

// data instance that will return boolean randomly
Assert.assertNotNull(ObjectScrambler.random(true).next());

// data instance that will return randomly generated Boolean array
final Data<Boolean[]> booleanValues = ArrayScrambler.randomArray(true, 10);
Assert.assertEquals(10, booleanValues.next().length);

// data instance that will return randomly generated primitives boolean array
final Data<boolean[]> booleanPrimitives = ArrayScrambler.randomArray(new boolean[10]);
Assert.assertEquals(10, booleanPrimitives.next().length);

```
