```java

// A list of incremented integer with step 1
System.out.println(CollectionScrambler.of(new ArrayList<Integer>(), NumberScrambler.increment(1)).next());
// A list of random double in a range 1.0-400.0
System.out.println(CollectionScrambler.of(new ArrayList<Double>(), NumberScrambler.random(1.0d, 400.0d)).next());

// A random element from provided collection
System.out.printf("%s random element: %s%n", new HashSet<String>(Arrays.asList("aa", "bb", "cc")), CollectionScrambler.randomOf(new HashSet<String>(Arrays.asList("aa", "bb", "cc"))).next());
// A random element from provided collection
System.out.printf("%s random element: %s%n", new HashSet<Integer>(Arrays.asList(1, 2, 3, 4, 5)), CollectionScrambler.randomOf(Arrays.asList(1, 2, 3, 4, 5)).next());

```

![Collection generation classes](collection-data-class-dgm.png)
