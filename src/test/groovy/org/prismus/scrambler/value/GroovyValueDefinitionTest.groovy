package org.prismus.scrambler.value

import org.prismus.scrambler.Value
import spock.lang.Specification

/**
 * @author Serge Pruteanu
 */
class GroovyValueDefinitionTest extends Specification {

    void 'test parse type text definitions'() {
        given:
        final parser = new GroovyValueDefinition()

        expect:
        parser.parseDefinitionText("of Integer.random(1, 100)").propertyValueMap.size() > 0
        parser.parseDefinitionText("of Long.random(1L, 100L)").propertyValueMap.size() > 0
        parser.parseDefinitionText("of new Date().random()").propertyValueMap.size() > 0
        parser.parseDefinitionText("of 'some template string'.random(100)").propertyValueMap.size() > 0

        parser.parseDefinitionText("of([1, 2, 3].randomOf())").propertyValueMap.size() > 0

        parser.parseDefinitionText("of(new PropertyPredicate(pattern: ~/\\w+Sid/), new RandomInteger(1, 100))").propertyValueMap.size() > 0
        parser.parseDefinitionText("of(new PropertyPredicate('*Sid'), new RandomInteger(1, 100))").propertyValueMap.size() > 0

        parser.parseDefinitionText("of 1.0.increment()").propertyValueMap.size() > 0
        parser.parseDefinitionText("of Integer.increment(1, 100)").propertyValueMap.size() > 0
        parser.parseDefinitionText("of Long.increment(1L, 100L)").propertyValueMap.size() > 0

        parser.parseDefinitionText("of new Date().increment()").propertyValueMap.size() > 0
        parser.parseDefinitionText("of new Date().increment(Calendar.MINUTE)").propertyValueMap.size() > 0
        parser.parseDefinitionText("of new Date().increment(Calendar.HOUR, 2)").propertyValueMap.size() > 0

        parser.parseDefinitionText("of 'some template string'.increment('some%s%d')").propertyValueMap.size() > 0
        parser.parseDefinitionText("of 'some template string'.increment('some%s%d', 12)").propertyValueMap.size() > 0

        parser.parseDefinitionText("constant 1.0").propertyValueMap.size() > 0
        parser.parseDefinitionText("constant 1").propertyValueMap.size() > 0
        parser.parseDefinitionText("constant 1L").propertyValueMap.size() > 0
        parser.parseDefinitionText("constant new Date()").propertyValueMap.size() > 0
        parser.parseDefinitionText("constant 'some template string'").propertyValueMap.size() > 0
        parser.parseDefinitionText("constant new Object()").propertyValueMap.size() > 0

        parser.parseDefinitionText("of new ArrayList(1024).of(new RandomInteger(1, 100))").propertyValueMap.size() > 0
        parser.parseDefinitionText("of new ArrayList(1024).of(new RandomString('some message', 45), 1024)").propertyValueMap.size() > 0

        parser.parseDefinitionText("""
of(1.random(0, 100))
of([1, 2, 3].randomOf())
of new Date().increment(Calendar.HOUR)
constant 'some template string'
""").propertyValueMap.size() > 0
    }

    void 'test parse from resource'() {
        given:
        def parser = new GroovyValueDefinition()
        ValueDefinition valueDefinition = parser.parseDefinition('/test-vd.groovy')

        expect:
        valueDefinition.propertyValueMap.size() > 0

        and: 'check definitions parse from different resource'
        parser.parseDefinition(this.class.getResource('/test-vd.groovy')).propertyValueMap.size() > 0

        and: 'check value parse from different resource'
        Value.isInstance(parser.parseValue(this.class.getResource('/test-value.groovy')))
        Value.isInstance(parser.parseValue('/test-value.groovy'))
    }

    void 'test parse value type definitions'() {
        given:
        final parser = new GroovyValueDefinition()

        expect:
        Value.isInstance(parser.parseValueText("2.random(1, 100)"))

        parser.parseDefinitionText("of 2.random(1, 100)").propertyValueMap.size() > 0
        parser.parseDefinitionText("of 3L.random(1L, 100L)").propertyValueMap.size() > 0
        parser.parseDefinitionText("of new Date().random()").propertyValueMap.size() > 0
        parser.parseDefinitionText("of 'some template string'.random(100)").propertyValueMap.size() > 0

        parser.parseDefinitionText("of([1, 2, 3].randomOf())").propertyValueMap.size() > 0

        parser.parseDefinitionText("constant 1.0").propertyValueMap.size() > 0
        parser.parseDefinitionText("of 1.increment(100)").propertyValueMap.size() > 0
        parser.parseDefinitionText("of 1L.increment(100L)").propertyValueMap.size() > 0

        parser.parseDefinitionText("of new Date().increment()").propertyValueMap.size() > 0
        parser.parseDefinitionText("of new Date().increment(Calendar.HOUR)").propertyValueMap.size() > 0
        parser.parseDefinitionText("of new Date().increment(Calendar.HOUR, 1)").propertyValueMap.size() > 0

        parser.parseDefinitionText("of 'some template string'.increment('some%s%d')").propertyValueMap.size() > 0
        parser.parseDefinitionText("of 'some template string'.increment('some%s%d', 12)").propertyValueMap.size() > 0

        parser.parseDefinitionText("of 1.0.constant()").propertyValueMap.size() > 0
        parser.parseDefinitionText("of 1.constant()").propertyValueMap.size() > 0
        parser.parseDefinitionText("of 1L.constant()").propertyValueMap.size() > 0
        parser.parseDefinitionText("of new Date().constant()").propertyValueMap.size() > 0
        parser.parseDefinitionText("constant 'some string'").propertyValueMap.size() > 0
        parser.parseDefinitionText("of new Object().constant()").propertyValueMap.size() > 0

        parser.parseDefinitionText("""
of '*array', int.arrayOf(1.random())
of 1.random(1, 100)
of([1, 2, 3].randomOf())
of new Date().increment(Calendar.HOUR, 1)
of 'some template string'.constant()
""").propertyValueMap.size() > 0
    }

