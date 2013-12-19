package org.prismus.scrambler.builder

import groovy.transform.CompileStatic

/**
 * todo: add description
 *
 * @author Serge Pruteanu
 */
@CompileStatic
class Definition extends Script {
    Map<String, Object> definitionMap = [:]

    @Override
    Object run() {
        return this
    }

    def propertyMissing(String name, def args) {
        // todo Serge: implement referencing
        return this;
    }

    def methodMissing(String name, def args) {
        definitionMap.put(name, args)
        return this;
    }

}
