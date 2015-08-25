package org.prismus.scrambler.value

import org.prismus.scrambler.Value
import spock.lang.Specification

/**
 * @author Serge Pruteanu
 */
class GroovyValueDefinitionTest extends Specification {

    void 'test scan value definitions'() {
        final foundResources = new LinkedHashSet<String>()
        ValueDefinition.Holder.lookupDefinitionResources(getClass().getResource('/test-scan-value-definition.jar').toURI().path, foundResources)
        expect:
        1 == foundResources.size()
        1 == ValueDefinition.matchValueDefinitions('test-scan*', foundResources).size()
        1 == ValueDefinition.matchValueDefinitions(null, foundResources).size()
        0 == ValueDefinition.matchValueDefinitions('resource', foundResources).size()

        and:'check scan definitions'
        final definition = new ValueDefinition()
        definition.scanLibraryDefinitions('test-scan*', foundResources)
        definition.getDefinitionMap().size() > 0
    }

    void 'test parse type text definitions'() {
        given:
        final parser = new GroovyValueDefinition()

        expect:
        parser.parseDefinitionText("definition Integer.random(1, 100)").definitionMap.size() > 0
        parser.parseDefinitionText("definition Long.random(1L, 100L)").definitionMap.size() > 0
        parser.parseDefinitionText("definition new Date().random()").definitionMap.size() > 0
        parser.parseDefinitionText("definition 'some template string'.random(100)").definitionMap.size() > 0

        parser.parseDefinitionText("definition([1, 2, 3].randomOf())").definitionMap.size() > 0

        parser.parseDefinitionText("definition(new PropertyPredicate(pattern: ~/\\w+Sid/), new RandomInteger(1, 100))").definitionMap.size() > 0
        parser.parseDefinitionText("definition(new PropertyPredicate('*Sid'), new RandomInteger(1, 100))").definitionMap.size() > 0

        parser.parseDefinitionText("definition 1.0.increment()").definitionMap.size() > 0
        parser.parseDefinitionText("definition Integer.increment(1, 100)").definitionMap.size() > 0
        parser.parseDefinitionText("definition Long.increment(1L, 100L)").definitionMap.size() > 0

        parser.parseDefinitionText("definition new Date().increment()").definitionMap.size() > 0
        parser.parseDefinitionText("definition new Date().increment(Calendar.MINUTE)").definitionMap.size() > 0
        parser.parseDefinitionText("definition new Date().increment(Calendar.HOUR, 2)").definitionMap.size() > 0

        parser.parseDefinitionText("definition 'some template string'.increment('some%s%d')").definitionMap.size() > 0
        parser.parseDefinitionText("definition 'some template string'.increment('some%s%d', 12)").definitionMap.size() > 0

        parser.parseDefinitionText("constant 1.0").definitionMap.size() > 0
        parser.parseDefinitionText("constant 1").definitionMap.size() > 0
        parser.parseDefinitionText("constant 1L").definitionMap.size() > 0
        parser.parseDefinitionText("constant new Date()").definitionMap.size() > 0
        parser.parseDefinitionText("constant 'some template string'").definitionMap.size() > 0
        parser.parseDefinitionText("constant new Object()").definitionMap.size() > 0

        parser.parseDefinitionText("definition new ArrayList(1024).of(new RandomInteger(1, 100))").definitionMap.size() > 0
        parser.parseDefinitionText("definition new ArrayList(1024).of(new RandomString('some message', 45), 1024)").definitionMap.size() > 0

        parser.parseDefinitionText("""
definition(1.random(0, 100))
definition([1, 2, 3].randomOf())
definition new Date().increment(Calendar.HOUR)
constant 'some template string'
""").definitionMap.size() > 0
    }

    void 'test parse from resource'() {
        given:
        def parser = new GroovyValueDefinition()
        ValueDefinition valueDefinition = parser.parseDefinition('/test-vd.groovy')

        expect:
        7 == valueDefinition.definitionMap.size()

        and: 'check definitions parse from different resource'
        parser.parseDefinition(this.class.getResource('/test-vd.groovy')).definitionMap.size() > 0

        and: 'check value parse from different resource'
        Value.isInstance(parser.parseValue(this.class.getResource('/test-value.groovy')))
        Value.isInstance(parser.parseValue('/test-value.groovy'))
    }

    void 'test parse value type definitions'() {
        given:
        final parser = new GroovyValueDefinition()

        expect:
        Value.isInstance(parser.parseValueText("2.random(1, 100)"))

        parser.parseDefinitionText("definition 2.random(1, 100)").definitionMap.size() > 0
        parser.parseDefinitionText("definition 3L.random(1L, 100L)").definitionMap.size() > 0
        parser.parseDefinitionText("definition new Date().random()").definitionMap.size() > 0
        parser.parseDefinitionText("definition 'some template string'.random(100)").definitionMap.size() > 0

        parser.parseDefinitionText("definition([1, 2, 3].randomOf())").definitionMap.size() > 0

        parser.parseDefinitionText("constant 1.0").definitionMap.size() > 0
        parser.parseDefinitionText("definition 1.increment(100)").definitionMap.size() > 0
        parser.parseDefinitionText("definition 1L.increment(100L)").definitionMap.size() > 0

        parser.parseDefinitionText("definition new Date().increment()").definitionMap.size() > 0
        parser.parseDefinitionText("definition new Date().increment(Calendar.HOUR)").definitionMap.size() > 0
        parser.parseDefinitionText("definition new Date().increment(Calendar.HOUR, 1)").definitionMap.size() > 0

        parser.parseDefinitionText("definition 'some template string'.increment('some%s%d')").definitionMap.size() > 0
        parser.parseDefinitionText("definition 'some template string'.increment('some%s%d', 12)").definitionMap.size() > 0

        parser.parseDefinitionText("definition 1.0.constant()").definitionMap.size() > 0
        parser.parseDefinitionText("definition 1.constant()").definitionMap.size() > 0
        parser.parseDefinitionText("definition 1L.constant()").definitionMap.size() > 0
        parser.parseDefinitionText("definition new Date().constant()").definitionMap.size() > 0
        parser.parseDefinitionText("constant 'some string'").definitionMap.size() > 0
        parser.parseDefinitionText("definition new Object().constant()").definitionMap.size() > 0

        parser.parseDefinitionText("""
definition '*array', int.arrayOf(1.random())
definition 1.random(1, 100)
definition([1, 2, 3].randomOf())
definition new Date().increment(Calendar.HOUR, 1)
definition 'some template string'.constant()
""").definitionMap.size() > 0
    }