    void 'test property value definition'() {
        given:
        final parser = new GroovyValueDefinition()

        expect:
        parser.parseDefinitionText("of '*Sid', 1.0").propertyValueMap.size() > 0
        parser.parseDefinitionText("of '*prop1', 2.random(1, 100)").propertyValueMap.size() > 0
        parser.parseDefinitionText("of '*prop2', 3L.random(1L, 100L)").propertyValueMap.size() > 0
        parser.parseDefinitionText("of '*prop3', new Date().random()").propertyValueMap.size() > 0
        parser.parseDefinitionText("of '*prop4', 'some template string'.random(100)").propertyValueMap.size() > 0

        parser.parseDefinitionText("of('*prop5', [1, 2, 3].randomOf())").propertyValueMap.size() > 0

        parser.parseDefinitionText("of '*prop6', 1.0.increment()").propertyValueMap.size() > 0
        parser.parseDefinitionText("of '*prop7', 1.increment(100)").propertyValueMap.size() > 0
        parser.parseDefinitionText("of '*prop8', 1L.increment(100L)").propertyValueMap.size() > 0

        parser.parseDefinitionText("of '*prop9', new Date()").propertyValueMap.size() > 0
        parser.parseDefinitionText("of new Date().increment(Calendar.HOUR)").propertyValueMap.size() > 0
        parser.parseDefinitionText("of new Date().increment(Calendar.HOUR, 1)").propertyValueMap.size() > 0

        parser.parseDefinitionText("of '*prop13', 'some template string'.increment('some%d')").propertyValueMap.size() > 0
        parser.parseDefinitionText("of '*prop14', 'some template string'.increment('some%d', 12)").propertyValueMap.size() > 0

        parser.parseDefinitionText("of '*prop15', 1.0.constant()").propertyValueMap.size() > 0
        parser.parseDefinitionText("of '*prop16', 1.constant()").propertyValueMap.size() > 0
        parser.parseDefinitionText("of '*prop17', 1L.constant()").propertyValueMap.size() > 0
        parser.parseDefinitionText("of '*prop18', new Date().constant()").propertyValueMap.size() > 0
        parser.parseDefinitionText("of '*prop19', 'some string'.constant()").propertyValueMap.size() > 0
        parser.parseDefinitionText("of '*prop20', new Object().constant()").propertyValueMap.size() > 0
    }

    void 'test parse text class definitions'() {
        given:
        final parser = new GroovyValueDefinition()

        and:
        final valueDefinition = parser.parseDefinitionText("""
of org.prismus.scrambler.beans.School.of {
        of '*Id', 1
}
""")
        expect:
        valueDefinition.propertyValueMap.size() > 0

        and:
        parser.parseDefinitionText("""
of '*Instance|*Object', org.prismus.scrambler.beans.School.of {
        of '*Id', 3L.increment
}
""").propertyValueMap.size() > 0
        and:
        parser.parseDefinitionText("""
of org.prismus.scrambler.beans.School.of([2.0.random(), 3], {
        of '*Id', 1.random()
})
""").propertyValueMap.size() > 0
    }

    void 'test parse text with parent reference definition'() {
        given:
        final parser = new GroovyValueDefinition()

        and:
        def definition = parser.parseDefinitionText("""
of 'id', 1.increment(300)
of org.prismus.scrambler.beans.School.of {
        reference '*Instance'
}
""")

        expect: 'verify root definition'
        0 < definition.propertyValueMap.size()

        and:
        parser.parseDefinitionText("reference '*Parent'").propertyValueMap.size() > 0
    }

    void 'test parse container with value'() {
        given:
        final parser = new GroovyValueDefinition()

        and:
        def valueDefinition = parser.parseDefinitionText("of'mumu', [:].of(prop1: 'param'.increment(null, 1), prop2: 1.increment(1))")

        expect:
        valueDefinition.propertyValueMap.size() > 0

        and:
        0 < parser.parseDefinitionText("of 'cucu*', [:].of(prop1: 'param'.increment(null, 1), prop2: 1.increment(1))").propertyValueMap.size()
        0 < parser.parseDefinitionText("of 'cucu*', [].of('param'.random(10))").propertyValueMap.size()
    }

    void 'test value definitions DSL from code'() {
        given:
        GroovyValueDefinition.register()

        expect:
        null != 'text'.increment()
        null != 1.random(1, 100)
        null != [1, 2, 3].randomOf()
        null != new Date().increment(Calendar.HOUR, 1)
        null != 'some template string'.constant()

        null != 2.random(1, 100)
        null != 3L.random(1L, 100L)
        null != new Date().random()
        null != int.arrayOf(1.random())
        null != 'some template string'.random(100)
    }

    void 'test parse text for map'() {
        given:
        final parser = new GroovyValueDefinition()
        final valueDefinition = parser.parseDefinitionText("""
of('a*':1.constant(), b:2.random(), c:'cucu')
of('*Sid': Integer.random(1, 100))
""")

        expect:
        valueDefinition.propertyValueMap.size() > 0
    }

}
