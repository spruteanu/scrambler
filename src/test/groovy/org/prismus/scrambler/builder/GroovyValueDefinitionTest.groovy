package org.prismus.scrambler.builder

import spock.lang.Specification

/**
 * @author Serge Pruteanu
 */
class GroovyValueDefinitionTest extends Specification {

    void 'test parse type text definitions'() {
        given:
        final parser = new GroovyValueDefinition()

        expect:
        parser.parseText("random 1, 100").propertyValueMap.size() > 0
        parser.parseText("random 1L, 100L").propertyValueMap.size() > 0
        parser.parseText("random new Date()").propertyValueMap.size() > 0
        parser.parseText("random('some template string', 100, true, false)").propertyValueMap.size() > 0

        parser.parseText("randomOf([1, 2, 3])").propertyValueMap.size() > 0

        parser.parseText("of(new PropertyPredicate(pattern: ~/\\w+Sid/), new RandomInteger(1, 100))").propertyValueMap.size() > 0
        parser.parseText("of(new PropertyPredicate('*Sid'), new RandomInteger(1, 100))").propertyValueMap.size() > 0

        parser.parseText("incremental 1.0").propertyValueMap.size() > 0
        parser.parseText("incremental 1, 100").propertyValueMap.size() > 0
        parser.parseText("incremental 1L, 100L").propertyValueMap.size() > 0

        parser.parseText("incremental new Date()").propertyValueMap.size() > 0
        parser.parseText("incremental new Date(), 2").propertyValueMap.size() > 0
        parser.parseText("incremental new Date(), 1, Calendar.HOUR").propertyValueMap.size() > 0

        parser.parseText("incremental('some template string', 4)").propertyValueMap.size() > 0
        parser.parseText("incremental('some template string', 'some%d')").propertyValueMap.size() > 0
        parser.parseText("incremental('some template string', 'some%d', 12)").propertyValueMap.size() > 0

        parser.parseText("constant 1.0").propertyValueMap.size() > 0
        parser.parseText("constant 1").propertyValueMap.size() > 0
        parser.parseText("constant 1L").propertyValueMap.size() > 0
        parser.parseText("constant new Date()").propertyValueMap.size() > 0
        parser.parseText("constant 'some template string'").propertyValueMap.size() > 0
        parser.parseText("constant new Object()").propertyValueMap.size() > 0

        parser.parseText("random(new ArrayList(1024), new RandomInteger(1, 100))").propertyValueMap.size() > 0
        parser.parseText("random(new ArrayList(1024), new RandomString('some message', 45), 1024)").propertyValueMap.size() > 0

        parser.parseText("""
random 1, 100
randomOf([1, 2, 3])
incremental new Date(), 1, Calendar.HOUR
constant 'some template string'
""").propertyValueMap.size() > 0
    }

    void 'test parse from resource'() {
        given:
        def parser = new GroovyValueDefinition()
        final valueDefinition = parser.parse('/test-vd.groovy')
        expect:
        valueDefinition.propertyValueMap.size() > 0
    }

    void 'test parse value type definitions'() {
        given:
        final parser = new GroovyValueDefinition()

        expect:
        parser.parseText("of 2.random(1, 100)").propertyValueMap.size() > 0
        parser.parseText("of 3L.random(1L, 100L)").propertyValueMap.size() > 0
        parser.parseText("of new Date().random()").propertyValueMap.size() > 0
        parser.parseText("of 'some template string'.random(100, true, false)").propertyValueMap.size() > 0

        parser.parseText("of([1, 2, 3].randomOf())").propertyValueMap.size() > 0

        parser.parseText("of 1.0").propertyValueMap.size() > 0
        parser.parseText("of 1.incremental(100)").propertyValueMap.size() > 0
        parser.parseText("of 1L.incremental(100L)").propertyValueMap.size() > 0

        parser.parseText("of new Date().incremental()").propertyValueMap.size() > 0
        parser.parseText("of new Date().incremental(2)").propertyValueMap.size() > 0
        parser.parseText("of new Date().incremental(1, Calendar.HOUR)").propertyValueMap.size() > 0

        parser.parseText("of 'some template string'.incremental('some%d')").propertyValueMap.size() > 0
        parser.parseText("of 'some template string'.incremental('some%d', 12)").propertyValueMap.size() > 0

        parser.parseText("of 1.0.constant()").propertyValueMap.size() > 0
        parser.parseText("of 1.constant()").propertyValueMap.size() > 0
        parser.parseText("of 1L.constant()").propertyValueMap.size() > 0
        parser.parseText("of new Date().constant()").propertyValueMap.size() > 0
        parser.parseText("of 'some string'").propertyValueMap.size() > 0
        parser.parseText("of new Object().constant()").propertyValueMap.size() > 0

        parser.parseText("""
of '*array', int.array(1.random())
of 1.random(1, 100)
of([1, 2, 3].randomOf())
of new Date().incremental(1, Calendar.HOUR)
of 'some template string'.constant()
""").propertyValueMap.size() > 0
    }

