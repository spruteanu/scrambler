package org.prismus.scrambler.builder

import spock.lang.Specification

/**
 * @author Serge Pruteanu
 */
class ValueDefinitionParserTest extends Specification {

    void test_parseText() {
        given:
        final parser = new ValueDefinitionParser()

        expect:
        parser.parseText("random 1, 100").typeValueMap.size() > 0
        parser.parseText("random 1L, 100L").typeValueMap.size() > 0
        parser.parseText("random new Date()").typeValueMap.size() > 0
        parser.parseText("random('some template string', 100, true, false)").typeValueMap.size() > 0

        parser.parseText("randomOf([1, 2, 3])").typeValueMap.size() > 0

        parser.parseText("of(new RegexPredicate(pattern: ~/\\w+Sid/), new RandomInteger(1, 100))").typeValueMap.size() > 0
        parser.parseText("of(new RegexPredicate('*Sid'), new RandomInteger(1, 100))").typeValueMap.size() > 0

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

        parser.parseText("of 1.0.incremental()").typeValueMap.size() > 0
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
        parser.parseText("of 'some string'.constant()").typeValueMap.size() > 0
        parser.parseText("of new Object().constant()").typeValueMap.size() > 0

        parser.parseText("""
of 1.random(1, 100)
of([1, 2, 3].randomOf())
of new Date().incremental(1, Calendar.HOUR)
of 'some template string'.constant()
""").typeValueMap.size() > 0
    }
}
