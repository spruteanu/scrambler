```java

// generate incremental integer with default step (1)
System.out.println(NumberScrambler.increment(1).next());

// generate incremental long with step (100)
System.out.println(NumberScrambler.increment(1L, 100L).next());

// generate incremental double with step (12.5)
System.out.println(NumberScrambler.increment(1.0d, 12.5d).next());

// generate incremental BigInteger with step (-1)
System.out.println(NumberScrambler.increment(BigInteger.valueOf(-1), BigInteger.valueOf(-1)).next());

// generate incremental array with step (100) starting from 0
Data<Integer[]> integerArray = ArrayScrambler.incrementArray(new Integer[10], 100, 10);
System.out.println(Arrays.asList(integerArray.next()));

// generate incremental array with step (100) starting from 1
integerArray = ArrayScrambler.incrementArray(1, 100, 10);
System.out.println(Arrays.asList(integerArray.next()));

// generate incremental array with step (10.5) starting from 0
Data<float[]> primitiveFloatArray = ArrayScrambler.incrementArray(new float[10], 10.5f, 10);
System.out.println(Arrays.asList(primitiveFloatArray.next()));

// generate random integer
System.out.println(NumberScrambler.random(100).next());

// generate random long
System.out.println(NumberScrambler.random(1000L).next());

// generate random short
System.out.println(NumberScrambler.random((short) 1000).next());

// generate random double
System.out.println(NumberScrambler.random(300.0d).next());

// generate random big integer
System.out.println(NumberScrambler.random(BigInteger.valueOf(-1)).next());

// generate random integer in a range
System.out.println(NumberScrambler.random(70, 100).next());

// generate random long in a range
System.out.println(NumberScrambler.random(900L, 1000L).next());

// generate random short in a range
System.out.println(NumberScrambler.random((short) 980, (short) 1000).next());

// generate random double in a range
System.out.println(NumberScrambler.random(300.0d, 500.0d).next());

// generate random big integer in a range
System.out.println(NumberScrambler.random(BigInteger.valueOf(-1), BigInteger.valueOf(100)).next());

```

![Number generation classes](number-data-class-dgm.png)
