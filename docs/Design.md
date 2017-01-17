## org.prismus.scrambler.Data
Data interface defines data generation capabilities. 

![Data interface](data-class-dgm.png)

```java
/**
 * An interface used to generate data.
 */
public interface Data<T> extends Serializable, Cloneable {
    /**
     * Generates data.
     *
     * @return an instance of object
     */
    T next();

    /**
     * Gets current generated data
     *
     * @return current data
     */
    T get();
}
```

It consists of 2 methods: 

1. ``T next()``
  Generates a data. Each call will produce a new one.
2. ``T get()``
  Method is used to get the data from prior execution of ``next()`` method.
  
## Custom org.prismus.scrambler.Data implementation 
It is recommended to create custom Data implementations by extending **org.prismus.scrambler.data.ConstantData** class.
This class is a template that allows to get and performs data setting for newly generated object.
Your implementation has to extend method **org.prismus.scrambler.data.Constant#doNext()** for data generation.

```java
public class ConstantData<T> implements Data<T> {
...
    public T get() {
        return object;
    }

    public T next() {
        final T data = doNext();
        setObject(data);
        return data;
    }

    protected T doNext() {
        return object;
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

## Facades
In order to hide complexity and have a simple API, generation capabilities are exposed thru Facade classes. 
Facades are defined in ``org.prismus.scrambler`` package and are grouped per type and named accordingly.

## java.lang.Number generation facade and classes
DataScrambler offers following ways to generate a Number data:

1. Incremental Number with a step (default is 1)
1. Random simple or in a range
1. Array of objects or primitives either incrementally or randomly

**Examples:**

```java
// generate incremental integer with default step (1)
System.out.println(NumberScrambler.increment(1).next());

// generate incremental array with step (100) starting from 0
Data<Integer[]> integerArray = ArrayScrambler.incrementArray(new Integer[10], 100, 10);
System.out.println(Arrays.asList(integerArray.next()));

// generate incremental array with step (10.5) starting from 0
Data<float[]> primitiveFloatArray = ArrayScrambler.incrementArray(new float[10], 10.5f, 10);
System.out.println(Arrays.asList(primitiveFloatArray.next()));

// generate random long in a range
System.out.println(NumberScrambler.random(900L, 1000L).next());
```
![Number generation classes](number-data-class-dgm.png)

## java.util.Date generation facade and classes
Dates generation can be done in following way:

1. Incrementally by calendar step(s)
   * Steps can be defined either using ``java.util.Calendar`` constants or using builder methods (verbose instead of magic constants)
1. Randomly simple or in a range
1. Array of dates either incrementally or random

**Examples:**

```java
// generate incremented date with default step and default calendar field (DAY)
System.out.println(DateScrambler.increment(new Date()).next());

// generate incremented date by several calendar fields
System.out.println(DateScrambler.increment(new Date())
    .days(-1)
    .hours(2)
    .minutes(15)
.next());

// generate an array of dates
System.out.println(Arrays.asList(DateScrambler.arrayOf(DateScrambler.increment(new Date())
    .days(-1)
    .hours(2)
    .minutes(15),
5).next()));

// generate random date in a day period
System.out.println(DateScrambler.random(new Date()).next());
// generate random date in a month period
System.out.println(DateScrambler.random(new Date(),
        DateScrambler.increment(new Date(), -1, Calendar.MONTH).next(),
        DateScrambler.increment(new Date(), 1, Calendar.MONTH).next()
).next());

System.out.println(Arrays.asList(DateScrambler.randomArray(new Date(),
        DateScrambler.increment(new Date(), -1, Calendar.MONTH).next(),
        DateScrambler.increment(new Date(), 1, Calendar.MONTH).next(),
        5).next()));
```

![Date generation classes](date-data-class-dgm.png)

## java.lang.String generation facade and classes
1. Incremental String using provided formatter pattern with an incremented index 
1. Random String using provided pattern of specified length 
1. Random UUID String 
1. Array of Strings generated incrementally or randomly

**Examples**

```java
// Generate an incremental string based on provided String, pattern and index
System.out.println(StringScrambler.increment("test", "%s Nr%d", 100).next());

// Generate an incremental string array based on provided String, pattern and index
System.out.println(Arrays.asList(StringScrambler.incrementArray("test", "%s Nr%d", 100, 10).next()));

// Generate an random string based on template String, count length
System.out.println(StringScrambler.random("My Random String 123").next());

// Generate an random string array based on template String, count length
System.out.println(Arrays.asList(StringScrambler.randomArray("My Random String 123", 10).next()));
```

![String generation classes](string-data-class-dgm.png)

## Generic object facade and classes
1. Constant data, each call to ``next()`` returns same data
1. Generated random data (simple or in a range) by providing Class type (if supported)
1. Random array of specified class type
1. Random Boolean

**Examples**

```java
// declare a data instance that represents constant data
Assert.assertEquals(1, ObjectScrambler.constant(1).next().longValue());

