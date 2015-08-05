```java

        // generate incremented date with default step and default calendar field (DAY)
        System.out.println(DateScrambler.increment(new Date()).next());
        // generate incremented date with provided step and default calendar field (DAY)
        System.out.println(DateScrambler.increment(new Date(), -1).next());
        // generate incremented date with provided step and calendar field (Calendar.MONTH)
        System.out.println(DateScrambler.increment(new Date(), -1, Calendar.MONTH).next());
        // generate incremented date by several calendar fields
        System.out.println(DateScrambler.increment(new Date()).days(-1).hours(2).minutes(15).next());

        // generate an array of dates
        System.out.println(Arrays.asList(DateScrambler.incrementArray(new Date(), 10).next()));
        // generate an array of dates by several fields
        System.out.println(Arrays.asList(DateScrambler.incrementArray(new Date(), new LinkedHashMap<Integer, Integer>() {{
            put(Calendar.DATE, 2);
            put(Calendar.MINUTE, -20);
        }}, 5).next()));

        // generate an array of dates
        System.out.println(Arrays.asList(DateScrambler.arrayOf(DateScrambler.increment(new Date()).days(-1).hours(2).minutes(15), 5).next()));

        // generate random date in a day period
        System.out.println(DateScrambler.random(new Date()).next());
        System.out.println(DateScrambler.random(new Date(),
                DateScrambler.increment(new Date(), -1, Calendar.MONTH).next(),
                DateScrambler.increment(new Date(), 1, Calendar.MONTH).next()
        ).next());

        System.out.println(Arrays.asList(DateScrambler.randomArray(new Date(),
                DateScrambler.increment(new Date(), -1, Calendar.MONTH).next(),
                DateScrambler.increment(new Date(), 1, Calendar.MONTH).next(),
                5).next()));

```