    void 'test property value definition'() {
        given:
        final parser = new GroovyValueDefinition()

        expect:
        parser.parseText("of '*Sid', 1.0").propertyValueMap.size() > 0
        parser.parseText("of '*prop1', 2.random(1, 100)").propertyValueMap.size() > 0
        parser.parseText("of '*prop2', 3L.random(1L, 100L)").propertyValueMap.size() > 0
        parser.parseText("of '*prop3', new Date().random()").propertyValueMap.size() > 0
        parser.parseText("of '*prop4', 'some template string'.random(100, true, false)").propertyValueMap.size() > 0

        parser.parseText("of('*prop5', [1, 2, 3].randomOf())").propertyValueMap.size() > 0

        parser.parseText("of '*prop6', 1.0.incremental()").propertyValueMap.size() > 0
        parser.parseText("of '*prop7', 1.incremental(100)").propertyValueMap.size() > 0
        parser.parseText("of '*prop8', 1L.incremental(100L)").propertyValueMap.size() > 0

        parser.parseText("of '*prop9', new Date()").propertyValueMap.size() > 0
        parser.parseText("of '*prop10', new Date().incremental(2)").propertyValueMap.size() > 0
        parser.parseText("of '*prop11', new Date().incremental(1, Calendar.HOUR)").propertyValueMap.size() > 0

        parser.parseText("of '*prop13', 'some template string'.incremental('some%d')").propertyValueMap.size() > 0
        parser.parseText("of '*prop14', 'some template string'.incremental('some%d', 12)").propertyValueMap.size() > 0

        parser.parseText("of '*prop15', 1.0.constant()").propertyValueMap.size() > 0
        parser.parseText("of '*prop16', 1.constant()").propertyValueMap.size() > 0
        parser.parseText("of '*prop17', 1L.constant()").propertyValueMap.size() > 0
        parser.parseText("of '*prop18', new Date().constant()").propertyValueMap.size() > 0
        parser.parseText("of '*prop19', 'some string'.constant()").propertyValueMap.size() > 0
        parser.parseText("of '*prop20', new Object().constant()").propertyValueMap.size() > 0
    }

    void 'test parse text class definitions'() {
        given:
        final parser = new GroovyValueDefinition()

        and:
        final valueDefinition = parser.parseText("""
of org.prismus.scrambler.beans.School.of {
        of '*Id', 1
}
""")
        expect:
        valueDefinition.propertyValueMap.size() > 0

        and:
        parser.parseText("""
of '*Instance|*Object', org.prismus.scrambler.beans.School.of {
        of '*Id', 3L.incremental()
}
""").propertyValueMap.size() > 0
        and:
        parser.parseText("""
of org.prismus.scrambler.beans.School.of([2.0.random(), 3], {
        of '*Id', 1.random()
})
""").propertyValueMap.size() > 0
    }

    void 'test parse text with parent reference definition'() {
        given:
        final parser = new GroovyValueDefinition()

        and:
        def definition = parser.parseText("""
of 'id', 1.incremental(300)
of org.prismus.scrambler.beans.School.of {
        parent '*Instance'
}
""")

        expect: 'verify root definition'
        0 < definition.propertyValueMap.size()

        and:
        parser.parseText("parent '*Parent'").propertyValueMap.size() > 0
    }

    void 'test parse container with value'() {
        given:
        final parser = new GroovyValueDefinition()

        and:
        def valueDefinition = parser.parseText("of'mumu', [:].of('param'.incremental(null, 1), 1.incremental(1))")

        expect:
        valueDefinition.propertyValueMap.size() > 0

        and:
        0 < parser.parseText("of 'cucu*', [:].of('param'.random(10), 1.incremental(1))").propertyValueMap.size()
        0 < parser.parseText("of 'cucu*', [].of('param'.random(10))").propertyValueMap.size()
    }

    void 'test value definitions DSL from code'() {
        given:
        GroovyValueDefinition.register()

        expect:
        null != 'text'.incremental()
        null != 1.random(1, 100)
        null != [1, 2, 3].randomOf()
        null != new Date().incremental(1, Calendar.HOUR)
        null != 'some template string'.constant()

        null != 2.random(1, 100)
        null != 3L.random(1L, 100L)
        null != new Date().random()
        null != int.array(1.random())
        null != 'some template string'.random(100, true, false)
    }

    void 'test parse text for map'() {
        given:
        final parser = new GroovyValueDefinition()

        and:
        def valueDefinition = parser.parseText("of('a*':1.constant(), b:2.random(), c:'cucu')")

        expect:
        valueDefinition.propertyValueMap.size() > 0
    }

}
