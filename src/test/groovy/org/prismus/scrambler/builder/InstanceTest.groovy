package org.prismus.scrambler.builder

import org.prismus.scrambler.Value
import org.prismus.scrambler.property.Constant
import org.prismus.scrambler.property.IncrementalInteger
import spock.lang.Specification

/**
 * @author Serge Pruteanu
 */
class InstanceTest extends Specification {
    // todo Serge: add end to end test

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
        3 == propertyDescriptors.size()
        propertyDescriptors.containsKey('total')
        propertyDescriptors.containsKey('person')
        propertyDescriptors.containsKey('items')
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

    private static class Order {
        BigDecimal total
        List<OrderItem> items = new ArrayList<OrderItem>()
        Person person
    }

    private static class Product {
        String name
        BigDecimal price
    }

    private static class OrderItem {
        int quantity
        String details = "no name"
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
