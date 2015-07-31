# Data Scrambler
Data Scrambler is a project that allows data generation in various forms. 
Project exposes an API to generate numbers, dates, strings, collections, maps, arrays in an incremental, random, 
or custom way if required. Also, it allows to generate data for interested Java classes like beans for example.

## DataScrambler Number generation methods
```java

        // generate incremental integer with default step (1)
        System.out.println(DataScrambler.increment(1).next());

        // generate incremental long with step (100)
        System.out.println(DataScrambler.increment(1L, 100L).next());

        // generate incremental double with step (12.5)
        System.out.println(DataScrambler.increment(1.0d, 12.5d).next());

        // generate incremental BigInteger with step (-1)
        System.out.println(DataScrambler.increment(BigInteger.valueOf(-1), BigInteger.valueOf(-1)).next());

        // generate incremental array with step (100) starting from 0
        Value<Integer[]> integerArray = DataScrambler.incrementArray(new Integer[10], 100, 10);
        System.out.println(Arrays.asList(integerArray.next()));

        // generate incremental array with step (100) starting from 1
        integerArray = DataScrambler.incrementArray(1, 100, 10);
        System.out.println(Arrays.asList(integerArray.next()));

        // generate incremental array with step (10.5) starting from 0
        Value<float[]> primitiveFloatArray = DataScrambler.incrementArray(new float[10], 10.5f, 10);
        System.out.println(Arrays.asList(primitiveFloatArray.next()));

        // generate random integer
        System.out.println(DataScrambler.random(100).next());

        // generate random long
        System.out.println(DataScrambler.random(1000L).next());

        // generate random short
        System.out.println(DataScrambler.random((short) 1000).next());

        // generate random double
        System.out.println(DataScrambler.random(300.0d).next());

        // generate random big integer
        System.out.println(DataScrambler.random(BigInteger.valueOf(-1)).next());

        // generate random integer in a range
        System.out.println(DataScrambler.random(70, 100).next());

        // generate random long in a range
        System.out.println(DataScrambler.random(900L, 1000L).next());

        // generate random short in a range
        System.out.println(DataScrambler.random((short) 980, (short) 1000).next());

        // generate random double in a range
        System.out.println(DataScrambler.random(300.0d, 500.0d).next());

        // generate random big integer in a range
        System.out.println(DataScrambler.random(BigInteger.valueOf(-1), BigInteger.valueOf(100)).next());

```