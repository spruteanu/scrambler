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
