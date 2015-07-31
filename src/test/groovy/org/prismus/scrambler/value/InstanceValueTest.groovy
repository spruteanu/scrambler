package org.prismus.scrambler.value

import org.junit.Assert
import org.prismus.scrambler.Value
import org.prismus.scrambler.beans.*
import spock.lang.Specification

/**
 * @author Serge Pruteanu
 */
@SuppressWarnings("GroovyConstructorNamedArguments")
class InstanceValueTest extends Specification {

    void 'instance creation'(Object value, Class expectedType) {
        given:
        final instance = new InstanceValue().usingValue(value)

        and:
        final instanceValue = instance.checkCreateInstance()

        expect:
        expectedType == instanceValue.class

        where:
        value << [School.class.name, School, new School(), School]
        expectedType << [School, School, School, School]
    }

    void 'verify encapsulated fields generation'() {
        given:
        final obj = new InstanceValue<EncapsulatedExtend>(EncapsulatedExtend).next()

        expect:
        null != obj.extReadOnly
        null != obj.extWriteOnly
        null != obj.extPublicValue
        null != obj.extImmutable
        null != obj.readOnly
//        null != obj.writeOnly
        null != obj.publicValue
    }

    @SuppressWarnings("GroovyAssignabilityCheck")
    void 'instance creation with arguments'(Object value, Class instanceType,
                                            List<Value> constructorValues, Integer expectedValue) {
        given:
        final instance = new InstanceValue().usingValue(value)
        instance.constructorValues = constructorValues

        and:
        final instanceValue = instance.checkCreateInstance()

        expect:
        instanceType == instanceValue.class
        constructorValues.get(0).next() == instanceValue.schoolId
        expectedValue == instanceValue.schoolId

        where:
        value << [School.class.name, School]
        instanceType << [School, School]
        constructorValues << [[new Constant(5)], [new Constant(100), new Constant("test")]]
        expectedValue << [5, 100]
    }

    void 'test lookup property definitions'() {
        given:
        final instance = new InstanceValue<Order>().usingValue(new Order())

        and:
        final propertyDescriptors = instance.lookupFields(instance.get())

        expect:
        null != propertyDescriptors
        4 == propertyDescriptors.size()
        propertyDescriptors.keySet().equals(['arrayField', 'total', 'person', 'items'] as Set)
        ArrayList == propertyDescriptors.get('items')?.value?.class
    }

    void 'test populate instance with properties'() {
        given:
        final instance = new InstanceValue<School>().usingValue(new School()).build(null)

        and:
        instance.populate(instance.get(), [schoolId: 3, name: 'Enatai'])

        expect:
        3 == instance.get().schoolId
        'Enatai' == instance.get().name
    }

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

        expect:
        order.total > 1
        order.arrayField != null
        order.arrayField.length > 0
        order.person != null
        order.person.firstName != null
        order.person.lastName != null
        order.person.phone != null
        ['M', 'F'].contains(order.person.sex)
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
        GroovyValueDefinition.register()
        final instanceValue = new InstanceValue<Order>(Order).usingDefinitions(new ValueDefinition(
                'firstName': ['Andy', 'Nicole', 'Nicolas', 'Jasmine'].randomOf(),
                'lastName': ['Smith', 'Ferrara', 'Maldini', "Shaffer"].randomOf(),
                'sex': ['M', 'F'].randomOf(),
                'phone': ['425-452-0001', '425-452-0002', '425-452-0003', "425-452-0004"].randomOf(),
                name: ['Candies', 'Star Wars Lego Factory', 'Star War Ninja GO'].randomOf(),
                price: 2.0.random(10.0, 50.0),
        ))
        final Order order = (Order) instanceValue.next()

        expect:
        order.total > 0
        order.arrayField != null
        order.arrayField.length > 0
        order.person != null
        order.person.firstName != null
        order.person.lastName != null
        order.person.phone != null
    }

    void 'test if parent is set properly'() {
        given:
        GroovyValueDefinition.register()

        final instance = new InstanceValue<School>(School).usingDefinitions(
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
    }

    private static class Encapsulated {
        private String readOnly
        private String writeOnly
        private String immutable

        String publicValue

        String getReadOnly() {
            return readOnly
        }

        void setWriteOnly(String writeOnly) {
            this.writeOnly = writeOnly
        }
    }

    private static class EncapsulatedExtend extends Encapsulated {
        private String extReadOnly
        private String extWriteOnly
        private String extImmutable

        String extPublicValue

        String getExtReadOnly() {
            return extReadOnly
        }

        void setExtWriteOnly(String extWriteOnly) {
            this.extWriteOnly = extWriteOnly
        }
    }

}
