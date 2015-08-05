## Java Bean class instance fields generation
With all above declared generation methods, InstanceScrambler can generate data for almost all class types.
`org.prismus.scrambler.InstanceScrambler.instanceOf(...)` methods allow to create instances either automatically or based on 
provided definitions.

**Example of automatically generated fields for Person.class**  
```java

final InstanceValue<Person> personValue = InstanceScrambler.instanceOf(Person.class);
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
coding easier, and InstanceScrambler API uses Groovy capabilities to make value definitions/data generation process easier.

### InstanceScrambler DSL
On top of InstanceScrambler generation API a DSL is defined that adds generation capabilities to Java objects. 
As result, value definitions process is less verbose, and definitions are easy for reading/writing.

**InstanceScrambler DSL examples**:  
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
...

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

import org.prismus.scrambler.InstanceScrambler
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

final personDefinition = InstanceScrambler.parseDefinition('/person-definition.groovy')
final addressDefinition = InstanceScrambler.parseDefinition('/address-definition.groovy')
usingDefinition(personDefinition)

definition('address', InstanceScrambler.instanceOf(Address).usingDefinitions(addressDefinition))

definition('staff', [].of(InstanceScrambler.instanceOf(Person)
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

definition('rooms', [].of(InstanceScrambler.instanceOf(ClassRoom)
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
            students: [].of(InstanceScrambler.instanceOf(Person)
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
        final InstanceValue<School> schoolValue = InstanceScrambler.instanceOf(School.class, new HashMap<String, Object>() {{
            put("state", "Washington");
        }}, "/school-definition.groovy");
        School school = schoolValue.next();
        Assert.assertNotNull(school);
    }
    
```

### IntelliJ IDEA IDE highlighting/context completion support
InstanceScrambler API DSL is defined in `org.prismus.scrambler.value.ValueDefinition.gdsl` and it is included in final jar. 
IntelliJ IDEA IDE will detect the definition automatically and will provide DSL methods highlighting and 
context support code/type completion.
