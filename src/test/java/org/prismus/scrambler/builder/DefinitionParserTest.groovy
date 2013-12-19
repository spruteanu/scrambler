package org.prismus.scrambler.builder

import spock.lang.Specification

/**
 * @author Serge Pruteanu
 */
class DefinitionParserTest extends Specification {

    void test_parse() {
        final shell = new GroovyShell()
        expect:
        shell != null
    }

    def method() { println 'cucu' }
}
