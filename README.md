# Data Scrambler
Data Scrambler is a project that allows data generation in various forms. 
Project exposes an API to generate numbers, dates, strings, collections, maps, arrays in an incremental, random, 
or custom generation logic if required. Also, it allows to generate data for Java classes like beans for example.

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

## org.prismus.scrambler.DataScrambler facade class
All generation abilities are exposed in `org.prismus.scrambler.DataScrambler` facade class.
Bellow are given examples of facade capabilities

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

## Generic object/array methods

```java

        // declare a value instance that represents constant value
        Assert.assertEquals(1, DataScrambler.constant(1).next().longValue());

        // declare a value instance that will return randomly generated array
        final Value<Long[]> longValues = DataScrambler.randomArray(1L, 10);
        Assert.assertEquals(10, longValues.next().length);

        // declare a value instance that will return randomly generated primitives array
        final Value<long[]> longPrimitives = DataScrambler.randomArray(new long[10]);
        Assert.assertEquals(10, longPrimitives.next().length);

        // declare a value instance that will return an element from provided array randomly
        Assert.assertTrue(new HashSet<Integer>(Arrays.asList(1, 2, 3, 4)).contains(
                DataScrambler.randomOf(new Integer[]{1, 2, 3, 4}).next()));

        // declare a value instance that will generate an array of Long objects randomly in a specified range
        final Value<Long[]> randomsInRange = DataScrambler.arrayOf(new Long[10], DataScrambler.random(900L, 1000L));
        Assert.assertEquals(10, randomsInRange.next().length);

        // declare a value instance that will generate an array of short primitives randomly in a specified range
        final Value<short[]> primitivesInRange = DataScrambler.arrayOf(new short[10], DataScrambler.random((short) 900, (short) 1000));
        Assert.assertEquals(10, primitivesInRange.next().length);

```

## Generate boolean methods

```java

        // value instance that will return boolean randomly
        Assert.assertNotNull(DataScrambler.random(true).next());

        // value instance that will return randomly generated Boolean array
        final Value<Boolean[]> booleanValues = DataScrambler.randomArray(true, 10);
        Assert.assertEquals(10, booleanValues.next().length);

        // value instance that will return randomly generated primitives boolean array
        final Value<boolean[]> booleanPrimitives = DataScrambler.randomArray(new boolean[10]);
        Assert.assertEquals(10, booleanPrimitives.next().length);

```

## Generate java.util.Date methods

```java

        // generate incremented date with default step and default calendar field (DAY)
        System.out.println(DataScrambler.increment(new Date()).next());
        // generate incremented date with provided step and default calendar field (DAY)
        System.out.println(DataScrambler.increment(new Date(), -1).next());
        // generate incremented date with provided step and calendar field (Calendar.MONTH)
        System.out.println(DataScrambler.increment(new Date(), -1, Calendar.MONTH).next());
        // generate incremented date by several calendar fields
        System.out.println(DataScrambler.increment(new Date()).days(-1).hours(2).minutes(15).next());

        // generate an array of dates
        System.out.println(Arrays.asList(DataScrambler.incrementArray(new Date(), 10).next()));
        // generate an array of dates by several fields
        System.out.println(Arrays.asList(DataScrambler.incrementArray(new Date(), new LinkedHashMap<Integer, Integer>() {{
            put(Calendar.DATE, 2);
            put(Calendar.MINUTE, -20);
        }}, 5).next()));

        // generate an array of dates
        System.out.println(Arrays.asList(DataScrambler.arrayOf(DataScrambler.increment(new Date()).days(-1).hours(2).minutes(15), 5).next()));

        // generate random date in a day period
        System.out.println(DataScrambler.random(new Date()).next());
        System.out.println(DataScrambler.random(new Date(),
                DataScrambler.increment(new Date(), -1, Calendar.MONTH).next(),
                DataScrambler.increment(new Date(), 1, Calendar.MONTH).next()
        ).next());

        System.out.println(Arrays.asList(DataScrambler.randomArray(new Date(),
                DataScrambler.increment(new Date(), -1, Calendar.MONTH).next(),
                DataScrambler.increment(new Date(), 1, Calendar.MONTH).next(),
                5).next()));

```

## Generate java.lang.String methods

