package org.prismus.scrambler.builder

import spock.lang.Specification

/**
 * @author Serge Pruteanu
 */
class ValueDefinitionParserTest extends Specification {

    void test_parse() {
        given:
        final parser = new ValueDefinitionParser()

        expect:
        parser != null

        parser.parse("random 1, 100") != null
        parser.parse("random 1L, 100L") != null
        parser.parse("random new Date()") != null
        parser.parse("random('some template string', 100, true, false)") != null

        parser.parse("random([1, 2, 3])") != null

        parser.parse("RandomInteger 1, 100") != null // todo Serge: ??? should I support this syntax???
        parser.parse("RandomLong 1, 100") != null
        parser.parse("RandomString('some template string', 100, true, false)") != null

        parser.parse("new RandomInteger(1, 100)") != null
        parser.parse("new RandomLong(1, 100)") != null
        parser.parse("new RandomString('some template string', 100, true, false)") != null

        parser.parse("incremental 1.0") != null
        parser.parse("incremental 1, 100") != null
        parser.parse("incremental 1L, 100L") != null

        parser.parse("incremental new Date()") != null
        parser.parse("incremental new Date(), 2") != null
        parser.parse("incremental new Date(), 1, Calendar.HOUR") != null

        parser.parse("incremental('some template string', 4)") != null
        parser.parse("incremental('some template string', 'some%d')") != null
        parser.parse("incremental('some template string', 'some%d', 12)") != null

        parser.parse("constant 1.0") != null
        parser.parse("constant 1") != null
        parser.parse("constant 1L") != null
        parser.parse("constant new Date()") != null
        parser.parse("constant 'some template string'") != null
        parser.parse("constant new Object()") != null

        parser.parse("collection(new RandomInteger(1, 100), new ArrayList(1024))") != null
        parser.parse("collection(new RandomString('some message', 45), new ArrayList(1024), 1024)") != null
    }

}