// declare a data instance that will return randomly generated array
final Data<Long[]> longValues = ArrayScrambler.randomArray(1L, 10);
Assert.assertEquals(10, longValues.next().length);

// declare a data instance that will generate an array of short primitives randomly in a specified range
final Data<short[]> primitivesInRange = ArrayScrambler.arrayOf(new short[10], NumberScrambler.random((short) 900, (short) 1000));
Assert.assertEquals(10, primitivesInRange.next().length);
```

![Generic object classes](generic-object-data-class-dgm.png)

## Arrays generation facade and classes
1. Array of specified type using provided generation strategy
1. Incremental array with a step
1. Random simple or in a range
1. Random element from provided array
1. Generate combinations array using provided array template

**Examples:**

```java
// declare a data instance that will return randomly generated primitives array
final Data<long[]> longPrimitives = ArrayScrambler.randomArray(new long[10]);
Assert.assertEquals(10, longPrimitives.next().length);

// declare a data instance that will generate an array of Long objects randomly in a specified range
final Data<Long[]> randomsInRange = ArrayScrambler.arrayOf(new Long[10], NumberScrambler.random(900L, 1000L));
Assert.assertEquals(10, randomsInRange.next().length);

// declare a data instance that will generate an array of short primitives randomly in a specified range
final Data<short[]> primitivesInRange = ArrayScrambler.arrayOf(new short[10], NumberScrambler.random((short) 900, (short) 1000));
Assert.assertEquals(10, primitivesInRange.next().length);

// Generate one combinations sequence of provided integer array using Johnson Trotter algorithm
ArrayScrambler.combinationsOf(1, 2, 3, 4, 5).next();
```

![Array generation classes](array-data-class-dgm.png)

## java.util.Collection generation facade and classes
1. Generate a collection using provided strategy
1. Incremental collection
1. Random collection simple or in a range
1. Random element from provided collection template
1. Combinations using provided collection template

**Examples:**
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

## java.util.Map generation facade and classes
1. Generate a map of datas using provided key/rule

**Examples**
```java
System.out.println(MapScrambler.mapOf(LinkedHashMap.class, new LinkedHashMap() {{
    put("ValueSID", NumberScrambler.increment(1));
    put("SomeID", new Constant(1));
    put("Amount", NumberScrambler.increment(100.0d));
    put("products", CollectionScrambler.collectionOf(ArrayList.class, MapScrambler.mapOf(LinkedHashMap.class, new LinkedHashMap() {{
        put("ProductSID", NumberScrambler.increment(1));
        put("Name", new ListRandomElement<String>(Arrays.asList("Table Tennis Set", "Ping Pong Balls", "Table Tennis Racket")));
        put("Price", NumberScrambler.random(16.0d, 200.0d));
    }})));
}}).next());
```

![Map generation classes](map-data-class-dgm.png)

## org.prismus.scrambler.DataPredicate
Data predicates are defined to match java classes fields with data generation rules.
Fields can be matched by name and/or type. In order to have matching process more flexible, 
fields can be matched by name, wildcard or regular expression. 

```java
/**
 * Interface that matches either {@code property} and/or {@code data}.
 * The predicate is mostly used as a Map.key for data definitions, to match instance fields
 */