    void 'test property value definition'() {
        given:
        final parser = new GroovyValueDefinition()

        expect:
        parser.parseDefinitionText("definition '*Sid', 1.0").definitionMap.size() > 0
        parser.parseDefinitionText("definition '*prop1', 2.random(1, 100)").definitionMap.size() > 0
        parser.parseDefinitionText("definition '*prop2', 3L.random(1L, 100L)").definitionMap.size() > 0
        parser.parseDefinitionText("definition '*prop3', new Date().random()").definitionMap.size() > 0
        parser.parseDefinitionText("definition '*prop4', 'some template string'.random(100)").definitionMap.size() > 0

        parser.parseDefinitionText("definition('*prop5', [1, 2, 3].randomOf())").definitionMap.size() > 0

        parser.parseDefinitionText("definition '*prop6', 1.0.increment()").definitionMap.size() > 0
        parser.parseDefinitionText("definition '*prop7', 1.increment(100)").definitionMap.size() > 0
        parser.parseDefinitionText("definition '*prop8', 1L.increment(100L)").definitionMap.size() > 0

        parser.parseDefinitionText("definition '*prop9', new Date()").definitionMap.size() > 0
        parser.parseDefinitionText("definition new Date().increment(Calendar.HOUR)").definitionMap.size() > 0
        parser.parseDefinitionText("definition new Date().increment(Calendar.HOUR, 1)").definitionMap.size() > 0

        parser.parseDefinitionText("definition '*prop13', 'some template string'.increment('%s;some%d')").definitionMap.size() > 0
        parser.parseDefinitionText("definition '*prop14', 'some template string'.increment('%s;some%d', 12)").definitionMap.size() > 0

        parser.parseDefinitionText("definition '*prop15', 1.0.constant()").definitionMap.size() > 0
        parser.parseDefinitionText("definition '*prop16', 1.constant()").definitionMap.size() > 0
        parser.parseDefinitionText("definition '*prop17', 1L.constant()").definitionMap.size() > 0
        parser.parseDefinitionText("definition '*prop18', new Date().constant()").definitionMap.size() > 0
        parser.parseDefinitionText("definition '*prop19', 'some string'.constant()").definitionMap.size() > 0
        parser.parseDefinitionText("definition '*prop20', new Object().constant()").definitionMap.size() > 0
    }

    void 'test parse text class definitions'() {
        given:
        final parser = new GroovyValueDefinition()

        and:
        final valueDefinition = parser.parseDefinitionText("""
definition org.prismus.scrambler.beans.School.definition {
        definition '*Id', 1
}
""")
        expect:
        valueDefinition.definitionMap.size() > 0

        and:
        parser.parseDefinitionText("""
definition '*Instance|*Object', org.prismus.scrambler.beans.School.definition {
        definition '*Id', 3L.increment
}
""").definitionMap.size() > 0
        and:
        parser.parseDefinitionText("""
definition org.prismus.scrambler.beans.School.definition([2.0.random(), 3], {
        definition '*Id', 1.random()
})
""").definitionMap.size() > 0
    }

    void 'test parse text with parent reference definition'() {
        given:
        final parser = new GroovyValueDefinition()

        and:
        def definition = parser.parseDefinitionText("""
definition 'id', 1.increment(300)
definition org.prismus.scrambler.beans.School.definition {
        reference '*Instance'
}
""")

        expect: 'verify root definition'
        0 < definition.definitionMap.size()

        and:
        parser.parseDefinitionText("reference '*Parent'").definitionMap.size() > 0
    }

    void 'test parse container with value'() {
        given:
        final parser = new GroovyValueDefinition()

        and:
        def valueDefinition = parser.parseDefinitionText("definition 'mumu', [:].of(prop1: 'param'.increment('%s%d', 1), prop2: 1.increment(1))")

        expect:
        valueDefinition.definitionMap.size() > 0

        and:
        0 < parser.parseDefinitionText("definition 'cucu*', [:].of(prop1: 'param'.increment('%s%d', 1), prop2: 1.increment(1))").definitionMap.size()
        0 < parser.parseDefinitionText("definition 'cucu*', [].of('param'.random(10))").definitionMap.size()
    }

    void 'test value definitions DSL from code'() {
        given:
        GroovyValueDefinition.register()

        expect:
        null != 'text'.increment('%s%d')
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
definition('a*':1.constant(), b:2.random(), c:'cucu')
definition('*Sid': Integer.random(1, 100))
""")

        expect:
        valueDefinition.definitionMap.size() > 0
    }

}
