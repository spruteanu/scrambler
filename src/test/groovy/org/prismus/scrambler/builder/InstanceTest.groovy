package org.prismus.scrambler.builder

import org.prismus.scrambler.Value
import org.prismus.scrambler.property.Constant
import org.prismus.scrambler.property.IncrementalInteger
import org.prismus.scrambler.property.IncrementalString
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

    void 'test lookup property descriptors'() {
        given:
        final instance = new Instance<IncrementalString>(new IncrementalString())

        and:
        final propertyDescriptors = instance.lookupPropertyDescriptors(instance.value)

        expect:
        null != propertyDescriptors
        !propertyDescriptors.containsKey('class')
        propertyDescriptors.containsKey('pattern')
        propertyDescriptors.containsKey('index')
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

    // todo Serge: add end to end test
}