```java

        // Generate an incremental string based on provided String, pattern and index
        System.out.println(DataScrambler.increment("test").next());
        System.out.println(DataScrambler.increment("test", "%s Nr%d").next());
        System.out.println(DataScrambler.increment("test", 100).next());
        System.out.println(DataScrambler.increment("test", "%s Nr%d", 100).next());

        // Generate an incremental string array based on provided String, pattern and index
        System.out.println(Arrays.asList(DataScrambler.incrementArray("test", "%s Nr%d", 100, 10).next()));

        // Generate an random string based on template String, count length
        System.out.println(DataScrambler.random("My Random String 123").next());
        System.out.println(DataScrambler.random("My Random String 123", 35).next());

        // Generate an random string array based on template String, count length
        System.out.println(Arrays.asList(DataScrambler.randomArray("My Random String 123", 10).next()));
        System.out.println(Arrays.asList(DataScrambler.randomArray("My Random String 123", 35, 10).next()));

```

## Generate java.util.Collection methods

```java

        // A list of incremented integer with step 1
        System.out.println(DataScrambler.of(new ArrayList<Integer>(), DataScrambler.increment(1)).next());
        // A list of random double in a range 1.0-400.0
        System.out.println(DataScrambler.of(new ArrayList<Double>(), DataScrambler.random(1.0d, 400.0d)).next());

        // A random element from provided collection
        System.out.printf("%s random element: %s%n", new HashSet<String>(Arrays.asList("aa", "bb", "cc")), DataScrambler.randomOf(new HashSet<String>(Arrays.asList("aa", "bb", "cc"))).next());
        // A random element from provided collection
        System.out.printf("%s random element: %s%n", new HashSet<Integer>(Arrays.asList(1, 2, 3, 4, 5)), DataScrambler.randomOf(Arrays.asList(1, 2, 3, 4, 5)).next());

```

## Generate java.util.Map methods

```java

        System.out.println(DataScrambler.mapOf(LinkedHashMap.class, new LinkedHashMap() {{
            put("ValueSID", DataScrambler.increment(1));
            put("SomeID", new Constant(1));
            put("Amount", DataScrambler.increment(100.0d));
            put("products", DataScrambler.collectionOf(ArrayList.class, DataScrambler.mapOf(LinkedHashMap.class, new LinkedHashMap() {{
                put("ProductSID", DataScrambler.increment(1));
                put("Name", new ListRandomElement<String>(Arrays.asList("Table Tennis Set", "Ping Pong Balls", "Table Tennis Racket")));
                put("Price", DataScrambler.random(16.0d, 200.0d));
            }})));
        }}).next());

```

## Java Bean class instance fields generation
With all above declared generation methods, DataScrambler can generate data for almost all class types.
`org.prismus.scrambler.DataScrambler.instanceOf(...)` methods allow to create instances either automatically or based on 
provided definitions.

**Example of automatically generated fields for Person.class**  
```java

        final InstanceValue<Person> personValue = DataScrambler.instanceOf(Person.class);
        Person person = personValue.next();
        Assert.assertNotNull(person.getFirstName());
        Assert.assertNotNull(person.getLastName());
        Assert.assertNotNull(person.getSex());
        Assert.assertNotNull(person.getDob());
        Assert.assertNotNull(person.getAddress());
        Assert.assertNotNull(person.getAddress().getNumber());
        Assert.assertNotNull(person.getAddress().getStreet());
        Assert.assertNotNull(person.getAddress().getCity());
        Assert.assertNotNull(person.getAddress().getState());

```

## Groovy scripting capabilities
Data Scrambler API is 100% implemented in Java. Itself Java is a great language, but there are a lot of 
ceremonies in coding that sometimes makes it boring. New generation languages like Groovy/Scala are less verbose/makes 
coding easier, and DataScrambler API uses Groovy capabilities to make value definitions/data generation process easier.

### DataScrambler DSL
On top of DataScrambler generation API a DSL is defined that adds generation capabilities to Java objects. 
As result, value definitions process is less verbose, and definitions are easy for reading/writing.

**DataScrambler DSL examples**:  
```groovy

    Integer.random(1, 100)
    Long.random(1L, 100L)
    new Date().random()
    'some template string'.random(100)

    [1, 2, 3].randomOf()

    1.0.increment()
    Integer.increment(1, 100)
    Long.increment(1L, 100L)

    new Date().increment()
    new Date().increment(Calendar.MINUTE)
    new Date().increment(Calendar.HOUR, 2)

    'some template string'.increment('some%s%d')
    'some template string'.increment('some%s%d', 12)

    constant 1.0
    constant 1
    constant 1L
    constant new Date()
    constant 'some template string'
    constant new Object()

    new ArrayList(1024).of(new RandomInteger(1, 100))
    new ArrayList(1024).of(new RandomString('some message', 45), 1024)

    1.random(0, 100)
    [1, 2, 3].randomOf()
    of new Date().increment(Calendar.HOUR)

    'text'.increment()
    1.random(1, 100)
    [1, 2, 3].randomOf()
    new Date().increment(Calendar.HOUR, 1)
    'some template string'.constant()

    2.random(1, 100)
    3L.random(1L, 100L)
    new Date().random()
    int.arrayOf(1.random())
    'some template string'.random(100)

```

