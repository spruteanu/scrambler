package org.prismus.scrambler.builder

import org.prismus.scrambler.Value
import org.prismus.scrambler.value.Constant
import org.prismus.scrambler.value.IncrementalInteger
import spock.lang.Specification

/**
 * @author Serge Pruteanu
 */
class InstanceTest extends Specification {

    void 'instance creation'(Object value, Class instanceType) {
        given:
        final instance = new Instance(value)

        and:
        final instanceValue = instance.checkCreateInstance()

        expect:
        instanceType == instanceValue.class

        where:
        value << [IncrementalInteger.class.name, IncrementalInteger, new IncrementalInteger(), IncrementalInteger]
        instanceType << [IncrementalInteger, IncrementalInteger, IncrementalInteger, IncrementalInteger]
    }

    @SuppressWarnings("GroovyAssignabilityCheck")
    void 'instance creation with arguments'(Object value, Class instanceType,
                                            List<Value> constructorValues, Integer expectedStep) {
        given:
        final instance = new Instance<IncrementalInteger>(value)
        instance.constructorValues = constructorValues

        and:
        final instanceValue = instance.checkCreateInstance()

        expect:
        instanceType == instanceValue.class
        constructorValues.get(0).next() == instanceValue.value
        expectedStep == instanceValue.step

        where:
        value << [IncrementalInteger.class.name, IncrementalInteger]
        instanceType << [IncrementalInteger, IncrementalInteger]
        constructorValues << [[new Constant(5)], [new Constant(100), new Constant(3)]]
        expectedStep << [1, 3]
    }

    void 'test lookup property definitions'() {
        given:
        final instance = new Instance<Order>(new Order())

        and:
        final propertyDescriptors = instance.lookupPropertyDefinitions(instance.value)

        expect:
        null != propertyDescriptors
        4 == propertyDescriptors.size()
        propertyDescriptors.keySet().equals(['arrayField', 'total', 'person', 'items'] as Set)
        ArrayList == propertyDescriptors.get('items')?.value?.class
    }

    void 'test populate instance with properties'() {
        given:
        final instance = new Instance<IncrementalInteger>(new IncrementalInteger())

        and:
        instance.populate(instance.value, [step: 3, value: 104])

        expect:
        3 == instance.value.step
        104 == instance.value.value
    }

    void 'check value definitions (tree definition) for instance'() {
        given:
        ValueCategory.registerValueMetaClasses()
        final instance = new Instance<Order>(Order)
        final definition = new ValueDefinition(
                (BigDecimal): BigDecimal.ONE.random(1.0, 100.0),
                (int[]): int.array(10.incremental(10)),
                person: Person.of(
                        'firstName': ['Andy', 'Nicole', 'Nicolas', 'Jasmine'].randomOf(),
                        'lastName': ['Smith', 'Ferrara', 'Maldini', "Shaffer"].randomOf(),
                        'age': 11.random(10, 60),
                        'sex': ['M' as char, 'F' as char].randomOf(),
                        'phone': ['425-452-0001', '425-452-0002', '425-452-0003', "425-452-0004"].randomOf()
                ),
                'item*': [].of(OrderItem.of(
                        quantity: 1.random(1, 5),
                        details: "detail field".random(200),
                        '*Time': new Date().random(),
                        (Product): Product.of(
                                name: ['Candies', 'Star Wars Lego Factory', 'Star War Ninja GO'].randomOf(),
                                price: 2.0.random(10.0, 50.0),
                        )
                ), 10),
        )
        instance.using(definition)
        final order = instance.next()

        expect:
        order.total > 1
        order.arrayField != null
        order.arrayField.length  > 0
        order.person != null
        order.person.firstName != null
        order.person.lastName != null
        order.person.phone != null
        order.person.age > 10
        ['M' as char, 'F' as char].contains(order.person.sex)
        order.items.size() > 0
        order.items[0].quantity > 0
        order.items[0].details.length() > 0
        order.items[0].orderTime != null
        order.items[0].product != null
        order.items[0].product.name.length() > 0
        order.items[0].product.price > 1
    }

    void 'check value definition introspection'() {
        given:
        ValueCategory.registerValueMetaClasses()
        final definition = new ValueDefinition(
                'firstName': ['Andy', 'Nicole', 'Nicolas', 'Jasmine'].randomOf(),
                'lastName': ['Smith', 'Ferrara', 'Maldini', "Shaffer"].randomOf(),
                'age': 11.random(10, 60),
                'sex': ['M' as char, 'F' as char].randomOf(),
                'phone': ['425-452-0001', '425-452-0002', '425-452-0003', "425-452-0004"].randomOf(),
                name: ['Candies', 'Star Wars Lego Factory', 'Star War Ninja GO'].randomOf(),
                price: 2.0.random(10.0, 50.0),
        ).forType(Order, true)
        final Order order = (Order) definition.instanceValue.next()

        expect:
        order.total > 0
        order.person != null
        order.person.firstName != null
        order.person.lastName != null
        order.person.phone != null
        order.person.age > 1
        order.arrayField != null
        order.arrayField.length  > 0
    }

    // todo Serge: add test cases for parent reference
    void 'test if parent is set properly'() {
        given:
        ValueCategory.registerValueMetaClasses()

        final instance = new Instance<School>(School)
        final definition = new ValueDefinition(
                '*Id': 1.incremental(1),
                'name': ['Enatai', 'Medina', 'Value Crest', 'Newport'].randomOf(),
                (List): [].of(ClassRoom.of(
                        parent: new ParentValue(), //todo Serge: parent value property reference is not supported
                        roomNumber: "101A".random(4),
                ), 10),
        )
        instance.using(definition)
        final school = instance.next()

        expect:
        school != null
        school.rooms != null
        school.rooms.size() > 0
        school.rooms[0].roomNumber.length() > 0
        school.rooms[0].parent == school
//        school.rooms[0].schoolId == school.schoolId
    }

    private static class School {
        int schoolId
        String name
        List<ClassRoom> rooms
    }

    private static class ClassRoom {
        School parent
        int schoolId
        String roomNumber
    }

    private static class Order {
        BigDecimal total
        List<OrderItem> items = new ArrayList<OrderItem>()
        Person person

        int[] arrayField
    }

    private static class Product {
        String name
        BigDecimal price
    }

    private static class OrderItem {
        int quantity
        String details = "no name"
        Date orderTime
        Product product
    }

    private static class Person {
        String firstName
        String lastName
        int age
        char sex
        String phone

        Address address
    }

    private static class Address {
        String number
        String street
        String postalCode
        String city
        String room
    }

    private static class Book {
        String author
        String title
        Integer isbn
        Integer numberOfPages
        String publisher
    }

    private static class Employee {
        String name
        int age
        String designation
        double salary
    }

}
