package org.prismus.scrambler.builder

import spock.lang.Specification

/**
 * @author Serge Pruteanu
 */
class ValueDefinitionParserTest extends Specification {

    void 'test parse type text definitions'() {
        given:
        final parser = new ValueDefinitionParser()

        expect:
        parser.parseText("random 1, 100").typeValueMap.size() > 0
        parser.parseText("random 1L, 100L").typeValueMap.size() > 0
        parser.parseText("random new Date()").typeValueMap.size() > 0
        parser.parseText("random('some template string', 100, true, false)").typeValueMap.size() > 0

        parser.parseText("randomOf([1, 2, 3])").typeValueMap.size() > 0

        parser.parseText("of(new PropertyPredicate(pattern: ~/\\w+Sid/), new RandomInteger(1, 100))").propertyValueMap.size() > 0
        parser.parseText("of(new PropertyPredicate('*Sid'), new RandomInteger(1, 100))").propertyValueMap.size() > 0

        parser.parseText("incremental 1.0").typeValueMap.size() > 0
        parser.parseText("incremental 1, 100").typeValueMap.size() > 0
        parser.parseText("incremental 1L, 100L").typeValueMap.size() > 0

        parser.parseText("incremental new Date()").typeValueMap.size() > 0
        parser.parseText("incremental new Date(), 2").typeValueMap.size() > 0
        parser.parseText("incremental new Date(), 1, Calendar.HOUR").typeValueMap.size() > 0

        parser.parseText("incremental('some template string', 4)").typeValueMap.size() > 0
        parser.parseText("incremental('some template string', 'some%d')").typeValueMap.size() > 0
        parser.parseText("incremental('some template string', 'some%d', 12)").typeValueMap.size() > 0

        parser.parseText("constant 1.0").typeValueMap.size() > 0
        parser.parseText("constant 1").typeValueMap.size() > 0
        parser.parseText("constant 1L").typeValueMap.size() > 0
        parser.parseText("constant new Date()").typeValueMap.size() > 0
        parser.parseText("constant 'some template string'").typeValueMap.size() > 0
        parser.parseText("constant new Object()").typeValueMap.size() > 0

        parser.parseText("random(new RandomInteger(1, 100), new ArrayList(1024))").typeValueMap.size() > 0
        parser.parseText("random(new RandomString('some message', 45), new ArrayList(1024), 1024)").typeValueMap.size() > 0

        parser.parseText("""
random 1, 100
randomOf([1, 2, 3])
incremental new Date(), 1, Calendar.HOUR
constant 'some template string'
""").typeValueMap.size() > 0
    }

    void 'test parse from resource'() {
        given:
        def parser = new ValueDefinitionParser()

        expect:
        parser.parse('/test-vd.groovy').typeValueMap.size() > 0
    }

    void 'test parse value type definitions'() {
        given:
        final parser = new ValueDefinitionParser()

        expect:
        parser.parseText("of 2.random(1, 100)").typeValueMap.size() > 0
        parser.parseText("of 3L.random(1L, 100L)").typeValueMap.size() > 0
        parser.parseText("of new Date().random()").typeValueMap.size() > 0
        parser.parseText("of 'some template string'.random(100, true, false)").typeValueMap.size() > 0

        parser.parseText("of([1, 2, 3].randomOf())").typeValueMap.size() > 0

        parser.parseText("of 1.0").typeValueMap.size() > 0
        parser.parseText("of 1.incremental(100)").typeValueMap.size() > 0
        parser.parseText("of 1L.incremental(100L)").typeValueMap.size() > 0

        parser.parseText("of new Date().incremental()").typeValueMap.size() > 0
        parser.parseText("of new Date().incremental(2)").typeValueMap.size() > 0
        parser.parseText("of new Date().incremental(1, Calendar.HOUR)").typeValueMap.size() > 0

        parser.parseText("of 'some template string'.incremental(4)").typeValueMap.size() > 0
        parser.parseText("of 'some template string'.incremental('some%d')").typeValueMap.size() > 0
        parser.parseText("of 'some template string'.incremental('some%d', 12)").typeValueMap.size() > 0

        parser.parseText("of 1.0.constant()").typeValueMap.size() > 0
        parser.parseText("of 1.constant()").typeValueMap.size() > 0
        parser.parseText("of 1L.constant()").typeValueMap.size() > 0
        parser.parseText("of new Date().constant()").typeValueMap.size() > 0
        parser.parseText("of 'some string'").typeValueMap.size() > 0
        parser.parseText("of new Object().constant()").typeValueMap.size() > 0

        parser.parseText("""
of 1.random(1, 100)
of([1, 2, 3].randomOf())
of new Date().incremental(1, Calendar.HOUR)
of 'some template string'.constant()
""").typeValueMap.size() > 0
    }

    void 'test property value definition'() {
        given:
        final parser = new ValueDefinitionParser()

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

        parser.parseText("of '*prop12', 'some template string'.incremental(4)").propertyValueMap.size() > 0
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
        final parser = new ValueDefinitionParser()

        and:
        final valueDefinition = parser.parseText("""
of org.prismus.scrambler.builder.Instance.of {
        of '*Sid', 1
}
""")
        expect:
        valueDefinition.typeValueMap.size() > 0
        valueDefinition.instanceValues.size() > 0
        valueDefinition.instanceValues[0].definition != null
        valueDefinition.instanceValues[0].definition.propertyValueMap.size() > 0

        and:
        parser.parseText("""
of '*Instance|*Object', org.prismus.scrambler.builder.Instance.of {
        of '*Sid', 3L.incremental()
}
""").propertyValueMap.size() > 0
        and:
        parser.parseText("""
of org.prismus.scrambler.builder.Instance.of([2.0.random(), 3], {
        of '*Sid', 1.random()
})
""").typeValueMap.size() > 0
    }

    void 'test parse text with parent reference definition'() {
        given:
        final parser = new ValueDefinitionParser()

        and:
        def rootDefinition = parser.parseText("""
of 'id', 1.incremental(300)
of org.prismus.scrambler.builder.Instance.of {
        parent '*Instance', 'id'
}
""")

        expect: 'verify root definition'
        0 < rootDefinition.typeValueMap.size()
        null != rootDefinition.instanceValues[0].definition
        null == rootDefinition.parent // root definition

        and: 'verify that parent of inner definition is root one'
        rootDefinition == rootDefinition.instanceValues[0].definition.parent

        and: 'verify ParentValue variables'
        rootDefinition == rootDefinition.instanceValues[0].definition.propertyValueMap.values()[0].parent
        null != rootDefinition.instanceValues[0].definition.propertyValueMap.values()[0].predicate

        and:
        parser.parseText("parent '*Parent'").propertyValueMap.size() > 0
    }

}
