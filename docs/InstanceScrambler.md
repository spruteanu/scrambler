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
