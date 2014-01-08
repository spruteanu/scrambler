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

        parser.parseText("random([1, 2, 3])").typeValueMap.size() > 0

//        parser.parseText("RandomInteger 1, 100").typeValueMap.size() > 0 // todo Serge: ??? should I support this syntax???
//        parser.parseText("RandomLong 1, 100").typeValueMap.size() > 0
//        parser.parseText("RandomString('some template string', 100, true, false)").typeValueMap.size() > 0

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

        parser.parseText("collection(new RandomInteger(1, 100), new ArrayList(1024))").typeValueMap.size() > 0
        parser.parseText("collection(new RandomString('some message', 45), new ArrayList(1024), 1024)").typeValueMap.size() > 0

        parser.parseText("""
random 1, 100
random([1, 2, 3])
incremental new Date(), 1, Calendar.HOUR
constant 'some template string'
""").typeValueMap.size() > 0
    }

    void 'test parse'() {
        given:
        def parser = new ValueDefinitionParser()

        expect:
        parser.parse('/test-vd.groovy').typeValueMap.size() > 0
    }

}
