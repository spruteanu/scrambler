```java

        // Generate an incremental string based on provided String, pattern and index
        System.out.println(StringScrambler.increment("test").next());
        System.out.println(StringScrambler.increment("test", "%s Nr%d").next());
        System.out.println(StringScrambler.increment("test", 100).next());
        System.out.println(StringScrambler.increment("test", "%s Nr%d", 100).next());

        // Generate an incremental string array based on provided String, pattern and index
        System.out.println(Arrays.asList(StringScrambler.incrementArray("test", "%s Nr%d", 100, 10).next()));

        // Generate an random string based on template String, count length
        System.out.println(StringScrambler.random("My Random String 123").next());
        System.out.println(StringScrambler.random("My Random String 123", 35).next());

        // Generate an random string array based on template String, count length
        System.out.println(Arrays.asList(StringScrambler.randomArray("My Random String 123", 10).next()));
        System.out.println(Arrays.asList(StringScrambler.randomArray("My Random String 123", 35, 10).next()));

```
