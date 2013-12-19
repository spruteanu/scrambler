package org.prismus.scrambler.builder;

import groovy.lang.GroovyShell;

/**
 * todo: add description
 *
 * @author Serge Pruteanu
 */
public class DefinitionParser {

    public void parse(String definitionText) {
        final GroovyShell shell = new GroovyShell();
        shell.evaluate(definitionText);
    }

}