public interface DataPredicate {
    /**
     * Match either property and/or data, used to identify if {@link Data} is applicable for provided arguments
     *
     * @param property property name to be matched
     * @param data data to be matched
     * @return true if it should be applicable
     */
    boolean apply(String property, Object data);
}
```

``org.prismus.scrambler.DataPredicates`` exposes variety of available predicates.

![Data Predicate interface](data-predicate-dgm.png)
![Data Predicate interface](data-predicate-class-dgm.png)

## org.prismus.scrambler.data.DataDefinition
DataDefinition is a builder that represents a registry of data generation rules matched by predicates.
Following functionality is offered by ``org.prismus.scrambler.data.DataDefinition``:

1. Register data for provided predicate through ``definition(...), constant(...), reference(...)`` methods
1. Load definitions from external sources<br/>
  1. ``usingDefinitions``        - load existing definitions. If provided resource/file is not found, an exception will be thrown
  1. ``scanDefinitions``         - load definitions if exists.
  1. ``scanLibraryDefinitions``  - scan classpath for definition sources and load if any matched. Scanning matches all ``*data-definition.groovy`` scripts once and caches them internally.
1. ``lookupData``                - lookup a registered definition for provided arguments. Methods are used from reference and instance data
1. Context properties.<br/>
  In order to make definition' resources more generic, it is possible to register a map of properties from 
``org.prismus.scrambler.InstanceScrambler`` methods and those properties will be available/injected from/to script definitions 
using ``getContextProperty(...)``. More details will be given in DSL definitions section.

All ``org.prismus.scrambler.data.DataDefinition`` methods are accessible from definition scripts.

## Java instances generation
``org.prismus.scrambler.data.InstanceData`` is a builder to generate java class with field's data for it.
Following operations are available:

1. Create an instance from defaults
1. Create an instance from a map of definitions
1. Create an instance using predefined rules or by scanning classpath on ``*-definition.groovy`` resources

**Notes:**

* If no fields are defined or a field doesn't have a match, field will be attempted to be generated using default 
definitions. DataScrambler API is shipped with ``/org.prismus.scrambler.data.default-definition.groovy`` 
and it can be changed by ``usingDefaultDefinitions(...)`` methods
* If no fields are defined, classpath will be scanned for class definition resource using following naming convention:<br/>
``"Class name" + "-definition.groovy"``, that for ``Person.class`` will match ``Person-definition.groovy``
* Often it is needed to populate a field based on some field' datas of created class. 
DataScrambler API allows that using ``org.prismus.scrambler.data.ReferenceData`` rule. See bellow more details. 

![Instance data classes](instance-data-class-dgm.png)

**Examples:**</br>
Groovy script example<br/> 
```groovy
// create an instance of School.class that has a list of rooms
final instance = new InstanceData<School>(School).usingDefinitions(
        '*Id': 1.increment(1),
        'name': ['Enatai', 'Medina', 'Value Crest', 'Newport'].randomOf(),
        (List): [].of(ClassRoom.definition(
                parent: School.reference(),
                schoolId: School.reference('schoolId'),
                roomNumber: "101A".random(4),
        ), 10),
)
final school = instance.next()
```

```java
// create an instance of Person.class with fields default generation rules. If in classpath there is a ``Person-definition.groovy`` script,
// fields will be generated using definitions from it
final InstanceData<Person> personValue = InstanceScrambler.instanceOf(Person.class);
Person person = personValue.next();

//...
// create an instance of Person.class using /person-definition.groovy definitions script
final InstanceData<Person> personValue = InstanceScrambler.instanceOf(Person.class, "/person-definition.groovy");
Person person = personValue.next();

//...
// Create an instance of Address.class using /address-definition.groovy and injected into script context properties
final InstanceData<Address> addressValue = InstanceScrambler.instanceOf(Address.class, new HashMap<String, Object>() {{
    put("state", "Washington");
}}, "/address-definition.groovy");
Address address = addressValue.next();
Assert.assertTrue("Washington".equals(address.getState()));

```

### Reference Data
Reference data is an implementation of data that allows to generate/create a field data by 'referencing' inquired 
class in defined rules. For more clarity, let's examine two examples:

#### Parent/Child relationship
There is a Database one-to-many relationship between School and Room, for which data are generated. Room.schoolId references
SID property from 'parent'. In order to resolve this data, a reference to ``School.class`` is defined for 'School.schoolId' field.
DataScrambler API parser will detect this declaration and will resolve the context for declared School.class reference data, thus, 
``Room.schoolId`` will get the data from already generated field ``School.schoolId``.

**Example from test: org.prismus.scrambler.data.InstanceDataTesttest if parent is set properly**<br/>
```groovy
final instance = new InstanceData<School>(School).usingDefinitions(
        '*Id': 1.increment(1),
        'name': ['Enatai', 'Medina', 'Value Crest', 'Newport'].randomOf(),
        (List): [].of(ClassRoom.definition(
                parent: School.reference(),
                schoolId: School.reference('schoolId'),
                roomNumber: "101A".random(4),
        ), 10),
)
final school = instance.next()

expect:
school != null
school.rooms != null
school.rooms.size() > 0
for (ClassRoom classRoom : school.rooms) {
    Assert.assertTrue(classRoom.roomNumber.length() > 0)
    Assert.assertSame(school, classRoom.parent)
    Assert.assertEquals(classRoom.schoolId, school.schoolId)
}
```

#### Generate field data based on other fields
Example where rules are defined for person definitions. There is a list of male and female names, and based on chosen 
name, an according gender should be set.

**An example snippet from person-definition.groovy:**<br/>
```groovy
final firstNamePattern = ~/(?i)(?:first\s*Name)|(?:first)/
definition(firstNamePattern, allFirstNames.randomOf())

... some logic here

//gender, reference the field on first name, and set according gender from generated first name field/data
definition(~/(?i)gender/, new ReferenceData(firstNamePattern) {
    @Override
    protected Object doNext() {
        final firstName = super.doNext()
        return femaleFirstNames.contains(firstName) ? 'Female' : 'Male'
    }
})