### Java bean class properties generation

```groovy

    void 'check value definitions (tree definition) for instance'() {
        given:
        GroovyValueDefinition.register()
        final instance = new InstanceValue<Order>(Order).usingDefinitions(
                (BigDecimal): BigDecimal.ONE.random(1.0, 100.0),
                (int[]): int.arrayOf(10.increment(10)),
                person: Person.definition(
                        'firstName': ['Andy', 'Nicole', 'Nicolas', 'Jasmine'].randomOf(),
                        'lastName': ['Smith', 'Ferrara', 'Maldini', "Shaffer"].randomOf(),
                        'sex': ['M', 'F'].randomOf(),
                        'phone': ['425-452-0001', '425-452-0002', '425-452-0003', "425-452-0004"].randomOf()
                ),
                'item*': [].of(OrderItem.definition(
                        quantity: 1.random(1, 5),
                        details: "detail field".random(200),
                        '*Time': new Date().random(),
                        (Product): Product.definition(
                                name: ['Candies', 'Star Wars Lego Factory', 'Star War Ninja GO'].randomOf(),
                                price: 2.0.random(10.0, 50.0),
                        )
                ), 10),
        )
        final order = instance.next()

```

### Value definition script files

**Example of values definition**  
```groovy

definition(Integer.random(1, 100))
definition('randomElement', [1, 2, 3].randomOf())
definition(new Date().increment(1, Calendar.HOUR))
constant 'some template string'
definition(new HashSet().of(4.increment(10), 100))
definition(prop1: 2.random(1, 100))
definition('prop2', 'some template string'.random('%s pattern %d'))

```

**More complex script with scripts inclusion and context map properties injection**  
```groovy

import org.prismus.scrambler.DataScrambler
import org.prismus.scrambler.beans.Address
import org.prismus.scrambler.beans.ClassRoom
import org.prismus.scrambler.beans.Person
import org.prismus.scrambler.beans.School
import org.prismus.scrambler.value.ReferenceValue

/**
 * School entities definition, with adequate address and person definitions
 *
 * @author Serge Pruteanu
 */
definition('*Id', 1.increment())
definition('name', ['Enatai', 'Medina', 'Value Crest', 'Newport', 'Cherry Crest', 'Eastgate Elementary'].randomOf())

final personDefinition = DataScrambler.parseDefinition('/person-definition.groovy')
final addressDefinition = DataScrambler.parseDefinition('/address-definition.groovy')
usingDefinition(personDefinition)

definition('address', DataScrambler.instanceOf(Address).usingDefinitions(addressDefinition))

definition('staff', [].of(DataScrambler.instanceOf(Person)
        .usingDefinitions(personDefinition).usingDefinitions(addressDefinition)))

definition('principle', new ReferenceValue('staff') {
    @Override
    Object next() {
        final staffList = super.next()
        final principle = staffList[0]
        setValue(principle)
        return principle
    }
})

definition('rooms', [].of(DataScrambler.instanceOf(ClassRoom)
        .usingDefinitions(personDefinition).usingDefinitions(addressDefinition)
        .usingDefinitions(
            parent: School.reference(), schoolId: School.reference('schoolId'),
            roomNumber: "101A".random(4),
            teacher: new ReferenceValue(School, 'staff') {
                @Override
                Object next() {
                    final staffList = super.next()
                    final principle = staffList[0]
                    setValue(principle)
                    return principle
                }
            },
            students: [].of(DataScrambler.instanceOf(Person)
                    .usingDefinitions(personDefinition).usingDefinitions(addressDefinition))
        )
))

```

**And know how above definition can be used in Java Bean instance creation**:

Bellow snippet shows how values definition script is used to create an instance of type School 
with address generated classes related to Washington state (cities, adequate zip codes), and adequate Persons with "real" 
names, "adequate" phone numbers, DOBs.

```java

    @Test
    public void test_complex_reused_definitions() throws IOException {
        final InstanceValue<School> schoolValue = DataScrambler.instanceOf(School.class, new HashMap<String, Object>() {{
            put("state", "Washington");
        }}, "/school-definition.groovy");
        School school = schoolValue.next();
        Assert.assertNotNull(school);
    }
    
```

### IntelliJ IDEA IDE highlighting/context completion support
DataScrambler API DSL is defined in `org.prismus.scrambler.value.ValueDefinition.gdsl` and it is included in final jar. 
IntelliJ IDEA IDE will detect the definition automatically and will provide DSL methods highlighting and 
context support code/type completion.
