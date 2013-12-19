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
        parser.parse("'test*' random 1L 100L") != null
    }

}
