## Groovy scripting capabilities
Data Scrambler API is 100% implemented in Java. Itself Java is a great language, but there are a lot of 
ceremonies in coding that sometimes makes it boring. New generation languages like Groovy/Scala are less verbose/makes 
coding easier, and DataScrambler API uses Groovy capabilities to make data definitions/data generation process easier.

### DataScrambler DSL
On top of DataScrambler generation API a DSL is defined that adds generation capabilities to Java objects. 
As result, data definitions process is less verbose, and definitions are easy for reading/writing.

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

### Definitions scripts

**Example of datas definition**  
```groovy

definition(Integer.random(1, 100))
definition('randomElement', [1, 2, 3].randomOf())
definition(new Date().increment(1, Calendar.HOUR))
constant 'some template string'
definition(new HashSet().of(4.increment(10), 100))
definition(prop1: 2.random(1, 100))
definition('prop2', 'some template string'.random('%s pattern %d'))

```

**More complex example with scripts inclusion and context map properties injection**  
```groovy

import org.prismus.scrambler.InstanceScrambler
import org.prismus.scrambler.beans.Address
import org.prismus.scrambler.beans.ClassRoom
import org.prismus.scrambler.beans.Person
import org.prismus.scrambler.beans.School
import org.prismus.scrambler.data.ReferenceData

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

definition('principle', new ReferenceData('staff') {
    @Override
    protected Object doNext() {
        final staffList = super.doNext()
        final principle = staffList[0]
        return principle
    }
})

definition('rooms', [].of(InstanceScrambler.instanceOf(ClassRoom)
        .usingDefinitions(personDefinition).usingDefinitions(addressDefinition)
        .usingDefinitions(
            parent: School.reference(), schoolId: School.reference('schoolId'),
            roomNumber: "101A".random(4),
            teacher: new ReferenceData(School, 'staff') {
                @Override
                protected Object doNext() {
                    final staffList = super.doNext()
                    final principle = staffList[0]
                    return principle
                }
            },
            students: [].of(InstanceScrambler.instanceOf(Person)
                    .usingDefinitions(personDefinition).usingDefinitions(addressDefinition))
        )
))

```

### IntelliJ IDEA IDE highlighting/context completion support
DataScrambler API DSL support is defined in `org.prismus.scrambler.data.DataDefinition.gdsl` and it is included in final jar. 
IntelliJ IDEA IDE will detect the definition automatically and will provide DSL methods highlighting and 
context support code/type completion.
