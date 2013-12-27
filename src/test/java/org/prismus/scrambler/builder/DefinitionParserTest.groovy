package org.prismus.scrambler.builder

import spock.lang.Specification

/**
 * @author Serge Pruteanu
 */
class DefinitionParserTest extends Specification {

    void test_parse() {
        final parser = new DefinitionParser()
        expect:
        parser != null
//        parser.parse("random 'test*', 1L, 100L") != null
        parser.parse("random 1, 100") != null

        parser.parse("random 1L, 100L") != null
        parser.parse("random new Date()") != null
        parser.parse("random('some template string', 100, true, false)") != null

        parser.parse("RandomInteger 1, 100") != null
        parser.parse("RandomLong 1, 100") != null
        parser.parse("RandomString('some template string', 100, true, false)") != null
    }

}