```

#### Notes
* Referenced types and fields MUST be defined/declared first
* Fields/types/Classes can be referenced by predicates
* References can be defined either in script using ``DataDefinition.reference(...)`` methods or explicitly using ``ReferenceData.class`` object

## DataScrambler DSL
Java is a neat programming language, but it is too verbose in the cases when you need to configure/define datas for a class.
Also, modern languages like Groovy and Scala comes with more capabilities that makes code even more succinct.
DataScrambler uses Groovy as a mechanism to define generation rules easier. Also, Groovy offers a possibility to create 
DSL, so, on top of DataScrambler API an according data generation DSL is created.

DataScrambler is implemented using Groovy metaclass DSL capabilities and mixin declarations 
(see [Groovy dynamic stateless mixins](https://groovyland.wordpress.com/2008/06/07/groovy-dynamic-stateless-mixins/) for example)
Scrambler generation facades' methods are added dynamically to groovy expando metaclass in 
``org.prismus.scrambler.data.GroovyDataDefinition`` static block.

**DSL Examples:**<br/>
```groovy
// define a random data in a range 1..100 of integer type 
Integer.random(1, 100)

// define a data that chooses a random element from the list
[1, 2, 3].randomOf()

// definition for an incremental Date, with 1 hour step 
new Date().increment(1, Calendar.HOUR)

// generate java.util.HashSet() 100 count with incremental step 10
new HashSet().of(4.increment(10), 100)

// random range 1..100 integer data
2.random(1, 100)

// random string data
'some template string'.random('pattern')
```

![DSL classes](groovy-definition-class-dgm.png)

### DataScrambler definition scripts
In addition to DSL registration, ``org.prismus.scrambler.data.GroovyDataDefinition`` is responsible for DataScrambler 
definition scripts parsing (``org.prismus.scrambler.data.GroovyDataDefinition#parseDefinitions(...)`` methods) 
from various sources (classpath resource, File, InputStream, Reader or even simple text with definitions).

**NOTE:**<br/>
Groovy Shell can be configured by defining in classpath ``/definitions-parser.properties`` file that will configure 
``org.codehaus.groovy.control.CompilerConfiguration`` properties.

Along with DataScrambler API DSL extension, methods from ``org.prismus.scrambler.data.DataDefinition`` are accessible 
from definitions script. This is implemented by setting the ``org.prismus.scrambler.data.DataDefinition`` instance 
to ``groovy.util.DelegatingScript`` that evaluates definitions script.

#### Performance
Groovy scripting features are used only at data generation rules definitions. Once fields are resolved with 
predicates matching, time is spent only on invoking data generation and fields population.

#### IDE support
Currently DataScrambler has support for highlighting and completion only for IntelliJ IDEA by 
``org.prismus.scrambler.data.DataDefinition.gdsl`` file shipped with library. 
Eclipse support will be added in near future.

## DataScrambler extensions

### Definitions library extension
In order to make definitions scripts re-usage an easy process, as well as to write less code for data generation, 
DataScrambler API has a capability of definitions scanning in the classpath. Definitions are scanned by listing 
all resources of ``META-INF/dictionary.desc``. Library definitions methods are available under definitions script scope
``org.prismus.scrambler.data.DataDefinition#usingLibraryDefinitions(...)`` as well as at 
``org.prismus.scrambler.data.InstanceData#usingLibraryDefinitions(...)``.

**Examples**<br/>
```groovy
final definition = new DataDefinition().usingLibraryDefinitions('person*')
expect: 'verify definitions loaded'
definition.definitionMap.size() > 0

and: 'verify person names generation'
0 < MapScrambler.mapOf(['firstName', 'lastName', 'middleName', 'gender', 'dateOfBirth', 'phone']).next().size()

and: 'verify address generation for Washington state'
0 < MapScrambler.mapOf(['Building Number', 'Street', 'State', 'City', 'Postal Code'], [state: 'Washington']).next().size()
```

#### Best Practices
1. Keep field definitions together<br/>
  With grouped definitions, if data will be generated not in the way how it is expected, it will be more easy to find the issue.
1. Define predicates accurately<br/>
  Define precise reg-exes if you have similar fields match, or filter those cases by type as well. 
  Keep in mind that fields are matched in the order how definitions are declared.
1. Keep custom generation rules simple<br/>
  It is not recommended to create inner rules in definition under groovy script. Such constructions may have performance 
  issues, as they are compiled dynamically. Instead, define the data class bellow and mark to compile statically 
  or define it as java class.
1. Declare definitions independent of usage context<br/>
  For better re-usage, keep definitions detached of usage context (do not use specific classes). 
  Verify how re-usable are definitions by generating data as a map and on java bean objects for example.

As a reference of definition scripts, see ``dictionary-ext`` module ones.

### Box Testing extension
**TBD, not finished yet**

### JDBC extension
**TBD, not implemented yet. Idea is to define rules that will populate database tables with data**
 